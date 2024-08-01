package top.zxdemo.chatlm.data.domain.openai.service.rule;

import top.zxdemo.chatlm.data.domain.openai.model.aggregates.ChatProcessAggregate;
import top.zxdemo.chatlm.data.domain.openai.model.entity.RuleLogicEntity;

/**
 * @author ZhangXing zxdemo.top
 * @description 规则过滤接口
 */
public interface ILogicFilter<T> {

    RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, T data) throws Exception;

}
