package com.margicrealms.magicchat.common.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 艾特全体频道内成员消息
 * @date 2025-03-25
 */
public class AtAllMessage extends ToppingAllMessage {

    public AtAllMessage(@NotNull MagicRealmsPlugin plugin, @NotNull String content, long keepTime) {
        this(plugin, null, content, keepTime);
    }

    public AtAllMessage(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content, long keepTime) {
        super(plugin, sender, content, keepTime);
    }

}
