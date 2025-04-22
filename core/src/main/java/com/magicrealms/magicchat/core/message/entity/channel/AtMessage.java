package com.magicrealms.magicchat.core.message.entity.channel;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 艾特消息类
 * 该类表示一种特殊类型的艾特消息（@消息），它继承自 `ToppingMessage` 类。
 * 艾特消息是针对特定成员的置顶消息，通常用于提醒某些特定玩家，类似于聊天中“@某某”提醒的功能。
 * 艾特消息将仅展示给指定的目标玩家，并在聊天框中显示，直到超出指定的保持时间。超过保持时间后，它将成为普通消息，移出置顶状态。
 * @date 2025-03-25
 */
public class AtMessage extends ToppingMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，用于创建一条新的艾特消息，针对指定成员的置顶消息。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param permissionNode 拥有此权限节点的玩家才能看到此消息，若为 NULL 则表示消息对所有玩家可见
     * @param content 消息的原始内容，插件需要传入未经过任何处理的文本
     * @param weight 消息的权重，权重越大优先级越高
     * @param keepTick 该消息保持在置顶状态的时间（单位：tick，1 tick = 1/20 秒）
     * @param target 需要接收该置顶消息的目标成员列表（玩家的 UUID）
     */
    public AtMessage(@Nullable UUID sender, @Nullable String permissionNode, String content, int weight, long keepTick, List<UUID> target) {
        super(sender, permissionNode, content, weight, keepTick, target);
    }
}
