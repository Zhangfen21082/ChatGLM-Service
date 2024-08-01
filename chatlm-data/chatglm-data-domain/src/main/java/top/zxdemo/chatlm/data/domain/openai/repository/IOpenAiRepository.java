package top.zxdemo.chatlm.data.domain.openai.repository;

import top.zxdemo.chatlm.data.domain.openai.model.entity.UserAccountQuotaEntity;

/**
 * @author ZhangXing zxdemo.top
 * @description OpenAi 仓储接口
 */
public interface IOpenAiRepository {

    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);

}
