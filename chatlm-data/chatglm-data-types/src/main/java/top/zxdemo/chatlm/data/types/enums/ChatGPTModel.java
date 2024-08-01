package top.zxdemo.chatlm.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ZhangXing zxdemo.top
 * @description 模型对象
 */
@Getter
@AllArgsConstructor
public enum ChatGPTModel {

    /** gpt-3.5-turbo */
    GPT_3_5_TURBO("glm-3-turbo"),

    ;
    private final String code;

}
