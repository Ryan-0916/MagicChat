package com.magicrealms.magicchat.core.message.builder;

import com.magicrealms.magicchat.core.message.entity.AbstractMessage;
import com.magicrealms.magicchat.core.message.entity.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import com.magicrealms.magicchat.core.message.entity.channel.AtAllMessage;
import com.magicrealms.magicchat.core.message.entity.channel.AtMessage;
import com.magicrealms.magicchat.core.message.entity.channel.ToppingAllMessage;
import com.magicrealms.magicchat.core.message.entity.channel.ToppingMessage;
import com.magicrealms.magicchat.core.message.entity.exclusive.TypewriterMessage;
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
    private long keepTick;
    private List<UUID> target;
    private String permissionNode;
    private String prefix;
    private int printTick;

    public MessageBuilder(@Nullable UUID sender, String content) {
        this.SENDER = sender;
        this.CONTENT = content;
    }

    public MessageBuilder setKeepTick(long keepTick) {
        this.keepTick = keepTick;
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

    public MessageBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public MessageBuilder setPrintTick(int printTick) {
        this.printTick = printTick;
        return this;
    }

    public AbstractMessage build(MessageType type) {
        return switch (type) {
            case CHANNEL -> new ChannelMessage(SENDER, permissionNode, CONTENT);
            case EXCLUSIVE -> new ExclusiveMessage(SENDER, CONTENT);
            case TOPPING -> new ToppingMessage(SENDER, permissionNode, CONTENT, keepTick, target);
            case TOPPING_ALL -> new ToppingAllMessage(SENDER, permissionNode, CONTENT, keepTick);
            case AT -> new AtMessage(SENDER, permissionNode, CONTENT, keepTick, target);
            case AT_ALL -> new AtAllMessage(SENDER, permissionNode, CONTENT, keepTick);
            case TYPEWRITER -> new TypewriterMessage(SENDER, CONTENT, prefix, printTick);
        };
    }
}
