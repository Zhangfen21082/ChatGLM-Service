package top.zxdemo.chatlm.data.infrastructure.repository;

import cn.bugstack.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import cn.bugstack.chatgpt.data.domain.order.model.aggregates.CreateOrderAggregate;
import cn.bugstack.chatgpt.data.domain.order.model.entity.*;
import cn.bugstack.chatgpt.data.domain.order.model.valobj.OrderStatusVO;
import cn.bugstack.chatgpt.data.domain.order.model.valobj.PayStatusVO;
import cn.bugstack.chatgpt.data.domain.order.repository.IOrderRepository;
import top.zxdemo.chatlm.data.infrastructure.dao.IOpenAIOrderDao;
import top.zxdemo.chatlm.data.infrastructure.dao.IOpenAIProductDao;
import top.zxdemo.chatlm.data.infrastructure.dao.IUserAccountDao;
import top.zxdemo.chatlm.data.infrastructure.po.OpenAIOrderPO;
import top.zxdemo.chatlm.data.infrastructure.po.OpenAIProductPO;
import top.zxdemo.chatlm.data.infrastructure.po.UserAccountPO;
import cn.bugstack.chatgpt.data.types.enums.OpenAIProductEnableModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangXing zxdemo.top
 * @description 订单仓储服务
 */
@Repository
public class OrderRepository implements IOrderRepository {

    @Resource
    private IOpenAIOrderDao openAIOrderDao;
    @Resource
    private IOpenAIProductDao openAIProductDao;

    @Resource
    private IUserAccountDao userAccountDao;


