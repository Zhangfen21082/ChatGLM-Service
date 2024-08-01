package top.zxdemo.chatlm.data.domain.weixin.service;

import top.zxdemo.chatlm.data.domain.weixin.model.entity.UserBehaviorMessageEntity;

/**
 * @author ZhangXing zxdemo.top
 * @description 受理用户行为接口
 */
public interface IWeiXinBehaviorService {

    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);

}
