import { Button, Input } from "antd";
import styles from "./auth.module.scss";

import qrImage from "../../../../public/qr.png"

import { useNavigate } from "react-router-dom";
import { useAccessStore } from "../../store/access";
import ChatGPTIcon from "../../icons/ChatGLM.svg";
export function Auth() {
    const navigate = useNavigate();
    const access = useAccessStore();
    return (
        <div className={styles["auth-page"]}>
            <ChatGPTIcon/>
            <div className={styles["auth-title"]}>ChatGLM-智谱AI对话平台</div>
            <div className={styles["auth-sub-title"]}>
            </div>
            <img
                src="https://blog.zxdemo.top/upload/img/blog/qr.png"
                style={{ width: 250 }}
            />
            <div className={styles["auth-tips"]}>
                <b>
                    扫码关注公众号【0与1】，

                    回复【01】获取访问密码
                </b>
            </div>

            <Input
                className={styles["auth-input"]}
                type="password"
                placeholder="在此处填写访问码"
                value={access.accessCode}
                onChange={(e) => {
                    access.updateCode(e.currentTarget.value);
                }}
                status={access.accessCodeErrorMsgs?'error': ''}

            />
            {access.accessCodeErrorMsgs?<span className={styles['auth-error']}>{access.accessCodeErrorMsgs}</span>:null}


            <div className={styles["auth-actions"]}>
                <Button type="primary" onClick={() => access.login()}>确认登录</Button>

            </div>
            <span>
        <b>注意</b>：使用过程中，请勿涉及色情、违法、暴力等不当话题。保持交流内容积极、合法，共同营造健康的网络环境！
      </span>
        </div>
    );
}
