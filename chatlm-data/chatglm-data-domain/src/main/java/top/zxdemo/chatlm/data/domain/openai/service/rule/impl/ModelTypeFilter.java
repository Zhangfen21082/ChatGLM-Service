package top.zxdemo.chatlm.data.domain.openai.service.rule.impl;

import top.zxdemo.chatlm.data.domain.openai.annotation.LogicStrategy;
import top.zxdemo.chatlm.data.domain.openai.model.aggregates.ChatProcessAggregate;
import top.zxdemo.chatlm.data.domain.openai.model.entity.RuleLogicEntity;
import top.zxdemo.chatlm.data.domain.openai.model.entity.UserAccountQuotaEntity;
import top.zxdemo.chatlm.data.domain.openai.model.valobj.LogicCheckTypeVO;
import top.zxdemo.chatlm.data.domain.openai.service.rule.ILogicFilter;
import top.zxdemo.chatlm.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 允许访问的模型过滤
 * @create 2023-10-03 16:53
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.MODEL_TYPE)
public class ModelTypeFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {
        // 1. 用户可用模型
        List<String> allowModelTypeList = data.getAllowModelTypeList();
        System.out.println(allowModelTypeList);
        String modelType = chatProcess.getModel();

        // 2. 模型校验通过
        if (allowModelTypeList.contains(modelType)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcess)
                    .build();
        }

        // 3. 模型校验拦截
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.REFUSE)
                .info("当前账户不支持使用 " + modelType + " 模型！可以联系客服升级账户。")
                .data(chatProcess)
                .build();
    }

}
