package com.magicrealms.magicchat.api.member.state;

import com.magicrealms.magicchat.api.MagicChat;
import com.magicrealms.magicchat.api.member.listener.SelectorListener;
import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.ExclusiveMessage;
import com.magicrealms.magicchat.api.message.exclusive.SelectorMessage;
import org.bukkit.Bukkit;

/**
 * @author Ryan-0916
 * @Desc 选择器阻塞
 * 发送选择器消息时将进入阻塞状态
 * 阻塞状态下发送专属消息，重组聊天框事件将由状态代理
 * @date 2025-04-25
 */
public class SelectorBlockState implements BlockState {

    private SelectorListener selectorListener;

    private final SelectorMessage message;

    public SelectorBlockState(SelectorMessage message) {
        this.message = message;
    }

    @Override
    public void enter(Member member) {
        selectorListener = new SelectorListener(member, message);
        Bukkit.getPluginManager()
                .registerEvents(selectorListener, MagicChat.getInstance());
    }

    @Override
    public void exit(Member member) {
        if (selectorListener != null) {
            selectorListener.unregister();
            selectorListener = null;
        }
    }

    @Override
    public void handleMessage(Member member, ExclusiveMessage message) {
        /* 选择器状态下消息只会添加到聊天记录中，不会重组聊天框 */
        if (!(message instanceof SelectorMessage)) {
            member.addMessageHistory(message);
        }
        resetChatDialog(member);
    }

    @Override
    public void resetChatDialog(Member member) {
        member.asyncResetChatDialog(selectorListener != null ?
                selectorListener.getSelector().getContent() : null);
    }

}
