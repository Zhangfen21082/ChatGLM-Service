package top.zxdemo.chatlm.data.domain.auth.repository;

/**
 * @author ZhangXing zxdemo.top
 * @description 认证仓储服务
 */
public interface IAuthRepository {

    String getCodeUserOpenId(String code);

    void removeCodeByOpenId(String code, String openId);

}
