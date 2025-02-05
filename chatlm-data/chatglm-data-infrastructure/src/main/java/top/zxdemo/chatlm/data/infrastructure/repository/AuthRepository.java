package top.zxdemo.chatlm.data.infrastructure.repository;

import cn.bugstack.chatgpt.data.domain.auth.repository.IAuthRepository;
import top.zxdemo.chatlm.data.infrastructure.redis.IRedisService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author ZhangXing zxdemo.top
 * @description 认证仓储服务
 */
@Repository
public class AuthRepository implements IAuthRepository {

    private static final String Key = "weixin_code";

    @Resource
    private IRedisService redisService;

    @Override
    public String getCodeUserOpenId(String code) {
        return redisService.getValue(Key + "_" + code);
    }

    @Override
    public void removeCodeByOpenId(String code, String openId) {
        redisService.remove(Key + "_" + code);
        redisService.remove(Key + "_" + openId);
    }

}
