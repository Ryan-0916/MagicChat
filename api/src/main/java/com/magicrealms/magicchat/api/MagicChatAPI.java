package com.magicrealms.magicchat.api;

import com.magicrealms.magicchat.api.channel.AbstractChannel;
import com.magicrealms.magicchat.api.channel.IChannelManager;
import com.magicrealms.magicchat.api.member.IMemberManager;
import com.magicrealms.magicchat.api.member.Member;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc MagicChatAPI API
 * @date 2025-05-25
 */
@SuppressWarnings("unused")
public record MagicChatAPI(MagicChat plugin) {

    private static MagicChatAPI instance;

    public MagicChatAPI(MagicChat plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static MagicChatAPI getInstance() {
        if (instance == null) {
            throw new RuntimeException("MagicChat API 未被初始化");
        }
        return instance;
    }

    /**
     * query member by player
     * {@link IMemberManager#getMember(Player)}
     */
    public Member getMember(Player player) {
       return plugin.getMemberManager().getMember(player);
    }

    /**
     * query member by player UUID
     * {@link IMemberManager#queryMember(UUID)}
     */
    public Optional<Member> queryMember(UUID memberId) {
        return plugin.getMemberManager().queryMember(memberId);
    }

    /**
     * query member by player UUID
     * {@link IChannelManager#getChannel(String)}
     */
    public AbstractChannel queryChannel(String channelName) {
        return plugin.getChannelManager().getChannel(channelName);
    }

}
