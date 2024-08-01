package top.zxdemo.chatgpt.data.test;

import top.zxdemo.chatglm.model.*;
import top.zxdemo.chatgpt.data.trigger.http.dto.ChatGPTRequestDTO;
import top.zxdemo.chatgpt.data.trigger.http.dto.MessageEntity;
import top.zxdemo.chatgpt.data.types.enums.ChatGPTModel;
import top.zxdemo.chatglm.session.OpenAiSession;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author @author ZhangXing zxdemo.top
 * @description 测试工程
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired(required = false)
    @Resource
    private OpenAiSession openAiSession;

    /**
     * 此对话模型 3.5 接近于官网体验
     */
    @Test
    public void test_chat_completions() throws Exception {
        // 1. 创建参数
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_3_5_TURBO); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("写一个Java冒泡排序")
                        .build());
            }
        });
        // 2. 发起请求
        ChatCompletionSyncResponse response = openAiSession.completionsSync(request);
        // 3. 解析结果
        response.getChoices().forEach(e -> {
            log.info("测试结果：{}", e.getMessage());
        });
    }

    /**
     * 此对话模型 3.5 接近于官网体验 & 流式应答
     */
    @Test
    public void test_chat_completions_stream() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_3_5_TURBO); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setIncremental(false);
        request.setIsCompatible(true); // 是否对返回结果数据做兼容，24年1月发布的 GLM_3_5_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异。开启 true 可以做兼容。
        // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
        request.setTools(new ArrayList<ChatCompletionRequest.Tool>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Tool.builder()
                        .type(ChatCompletionRequest.Tool.Type.web_search)
                        .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("小傅哥").build())
                        .build());
            }
        });
        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("你好")
                        .build());
            }
        });

        // 请求
        openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果 onEvent：{}", response.getData());
                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.finish.getCode().equals(type)) {
                    ChatCompletionResponse.Meta meta = JSON.parseObject(response.getMeta(), ChatCompletionResponse.Meta.class);
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(meta));
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.info("对话异常");
                countDownLatch.countDown();
            }
        });

        // 等待
        countDownLatch.await();
    }





    @Test
    public void test_request_json() {

        List<MessageEntity> messages = new ArrayList<>();

//        MessageEntity messageEntity00 = new MessageEntity();
//        messageEntity00.setRole(Constants.Role.USER.getCode());
//        messageEntity00.setContent("你是一个非常专业的 Java 开发工程师，具备世界一流水平，曾在多家世界级互联网公司任职，可以写出非常优秀易于扩展和迭代的代码。");

        MessageEntity messageEntity01 = new MessageEntity();
        // messageEntity01.setRole(Constants.Role.USER.getCode());
        messageEntity01.setContent("写一个java冒泡排序");

//        messages.add(messageEntity00);
        messages.add(messageEntity01);

        ChatGPTRequestDTO requestDTO = ChatGPTRequestDTO.builder().model(ChatGPTModel.GPT_3_5_TURBO.getCode()).messages(messages).build();

        log.info(JSON.toJSONString(requestDTO));
        // {"messages":[{"content":"你是一个非常专业的 Java 开发工程师，具备世界一流水平，曾在多家世界级互联网公司任职，可以写出非常优秀易于扩展和迭代的代码。","role":"user"},{"content":"写一个java冒泡排序","role":"user"}],"model":"gpt-3.5-turbo"}

        String str = "{\"messages\":[{\"content\":\"写一个java冒泡排序\",\"role\":\"user\"}],\"model\":\"gpt-3.5-turbo\"}";
    }

}
