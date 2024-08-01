package top.zxdemo.chatlm.data.domain.openai.service.rule.impl;

import cn.bugstack.chatglm.model.Role;
import top.zxdemo.chatlm.data.domain.openai.annotation.LogicStrategy;
import top.zxdemo.chatlm.data.domain.openai.model.aggregates.ChatProcessAggregate;
import top.zxdemo.chatlm.data.domain.openai.model.entity.MessageEntity;
import top.zxdemo.chatlm.data.domain.openai.model.entity.RuleLogicEntity;
import top.zxdemo.chatlm.data.domain.openai.model.entity.UserAccountQuotaEntity;
import top.zxdemo.chatlm.data.domain.openai.model.valobj.LogicCheckTypeVO;
import top.zxdemo.chatlm.data.domain.openai.service.rule.ILogicFilter;
import top.zxdemo.chatlm.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 敏感词过滤
 * @create 2023-09-16 17:39
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.SENSITIVE_WORD)
public class SensitiveWordFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Resource
    private SensitiveWordBs words;

    @Value("${app.config.white-list}")
    private String whiteListStr;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {
        // 白名单用户不做敏感词处理
        if (chatProcess.isWhiteList(whiteListStr)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        ChatProcessAggregate newChatProcessAggregate = new ChatProcessAggregate();
        newChatProcessAggregate.setOpenid(chatProcess.getOpenid());
        newChatProcessAggregate.setModel(chatProcess.getModel());


        List<MessageEntity> newMessages = new ArrayList<>();
        boolean hasSensitiveWords = false;

        for (MessageEntity message : chatProcess.getMessages()) {
            String content = message.getContent();
            String replace = words.replace(content);

            // 仅对用户消息进行敏感词检测
            if (Role.user.getCode().equals(message.getRole()) && !content.equals(replace)) {
                hasSensitiveWords = true;
            }

            newMessages.add(MessageEntity.builder()
                    .role(message.getRole())
                    .name(message.getName())
                    .content(replace)
                    .build());
        }

        newChatProcessAggregate.setMessages(newMessages);

        if (hasSensitiveWords) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .info("检测到敏感词，请重新输入（请点击清除聊天后继续对话）")
                    .type(LogicCheckTypeVO.REFUSE)
                    .data(chatProcess).build();
        }


        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS)
                .data(newChatProcessAggregate)
                .build();

    }

}
