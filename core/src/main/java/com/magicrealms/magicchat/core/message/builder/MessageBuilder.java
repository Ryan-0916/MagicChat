package com.magicrealms.magicchat.core.message.builder;

import com.magicrealms.magicchat.core.message.entity.AbstractMessage;
import com.magicrealms.magicchat.core.message.entity.PrivateMessage;
import com.magicrealms.magicchat.core.message.entity.PublicMessage;
import com.magicrealms.magicchat.core.message.entity.pub.AtAllMessage;
import com.magicrealms.magicchat.core.message.entity.pub.AtMessage;
import com.magicrealms.magicchat.core.message.entity.pub.ToppingAllMessage;
import com.magicrealms.magicchat.core.message.entity.pub.ToppingMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 消息构造器
 * @date 2025-03-26
 */
@SuppressWarnings("unused")
public class MessageBuilder {

    /** 消息发送者 */
    private final UUID SENDER;
    private final String CONTENT;
    private long keepTime;
    private UUID exclusive;
    private List<UUID> target;
    private String permissionNode;

    public MessageBuilder(@Nullable UUID sender, String content) {
        this.SENDER = sender;
        this.CONTENT = content;
    }

    public MessageBuilder setKeepTime(long keepTime) {
        this.keepTime = keepTime;
        return this;
    }

    public MessageBuilder setExclusive(UUID exclusive) {
        this.exclusive = exclusive;
        return this;
    }

    public MessageBuilder setTarget(List<UUID> target) {
        this.target = target;
        return this;
    }

    public MessageBuilder setPermissionNode(String permissionNode) {
        this.permissionNode = permissionNode;
        return this;
    }

    public AbstractMessage build(MessageType type) {
        return switch (type) {
            case PUBLIC -> new PublicMessage(SENDER, permissionNode, CONTENT);
            case PRIVATE -> new PrivateMessage(SENDER, CONTENT, exclusive);
            case TOPPING -> new ToppingMessage(SENDER, permissionNode, CONTENT, keepTime, target);
            case TOPPING_ALL -> new ToppingAllMessage(SENDER, permissionNode, CONTENT, keepTime);
            case AT -> new AtMessage(SENDER, permissionNode, CONTENT, keepTime, target);
            case AT_ALL -> new AtAllMessage(SENDER, permissionNode, CONTENT, keepTime);
        };
    }
}
