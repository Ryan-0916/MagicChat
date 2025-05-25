package com.magicrealms.magicchat.api.message.channel;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 全员置顶消息类
 * 该类用于表示一种特殊类型的置顶消息——全员置顶消息。消息将被所有玩家看到，并且在聊天框的最下端显示，
 * 直到超出指定的保持时间。超过保持时间后，它将成为普通消息，移出置顶状态。
 * 该类继承自 `AbstractToppingMessage`，并实现了全员置顶消息的功能。
 * @date 2025-03-25
 */
public class ToppingAllMessage extends AbstractToppingMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，用于创建一条新的全员置顶消息。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param permissionNode 拥有此权限节点的玩家才能看到此消息，若为 NULL 则表示消息对所有玩家可见
     * @param content 消息的原始内容，插件需要传入未经过任何处理的文本
     * @param priority 消息的权重，权重越大优先级越高
     * @param keepTick 该消息保持在置顶状态的时间（单位：tick，1 tick = 1/20 秒）
     */
    public ToppingAllMessage(@Nullable UUID sender, @Nullable String permissionNode, String content, int priority, long keepTick) {
        super(sender, permissionNode, content, priority, keepTick);
    }
}
