package top.zxdemo.chatlm.data.domain.order.repository;

import top.zxdemo.chatlm.data.domain.order.model.aggregates.CreateOrderAggregate;
import top.zxdemo.chatlm.data.domain.order.model.entity.PayOrderEntity;
import top.zxdemo.chatlm.data.domain.order.model.entity.ProductEntity;
import top.zxdemo.chatlm.data.domain.order.model.entity.ShopCartEntity;
import top.zxdemo.chatlm.data.domain.order.model.entity.UnpaidOrderEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangXing zxdemo.top
 * @description 订单仓储接口
 */
public interface IOrderRepository {

    // 查询未支付的订单
    UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity);

    // 查询商品
    ProductEntity queryProduct(Integer productId);

    // 保存订购
    void saveOrder(CreateOrderAggregate aggregate);

    // 更新支付信息
    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime);

    void deliverGoods(String orderId);

    CreateOrderAggregate queryOrder(String orderId);

    List<String> queryReplenishmentOrder();

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    List<ProductEntity> queryProductList();

}
