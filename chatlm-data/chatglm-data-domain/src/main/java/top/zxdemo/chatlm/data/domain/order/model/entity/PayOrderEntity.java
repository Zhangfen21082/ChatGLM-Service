package top.zxdemo.chatlm.data.domain.order.model.entity;

import top.zxdemo.chatlm.data.domain.order.model.valobj.PayStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZhangXing zxdemo.top
 * @description 支付单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderEntity {

    /**
     * 用户ID
     */
    private String openid;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 支付地址；创建支付后，获得的URL地址
     */
    private String payUrl;
    /**
     * 支付状态；0-等待支付、1-支付完成、2-支付失败、3-放弃支付
     */
    private PayStatusVO payStatus;

    @Override
    public String toString() {
        return "PayOrderEntity{" +
                "openid='" + openid + '\'' +
                ", orderId='" + orderId + '\'' +
                ", payUrl='" + payUrl + '\'' +
                ", payStatus=" + payStatus.getCode() + ": " + payStatus.getDesc() +
                '}';
    }

}
