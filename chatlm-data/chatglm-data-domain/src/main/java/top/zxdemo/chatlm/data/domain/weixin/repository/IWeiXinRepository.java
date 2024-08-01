package top.zxdemo.chatlm.data.domain.weixin.repository;

/**
 * @author ZhangXing zxdemo.top
 * @description 微信服务仓储
 */
public interface IWeiXinRepository {

    String genCode(String openId);

}
