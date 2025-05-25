package com.magicrealms.magicchat.core.member;

import com.magicrealms.magicchat.api.MagicChat;
import com.magicrealms.magicchat.api.member.IMemberManager;
import com.magicrealms.magicchat.api.message.MessageHistoryStorage;
import com.magicrealms.magicchat.core.BukkitMagicChat;
import com.magicrealms.magicchat.api.channel.AbstractChannel;
import com.magicrealms.magicchat.api.member.Member;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.magicrealms.magicchat.common.MagicChatConstant.MEMBERS_CHANNEL;
import static com.magicrealms.magicchat.common.MagicChatConstant.YML_CONFIG;

/**
 * @author Ryan-0916
 * @Desc
 * 成员存储类
 * 该类用于存储和管理服务器内的成员
 * 通过该类可以获取成员，创建成员，销毁成员。
 * @date 2025-04-09
 */
public class MemberManager implements IMemberManager {

    private final Map<UUID, Member> members;

    public MemberManager() {
        members = new ConcurrentHashMap<>();
    }

    @Override
    public Member getMember(Player player) {
        /* 成员已注册 */
        if (members.containsKey(player.getUniqueId())) {
            return members.get(player.getUniqueId());
        }
        String channelName = BukkitMagicChat.getInstance().getRedisStore()
                .hGetValue(MEMBERS_CHANNEL, player.getName())
                .orElse(BukkitMagicChat.getInstance().getConfigManager().getYmlValue(YML_CONFIG, "DefaultChannel"));
        AbstractChannel channel = MagicChat.getInstance().getChannelManager().getChannel(channelName);
        Member member = new Member(player, channel);
        /* 同步聊天记录 */
        MessageHistoryStorage.getInstance().initializeHistory(member);
        members.put(player.getUniqueId(), member);
        return member;
    }

    @Override
    public Optional<Member> queryMember(UUID memberId) {
        return Optional.ofNullable(members.get(memberId));
    }
}
