package com.magicrealms.magicchat.core.message.entity;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;


/**
 * @author Ryan-0916
 * @Desc 频道消息类
 * 该类用于表示特定频道中的消息，可以包括权限控制，只有拥有某个权限节点的玩家才能看到此消息。
 * 此类继承自抽象类 `AbstractMessage`，并实现了消息内容的具体处理方法。
 * @date 2025-03-25
 */
@Getter
public class ChannelMessage extends AbstractMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 拥有某项权限的玩家可见 */
    private final String permissionNode;

    /**
     * 创建一条新的频道消息。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由系统发送
     * @param permissionNode 拥有此权限节点的玩家才能看到此消息
     * @param content 消息的原始内容，插件需要传入未经过任何处理的文本
     */
    public ChannelMessage(@Nullable UUID sender, String permissionNode, String content) {
        super(sender, content);
        this.permissionNode = permissionNode;
    }

    /**
     * 消息内容的具体处理方法
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param content 消息的原始内容，未经过任何处理的文本
     * @return 处理后的消息内容
     */
    @Override
    protected String handleMessage(@Nullable UUID sender, String content) {
        return content;
    }

}
