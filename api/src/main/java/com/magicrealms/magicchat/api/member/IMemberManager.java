package com.magicrealms.magicchat.api.member;

import org.bukkit.entity.Player;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 用于管理成员相关方法的接口类
 * @date 2025-05-25
 */
public interface IMemberManager {

    /**
     * 查询聊天成员
     * @param player 玩家
     * @return 聊天成员
     * 如果聊天成员不存在，则根据玩家创建该成员
     * 如果成员存在，则返回该成员
     */
    Member getMember(Player player);

    /**
     * 查询成员
     * @param memberId 玩家 UUID
     * @return 聊天成员
     * 如果聊天成员不存在，返回 NULL
     * 如果成员存在，则返回该成员
     */
    Optional<Member> queryMember(UUID memberId);
}
