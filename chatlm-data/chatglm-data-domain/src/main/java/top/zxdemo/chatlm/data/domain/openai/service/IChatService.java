package top.zxdemo.chatlm.data.domain.openai.service;

import top.zxdemo.chatlm.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author ZhangXing zxdemo.top
 * @description
 */
public interface IChatService {

    ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess);

}