    @Override
    public UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity) {
        OpenAIOrderPO openAIOrderPOReq = new OpenAIOrderPO();
        openAIOrderPOReq.setOpenid(shopCartEntity.getOpenid());
        openAIOrderPOReq.setProductId(shopCartEntity.getProductId());
        OpenAIOrderPO openAIOrderPORes = openAIOrderDao.queryUnpaidOrder(openAIOrderPOReq);
        if (null == openAIOrderPORes) return null;
        return UnpaidOrderEntity.builder()
                .openid(shopCartEntity.getOpenid())
                .orderId(openAIOrderPORes.getOrderId())
                .productName(openAIOrderPORes.getProductName())
                .totalAmount(openAIOrderPORes.getTotalAmount())
                .payUrl(openAIOrderPORes.getPayUrl())
                .payStatus(PayStatusVO.get(openAIOrderPORes.getPayStatus()))
                .build();
    }

    @Override
    public ProductEntity queryProduct(Integer productId) {
        OpenAIProductPO openAIProductPO = openAIProductDao.queryProductByProductId(productId);
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(openAIProductPO.getProductId());
        productEntity.setProductName(openAIProductPO.getProductName());
        productEntity.setProductDesc(openAIProductPO.getProductDesc());
        productEntity.setQuota(openAIProductPO.getQuota());
        productEntity.setPrice(openAIProductPO.getPrice());
        productEntity.setEnable(OpenAIProductEnableModel.get(openAIProductPO.getIsEnabled()));
        return productEntity;
    }

    @Override
    public void saveOrder(CreateOrderAggregate aggregate) {
        String openid = aggregate.getOpenid();
        ProductEntity product = aggregate.getProduct();
        OrderEntity order = aggregate.getOrder();
        OpenAIOrderPO openAIOrderPO = new OpenAIOrderPO();
        openAIOrderPO.setOpenid(openid);
        openAIOrderPO.setProductId(product.getProductId());
        openAIOrderPO.setProductName(product.getProductName());
        openAIOrderPO.setProductQuota(product.getQuota());
        openAIOrderPO.setOrderId(order.getOrderId());
        openAIOrderPO.setOrderTime(order.getOrderTime());
        openAIOrderPO.setOrderStatus(order.getOrderStatus().getCode());
        openAIOrderPO.setTotalAmount(order.getTotalAmount());
        openAIOrderPO.setPayType(order.getPayTypeVO().getCode());
        openAIOrderPO.setPayStatus(PayStatusVO.WAIT.getCode());
        openAIOrderDao.insert(openAIOrderPO);
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        OpenAIOrderPO openAIOrderPO = new OpenAIOrderPO();
        openAIOrderPO.setOpenid(payOrderEntity.getOpenid());
        openAIOrderPO.setOrderId(payOrderEntity.getOrderId());
        openAIOrderPO.setPayUrl(payOrderEntity.getPayUrl());
        openAIOrderPO.setPayStatus(payOrderEntity.getPayStatus().getCode());
        openAIOrderDao.updateOrderPayInfo(openAIOrderPO);
    }

    public boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime) {
        OpenAIOrderPO openAIOrderPO = new OpenAIOrderPO();
        openAIOrderPO.setOrderId(orderId);
        openAIOrderPO.setPayAmount(totalAmount);
        openAIOrderPO.setPayTime(payTime);
        openAIOrderPO.setTransactionId(transactionId);
        int count = openAIOrderDao.changeOrderPaySuccess(openAIOrderPO);
        System.out.println("你好");
        System.out.println(count==1);
        return count == 1;

    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 350, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void deliverGoods(String orderId) {
        OpenAIOrderPO openAIOrderPO = openAIOrderDao.queryOrder(orderId);

        // 1. 变更发货状态
        int updateOrderStatusDeliverGoodsCount = openAIOrderDao.updateOrderStatusDeliverGoods(orderId);
        if (1 != updateOrderStatusDeliverGoodsCount) throw new RuntimeException("updateOrderStatusDeliverGoodsCount update count is not equal 1");

        // 2. 账户额度变更
        UserAccountPO userAccountPO = userAccountDao.queryUserAccount(openAIOrderPO.getOpenid());
        UserAccountPO userAccountPOReq = new UserAccountPO();
        userAccountPOReq.setOpenid(openAIOrderPO.getOpenid());
        userAccountPOReq.setTotalQuota(openAIOrderPO.getProductQuota());
        userAccountPOReq.setSurplusQuota(openAIOrderPO.getProductQuota());
        if (null != userAccountPO){
            int addAccountQuotaCount = userAccountDao.addAccountQuota(userAccountPOReq);
            if (1 != addAccountQuotaCount) throw new RuntimeException("addAccountQuotaCount update count is not equal 1");
        } else {
            userAccountPOReq.setStatus(UserAccountStatusVO.AVAILABLE.getCode());
            userAccountPOReq.setModelTypes("glm-3-turbo,glm-4v,gpt-4,gpt-4-32k");
            userAccountDao.insert(userAccountPOReq);
        }
    }

    @Override
    public CreateOrderAggregate queryOrder(String orderId) {
        OpenAIOrderPO openAIOrderPO = openAIOrderDao.queryOrder(orderId);

        ProductEntity product = new ProductEntity();
        product.setProductId(openAIOrderPO.getProductId());
        product.setProductName(openAIOrderPO.getProductName());

        OrderEntity order = new OrderEntity();
        order.setOrderId(openAIOrderPO.getOrderId());
        order.setOrderTime(openAIOrderPO.getOrderTime());
        order.setOrderStatus(OrderStatusVO.get(openAIOrderPO.getOrderStatus()));
        order.setTotalAmount(openAIOrderPO.getTotalAmount());

        CreateOrderAggregate createOrderAggregate = new CreateOrderAggregate();
        createOrderAggregate.setOpenid(openAIOrderPO.getOpenid());
        createOrderAggregate.setOrder(order);
        createOrderAggregate.setProduct(product);

        return createOrderAggregate;
    }

    @Override
    public List<String> queryReplenishmentOrder() {
        return openAIOrderDao.queryReplenishmentOrder();
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return openAIOrderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return openAIOrderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return openAIOrderDao.changeOrderClose(orderId);
    }

    @Override
    public List<ProductEntity> queryProductList() {
        List<OpenAIProductPO> openAIProductPOList =  openAIProductDao.queryProductList();
        List<ProductEntity> productEntityList = new ArrayList<>(openAIProductPOList.size());
        for (OpenAIProductPO openAIProductPO : openAIProductPOList) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductId(openAIProductPO.getProductId());
            productEntity.setProductName(openAIProductPO.getProductName());
            productEntity.setProductDesc(openAIProductPO.getProductDesc());
            productEntity.setQuota(openAIProductPO.getQuota());
            productEntity.setPrice(openAIProductPO.getPrice());
            productEntityList.add(productEntity);
        }
        return productEntityList;
    }

}
