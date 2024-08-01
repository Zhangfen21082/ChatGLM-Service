// 导入 SCSS 文件
import styles from './dialog-list-item.module.scss';
import { useState, useRef } from 'react';
import { Avatar, Badge, Space } from 'antd';
import { ChatSession } from "@/app/store/chat-store";
import DeleteIcon from "@/app/icons/delete.svg";

interface Props {
    session: ChatSession;
    selected: boolean;
    onClick: () => void;
    onClickDelete: () => void;
}

export function DialogListItem(props: Props) {
    const { session, selected } = props;
    const [isEditing, setIsEditing] = useState(false);
    const [dialogTitle, setDialogTitle] = useState(session.dialog.title);
    const inputRef = useRef<HTMLInputElement>(null); // 用于获取输入框的引用
    const dialog = session.dialog;
    const date = new Date(dialog.timestamp);
    const timeString = date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

    const handleDoubleClick = () => {
        setIsEditing(true);
    };

    const handleBlur = () => {
        setIsEditing(false);
        // 可以在这里添加更新对话标题的逻辑，比如调用一个更新方法
        console.log('更新对话标题:', dialogTitle);
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            setIsEditing(false);
            // 可以在这里添加更新对话标题的逻辑，比如调用一个更新方法
            console.log('更新对话标题:', dialogTitle);
        }
    };

    const clearInput = () => {
        setDialogTitle('');
        if (inputRef.current) {
            inputRef.current.focus(); // 清空后自动聚焦输入框
        }
    };

    return (
        <div className={`${styles.wrapper} ${selected ? styles.selected : ''}`} onClick={props.onClick}>
            <div className={styles.left}>
                <Space size={24}>
                    <Badge count={selected ? dialog.count : 0} size={"small"} color={"#fca7a7"}>
                        <Avatar shape={"square"} src={dialog.avatar} size={40} />
                    </Badge>
                </Space>
            </div>
            <div className={styles.right}>
                <div className={`${styles.line1} ${isEditing ? styles.editing : ''}`} onDoubleClick={handleDoubleClick}>
                    {isEditing ? (
                        <input
                            ref={inputRef}
                            type="text"
                            value={dialogTitle}
                            onChange={(e) => setDialogTitle(e.target.value)}
                            onBlur={handleBlur}
                            onKeyDown={handleKeyDown}
                            autoFocus
                            className={styles.editableInput}
                            placeholder="请输入新的对话标题"
                        />
                    ) : (
                        <p className={styles.title}>{dialogTitle}</p>
                    )}
                    <p className={styles.time}>{timeString}</p>
                </div>
                <div className={styles.line2}>
                    {dialog.subTitle}
                </div>
            </div>
            <div className={styles["chat-item-delete"]} onClickCapture={props.onClickDelete}>
                <DeleteIcon />
            </div>
            {/* 鼠标悬浮提示 */}
            {!isEditing && <div className={styles.tooltip}>双击重命名</div>}
        </div>
    );
}
