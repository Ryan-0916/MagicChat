package com.magicrealms.magicchat.core.message.builder;

import com.magicrealms.magicchat.core.message.AbstractMessage;
import com.magicrealms.magicchat.core.message.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.ChannelMessage;
import com.magicrealms.magicchat.core.message.channel.AtAllMessage;
import com.magicrealms.magicchat.core.message.channel.AtMessage;
import com.magicrealms.magicchat.core.message.channel.ToppingAllMessage;
import com.magicrealms.magicchat.core.message.channel.ToppingMessage;
import com.magicrealms.magicchat.core.message.exclusive.SelectorMessage;
import com.magicrealms.magicchat.core.message.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.message.option.AbstractOption;
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
    private String suffix;
    private int printTick;
    private int weight;
    private List<AbstractOption> options;

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

    public MessageBuilder setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public MessageBuilder setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public MessageBuilder setOptions(List<AbstractOption> options) {
        this.options = options;
        return this;
    }

    public AbstractMessage build(MessageType type) {
        return switch (type) {
            case CHANNEL -> new ChannelMessage(SENDER, permissionNode, CONTENT);
            case EXCLUSIVE -> new ExclusiveMessage(SENDER, CONTENT);
            case TOPPING -> new ToppingMessage(SENDER, permissionNode, CONTENT, weight, keepTick, target);
            case TOPPING_ALL -> new ToppingAllMessage(SENDER, permissionNode, CONTENT, weight, keepTick);
            case AT -> new AtMessage(SENDER, permissionNode, CONTENT, weight, keepTick, target);
            case AT_ALL -> new AtAllMessage(SENDER, permissionNode, CONTENT, weight, keepTick);
            case TYPEWRITER -> new TypewriterMessage(SENDER, CONTENT, prefix, suffix, printTick);
            case SELECTOR -> new SelectorMessage(SENDER, CONTENT, prefix, suffix, printTick, options);
        };
    }
}
