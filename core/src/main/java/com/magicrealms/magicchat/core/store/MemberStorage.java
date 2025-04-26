package com.magicrealms.magicchat.core.store;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.channel.AbstractChannel;
import com.magicrealms.magicchat.core.member.Member;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.magicrealms.magicchat.core.MagicChatConstant.MEMBERS_CHANNEL;
import static com.magicrealms.magicchat.core.MagicChatConstant.YML_CONFIG;

/**
 * @author Ryan-0916
 * @Desc
 * 成员存储类
 * 该类用于存储和管理服务器内的成员
 * 通过该类可以获取成员，创建成员，销毁成员。
 * @date 2025-04-09
 */
public class MemberStorage {

    private static volatile MemberStorage instance;

    private final Map<UUID, Member> members;

    private MemberStorage() {
        members = new ConcurrentHashMap<>();
    }

    public static MemberStorage getInstance() {
        if (instance == null) {
            synchronized (MemberStorage.class) {
                if (instance == null) {
                    instance = new MemberStorage();
                }
            }
        }
        return instance;
    }

    /**
     * 检索成员
     * 检查玩家是否已经注册过，如果未注册，则为其创建并注册一个新的成员对象。
     * 新成员会默认加入到配置中指定的频道。
     * 如果成员已经存在，直接返回已有的成员对象。
     * @param player 玩家对象，用于创建成员
     * @return 返回已注册或新创建的成员对象
     */
    @NotNull
    public Member retrieveMember(Player player) {
        /* 成员已注册 */
        if (members.containsKey(player.getUniqueId())) {
            return members.get(player.getUniqueId());
        }
        String channelName = MagicChat.getInstance().getRedisStore()
                .hGetValue(MEMBERS_CHANNEL, player.getName())
                .orElse(MagicChat.getInstance().getConfigManager().getYmlValue(YML_CONFIG, "DefaultChannel"));
        AbstractChannel channel = ChannelStorage.getInstance().retrieveChannel(channelName);
        Member member = new Member(player, channel);
        /* 同步聊天记录 */
        MessageHistoryStorage.getInstance().initializeHistory(member);
        members.put(player.getUniqueId(), member);
        return member;
    }

}
