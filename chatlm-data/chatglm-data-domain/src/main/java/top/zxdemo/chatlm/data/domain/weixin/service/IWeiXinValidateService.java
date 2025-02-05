package top.zxdemo.chatlm.data.domain.weixin.service;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 验签接口
 * @create 2023-08-05 16:56
 */
public interface IWeiXinValidateService {

    boolean checkSign(String signature, String timestamp, String nonce);

}
