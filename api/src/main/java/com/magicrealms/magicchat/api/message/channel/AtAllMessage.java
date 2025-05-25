package com.magicrealms.magicchat.api.message.channel;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 艾特全体频道内成员消息类
 * 该类表示一种特殊类型的艾特全体消息（@全体消息），它继承自 `ToppingAllMessage` 类。
 * 艾特全体消息用于提醒频道中的所有成员，通常是类似于聊天中“@everyone”提醒的功能。
 * 该消息会置顶显示在所有在线成员的聊天框中，直到超出指定的保持时间。超出保持时间后，它将成为普通消息，移出置顶状态。
 * @date 2025-03-25
 */
public class AtAllMessage extends ToppingAllMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，用于创建一条新的艾特全体消息，针对所有频道成员的置顶消息。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param permissionNode 拥有此权限节点的玩家才能看到此消息，若为 NULL 则表示消息对所有玩家可见
     * @param content 消息的原始内容，插件需要传入未经过任何处理的文本
     * @param priority 消息的权重，权重越大优先级越高
     * @param keepTick 该消息保持在置顶状态的时间（单位：tick，1 tick = 1/20 秒）
     */
    public AtAllMessage(@Nullable UUID sender, @Nullable String permissionNode, String content, int priority, long keepTick) {
        super(sender, permissionNode, content, priority, keepTick); // 调用父类构造函数
    }
}
