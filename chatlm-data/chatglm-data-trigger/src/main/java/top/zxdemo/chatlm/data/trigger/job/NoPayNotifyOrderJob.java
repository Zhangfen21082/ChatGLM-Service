package top.zxdemo.chatlm.data.trigger.job;

import cn.bugstack.chatgpt.data.domain.order.service.IOrderService;
import cn.bugstack.ltzf.payments.nativepay.model.QueryOrderByOutTradeNoResponse;
import com.google.common.eventbus.EventBus;
import com.wechat.pay.java.service.payments.model.Transaction;
// import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import cn.bugstack.ltzf.payments.nativepay.NativePayService;
// import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import cn.bugstack.ltzf.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author ZhangXing zxdemo.top
 * @description 检测未接收到或未正确处理的支付回调通知
 */
@Slf4j
@Component()
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;
    @Autowired(required = false)
    private NativePayService payService;
    @Resource
    private EventBus eventBus;

    @Value("${ltzf.sdk.config.merchant_id}")
    private String mchid;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Timed(value = "no_pay_notify_order_job", description = "定时任务，订单支付状态更新")
    @Scheduled(cron = "0 0/1 * * * ?")
    //@Scheduled(cron = "*/10 * * * * ?")
    public void exec() {
        try {
            if (null == payService) {
                log.info("定时任务，订单支付状态更新。应用未配置支付渠道，任务不执行。");
                return;
            }
            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，订单支付状态更新，暂无未更新订单 orderId is null");
                return;
            }
            for (String orderId : orderIds) {
                // 查询结果
                log.info("开始处理", orderId);
                System.out.println(orderId);
                QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
                request.setMchid(mchid);
                request.setOutTradeNo(orderId);
                QueryOrderByOutTradeNoResponse response = payService.queryOrderByOutTradeNo(request);
                if (response.getCode() == 0) {
                    log.info("定时任务，订单支付状态更新，当前订单未支付 orderId is {}", orderId);
                    continue;
                }

                String transactionId = response.getData().getPayNo();
                BigDecimal total = new BigDecimal(response.getData().getTotalFee());
                String successTime = response.getData().getSuccessTime();
                System.out.println(successTime);
                // 更新订单
                boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, total, dateFormat.parse(successTime));
                if (isSuccess) {
                    // 发布消息
                    eventBus.post(orderId);
                }
            }
        } catch (Exception e) {
            log.error("定时任务，订单支付状态更新失败", e);
        }
    }

}
