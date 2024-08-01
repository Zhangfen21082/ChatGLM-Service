package top.zxdemo.chatlm.data.domain.weixin.service.message;

import top.zxdemo.chatlm.data.domain.weixin.model.entity.MessageTextEntity;
import top.zxdemo.chatlm.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import top.zxdemo.chatlm.data.domain.weixin.model.valobj.MsgTypeVO;
import top.zxdemo.chatlm.data.domain.weixin.repository.IWeiXinRepository;
import top.zxdemo.chatlm.data.domain.weixin.service.IWeiXinBehaviorService;
import cn.bugstack.chatgpt.data.types.exception.ChatGPTException;
import cn.bugstack.chatgpt.data.types.sdk.weixin.XmlUtil;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ZhangXing zxdemo.top
 * @description 受理用户行为接口实现类
 */
@Service
@Slf4j
public class WeiXinBehaviorService implements IWeiXinBehaviorService {

    @Value("${wx.config.originalid}")
    private String originalId;

    @Resource
    private Cache<String, String> codeCache;

    @Resource
    private IWeiXinRepository repository;

    /**
     * 1. 用户的请求行文，分为事件event、消息text，这里我们只处理消息内容
     * 2. 用户行为、消息类型，是多样性的，这部分如果用户有更多的扩展需求，可以使用设计模式【模板模式 + 策略模式 + 工厂模式】，分拆逻辑。
     */
    @Override
    public String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity) {
        // Event 事件类型，忽略处理
        if (MsgTypeVO.EVENT.getCode().equals(userBehaviorMessageEntity.getMsgType())) {
            return "";
        }

        // Text 文本类型
        if (MsgTypeVO.TEXT.getCode().equals(userBehaviorMessageEntity.getMsgType())) {

            log.info("用户发送信息", userBehaviorMessageEntity.getContent());
            // 判断请求是正确的
            if (userBehaviorMessageEntity.getContent().equals("01")) {

                // 缓存验证码
                String code = repository.genCode(userBehaviorMessageEntity.getOpenId());

                // 反馈信息[文本]
                MessageTextEntity res = new MessageTextEntity();
                res.setToUserName(userBehaviorMessageEntity.getOpenId());
                res.setFromUserName(originalId);
                res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
                res.setMsgType("text");
                res.setContent(String.format("您的验证码为：%s 有效期%d分钟！", code, 3));
                return XmlUtil.beanToXml(res);
            } else {
                MessageTextEntity res = new MessageTextEntity();
                res.setToUserName(userBehaviorMessageEntity.getOpenId());
                res.setFromUserName(originalId);
                res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
                res.setMsgType("text");
                res.setContent("访问密码错误，请重新输入");
                return XmlUtil.beanToXml(res);
            }
        }

        throw new ChatGPTException(userBehaviorMessageEntity.getMsgType() + " 未被处理的行为类型 Err！");
    }

}
