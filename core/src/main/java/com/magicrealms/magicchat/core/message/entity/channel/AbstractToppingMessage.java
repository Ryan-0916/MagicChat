package com.magicrealms.magicchat.core.message.entity.channel;

import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 置顶消息抽象类
 * 该类用于表示一种特殊的消息——置顶消息。置顶消息将永远显示在聊天框的最下端，直到超出指定的保持时间。
 * 超过保持时间后，消息将变为普通消息，不再保持在最下端。该类继承自 `ChannelMessage`，并扩展了消息保持时间的功能。
 * @date 2025-03-26
 */
@Getter
public abstract class AbstractToppingMessage extends ChannelMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 保持时间（单位：tick），即消息保持在置顶状态的时间 */
    private final long keepTick;

    /**
     * 构造函数，用于创建一条新的置顶消息。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由系统发送
     * @param permissionNode 拥有此权限节点的玩家才能看到此消息
     * @param content 消息的原始内容，插件需要传入未经过任何处理的文本
     * @param keepTick 该消息保持在置顶状态的时间（单位：tick，1 tick = 1/20 秒）
     */
    public AbstractToppingMessage(@Nullable UUID sender, @Nullable String permissionNode ,String content, long keepTick) {
        super(sender, permissionNode, content);
        this.keepTick = keepTick;
    }

}
