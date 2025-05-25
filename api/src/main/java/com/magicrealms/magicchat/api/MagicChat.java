package com.magicrealms.magicchat.api;

import com.magicrealms.magicchat.api.channel.IChannelManager;
import com.magicrealms.magicchat.api.format.FormatManager;
import com.magicrealms.magicchat.api.member.IMemberManager;
import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.store.RedisStore;
import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc Abstract MagicChat Plugin
 * @date 2025-05-25
 */
public abstract class MagicChat extends MagicRealmsPlugin {

    protected MagicChatAPI api;

    @Getter
    protected static MagicChat instance;

    @Getter
    protected RedisStore redisStore;

    @Getter
    protected FormatManager formatManager;

    @Getter
    protected IChannelManager channelManager;

    @Getter
    protected IMemberManager memberManager;

    public MagicChat() {
        instance = this;
    }

}
