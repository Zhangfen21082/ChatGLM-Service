package top.zxdemo.chatlm.data.domain.weixin.service.validate;

import top.zxdemo.chatlm.data.domain.weixin.service.IWeiXinValidateService;
import cn.bugstack.chatgpt.data.types.sdk.weixin.SignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 验签接口实现
 * @create 2023-08-05 16:57
 */
@Service
public class WeiXinValidateService implements IWeiXinValidateService {

    @Value("${wx.config.token}")
    private String token;

    @Override
    public boolean checkSign(String signature, String timestamp, String nonce) {
        return SignatureUtil.check(token, signature, timestamp, nonce);
    }

}
