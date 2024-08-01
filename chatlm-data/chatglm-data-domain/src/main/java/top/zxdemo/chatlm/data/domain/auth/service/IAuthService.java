package top.zxdemo.chatlm.data.domain.auth.service;

import top.zxdemo.chatlm.data.domain.auth.model.entity.AuthStateEntity;

/**
 * @author ZhangXing zxdemo.top
 * @description 鉴权验证服务接口
 */
public interface IAuthService {

    /**
     * 登录验证
     * @param code 验证码
     * @return Token
     */
    AuthStateEntity doLogin(String code);

    boolean checkToken(String token);

    String openid(String token);
}
