package top.zxdemo.chatlm.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *  @author ZhangXing top.zxdemo
 * @description 读取属性
 */
@Data
@ConfigurationProperties(prefix = "chatglm.sdk.config", ignoreInvalidFields = true)
public class ChatGLMSDKConfigProperties {

    /** 状态；open = 开启、close 关闭 */
    private boolean enable;
    /** 转发地址 */
    private String apiHost;
    /** 可以申请 sk-*** */
    private String apiSecretKey;

}

