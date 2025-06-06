package com.magicrealms.magicchat.api.member.state;

import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.ExclusiveMessage;

/**
 * @author Ryan-0916
 * @Desc 无阻塞状态
 * 没有任何的阻塞状态
 * 默认的处理事件
 * @date 2025-04-25
 */
public class NoneBlockState implements BlockState {

    @Override
    public void handleMessage(Member member, ExclusiveMessage message) {
        /* 添加消息至聊天记录 */
        member.addMessageHistory(message);
        resetChatDialog(member);
    }

    @Override
    public void resetChatDialog(Member member) {
        member.asyncResetChatDialog(null);
    }

}
