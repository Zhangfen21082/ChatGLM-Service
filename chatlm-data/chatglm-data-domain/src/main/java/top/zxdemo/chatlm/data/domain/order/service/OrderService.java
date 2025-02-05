package top.zxdemo.chatlm.data.domain.order.service;

import top.zxdemo.chatlm.data.domain.order.model.aggregates.CreateOrderAggregate;
import top.zxdemo.chatlm.data.domain.order.model.entity.OrderEntity;
import top.zxdemo.chatlm.data.domain.order.model.entity.PayOrderEntity;
import top.zxdemo.chatlm.data.domain.order.model.entity.ProductEntity;
import top.zxdemo.chatlm.data.domain.order.model.valobj.OrderStatusVO;
import top.zxdemo.chatlm.data.domain.order.model.valobj.PayStatusVO;
import top.zxdemo.chatlm.data.domain.order.model.valobj.PayTypeVO;
// import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import cn.bugstack.ltzf.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
// import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import cn.bugstack.ltzf.payments.nativepay.model.PrepayRequest;
// import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import cn.bugstack.ltzf.payments.nativepay.model.PrepayResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangXing zxdemo.top
 * @description 订单服务
 */
@Service
public class OrderService extends AbstractOrderService {

/*    @Value("${wxpay.config.appid}")
    private String appid;*/
    @Value("${ltzf.sdk.config.merchant_id}")
    private String mchid;
    @Value("${ltzf.sdk.config.notify-url}")
    private String notifyUrl;
    @Autowired(required = false)
    private NativePayService payService;

    @Override
    protected OrderEntity doSaveOrder(String openid, ProductEntity productEntity) {
        OrderEntity orderEntity = new OrderEntity();
        // 数据库有幂等拦截，如果有重复的订单ID会报错主键冲突。如果是公司里一般会有专门的雪花算法UUID服务
        orderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        orderEntity.setOrderTime(new Date());
        orderEntity.setOrderStatus(OrderStatusVO.CREATE);
        orderEntity.setTotalAmount(productEntity.getPrice());
        orderEntity.setPayTypeVO(PayTypeVO.WEIXIN_NATIVE);

        // 聚合信息
        CreateOrderAggregate aggregate = CreateOrderAggregate.builder()
                .openid(openid)
                .product(productEntity)
                .order(orderEntity)
                .build();
        // 保存订单；订单和支付，是2个操作。
        // 一个是数据库操作，一个是HTTP操作。所以不能一个事务处理，只能先保存订单再操作创建支付单，如果失败则需要任务补偿
        orderRepository.saveOrder(aggregate);
        return orderEntity;
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String openid, String orderId, String productName, BigDecimal amountTotal) throws Exception {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(amountTotal.multiply(new BigDecimal(100)).intValue());
        // request.setAmount(amount);
        // request.setAppid(appid);
        request.setMchid(mchid);
        request.setBody(productName);
        // request.setDescription(productName);
        request.setNotifyUrl(notifyUrl);
        request.setOutTradeNo(orderId);
        request.setTotalFee(String.valueOf(amountTotal));

        // 创建微信支付单，如果你有多种支付方式，则可以根据支付类型的策略模式进行创建支付单
        String codeUrl = "";
        if (null != payService) {
            PrepayResponse prepay = payService.prepay(request);
            codeUrl = prepay.getData().getCodeUrl();
        } else {
            codeUrl = "因未配置支付渠道，所以暂时不能生成支付URL";
        }

        PayOrderEntity payOrderEntity = PayOrderEntity.builder()
                .openid(openid)
                .orderId(orderId)
                .payUrl(codeUrl)
                .payStatus(PayStatusVO.WAIT)
                .build();

        // 更新订单支付信息
        orderRepository.updateOrderPayInfo(payOrderEntity);
        return payOrderEntity;
    }

    @Override
    public boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime) {
        return orderRepository.changeOrderPaySuccess(orderId, transactionId, totalAmount, payTime);
    }

    @Override
    public CreateOrderAggregate queryOrder(String orderId) {
        return orderRepository.queryOrder(orderId);
    }

    @Override
    public void deliverGoods(String orderId) {
        orderRepository.deliverGoods(orderId);
    }

    @Override
    public List<String> queryReplenishmentOrder() {
        return orderRepository.queryReplenishmentOrder();
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderRepository.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderRepository.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderRepository.changeOrderClose(orderId);
    }

    @Override
    public List<ProductEntity> queryProductList() {
        return orderRepository.queryProductList();
    }

}
