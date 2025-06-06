package com.magicrealms.magicchat.api.member.state;

import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.ExclusiveMessage;

/**
 * @author Ryan-0916
 * @Desc 阻塞状态
 * 该状态下 MagicChat 插件不会给玩家发送任何消息
 * 玩家消息将由子状态代理
 * @date 2025-04-25
 */
public interface BlockState {
    /* 进入状态 */
    default void enter(Member member){}
    /* 离开状态 */
    default void exit(Member member){}
    /* 消息处理 */
    default void handleMessage(Member member, ExclusiveMessage message){}
    /* 重置聊天框 */
    default void resetChatDialog(Member member){}
}
