package com.margicrealms.magicchat.common.message.builder;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.margicrealms.magicchat.common.message.entity.AbstractMessage;
import com.margicrealms.magicchat.common.message.entity.PrivateMessage;
import com.margicrealms.magicchat.common.message.entity.PublicMessage;
import com.margicrealms.magicchat.common.message.entity.pub.AtAllMessage;
import com.margicrealms.magicchat.common.message.entity.pub.AtMessage;
import com.margicrealms.magicchat.common.message.entity.pub.ToppingAllMessage;
import com.margicrealms.magicchat.common.message.entity.pub.ToppingMessage;
import com.margicrealms.magicchat.common.message.enums.MessageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 消息构造器
 * @date 2025-03-26
 */
public class MessageBuilder {

    /** 消息发送者 */
    private final MagicRealmsPlugin PLUGIN;
    private final UUID SENDER;
    private final String CONTENT;
    private long keepTime;
    private UUID exclusive;
    private List<UUID> target;

    public MessageBuilder(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content) {
        this.PLUGIN = plugin;
        this.SENDER = sender;
        this.CONTENT = content;
    }

    public MessageBuilder setKeepTime(long keepTime) {
        this.keepTime = keepTime;
        return this;
    }

    public MessageBuilder setExclusive(@NotNull UUID exclusive) {
        this.exclusive = exclusive;
        return this;
    }

    public MessageBuilder setTarget(@NotNull List<UUID> target) {
        this.target = target;
        return this;
    }

    public AbstractMessage build(@NotNull MessageType type) {
        return switch (type) {
            case PUBLIC -> new PublicMessage(PLUGIN, SENDER, CONTENT);
            case PRIVATE -> new PrivateMessage(PLUGIN, SENDER, CONTENT, exclusive);
            case TOPPING -> new ToppingMessage(PLUGIN, SENDER, CONTENT, keepTime, target);
            case TOPPING_ALL -> new ToppingAllMessage(PLUGIN, SENDER, CONTENT, keepTime);
            case AT -> new AtMessage(PLUGIN, SENDER, CONTENT, keepTime, target);
            case AT_ALL -> new AtAllMessage(PLUGIN, SENDER, CONTENT, keepTime);
        };
    }
}
