package com.magicrealms.magicchat.core.message.entity.channel;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 置顶消息类
 * 该类用于表示一种特殊类型的置顶消息——针对特定成员的置顶消息。消息将被指定的玩家看到，
 * 并在聊天框的最下端显示，直到超出指定的保持时间。超过保持时间后，它将成为普通消息，移出置顶状态。
 * 该类继承自 `AbstractToppingMessage`，并实现了针对特定成员的置顶消息功能。
 * @date 2025-03-25
 */
@Getter
public class ToppingMessage extends AbstractToppingMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 置顶的目标成员列表，只有这些成员能看到此消息 */
    private final List<UUID> target;

    /**
     * 构造函数，用于创建一条新的置顶消息，针对指定的成员。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param permissionNode 拥有此权限节点的玩家才能看到此消息，若为 NULL 则表示消息对所有玩家可见
     * @param content 消息的原始内容，插件需要传入未经过任何处理的文本
     * @param keepTick 该消息保持在置顶状态的时间（单位：tick，1 tick = 1/20 秒）
     * @param target 需要接收该置顶消息的目标成员列表（玩家的 UUID）
     */
    public ToppingMessage(@Nullable UUID sender, @Nullable String permissionNode, String content, long keepTick, List<UUID> target) {
        super(sender, permissionNode, content, keepTick);
        this.target = target;
    }
}
