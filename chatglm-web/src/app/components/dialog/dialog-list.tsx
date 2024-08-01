import styles from "./dialog-list.module.scss";
import {DialogListItem} from "./dialog-list-item";
import {DialogResizeableSidebar} from "@/app/components/dialog/dialog-resizeable-sidebar";
import {useNavigate} from "react-router-dom";
import {userChatStore} from "@/app/store/chat-store";
import {DialogHead} from "@/app/components/dialog/dialog-head";
import { useEffect } from 'react';

/**
 * 对话框列表
 */
export function DialogList() {
    const navigate = useNavigate();
    const chatStore = userChatStore();
    const [sessions, currentSessionIndex, selectSession] = userChatStore(
        (state) => [
            state.sessions,
            state.currentSessionIndex,
            state.selectSession]);


    useEffect(() => {
        // 如果当前没有选中的会话并且有会话存在，则选中第一个会话
        selectSession(0);
        navigate(`/chat/${sessions[0].id}`, { state: { title: sessions[0].dialog.title } });

    }, []); // 注意，这里只在初次加载时执行

    return (
        // DialogResizeableSidebar 用于调整对话栏的大小
        <DialogResizeableSidebar>
            {/*头部操作*/}
            <DialogHead/>
            {/*对话列表*/}
            <div className={styles["dialog-list"]}>
                {sessions.map((session, index) => (
                    <DialogListItem
                        key={session.id}
                        session={session}
                        selected={currentSessionIndex === index}
                        onClick={() => {
                            // 点击时跳转到对应的界面，并传递必要参数信息
                            selectSession(index);
                            navigate(`/chat/${session.id}`, {state: {title: session.dialog.title}})
                        }}
                        onClickDelete={() => {
                            chatStore.deleteSession(index);
                        }}


                    />
                ))}

            </div>
        </DialogResizeableSidebar>
    );

}