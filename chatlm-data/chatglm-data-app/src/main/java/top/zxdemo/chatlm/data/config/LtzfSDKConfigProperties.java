package top.zxdemo.chatlm.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 *  @author ZhangXing top.zxdemo
 * @description 读取属性
 */

@Data
@ConfigurationProperties(prefix = "ltzf.sdk.config", ignoreInvalidFields = true)
public class LtzfSDKConfigProperties {

    /** 状态；open = 开启、close 关闭 */
    private boolean enable;
    /** 开发者ID */
    private String appId;
    /** 商户号ID */
    private String merchantId;
    /** 商户秘钥 */
    private String partnerKey;

}
