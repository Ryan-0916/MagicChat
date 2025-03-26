package com.margicrealms.magicchat.common.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 艾特消息
 * @date 2025-03-25
 */
public class AtMessage extends ToppingMessage {

    public AtMessage(@NotNull MagicRealmsPlugin plugin, @NotNull String content, long keepTime, @NotNull List<UUID> target) {
        this(plugin, null, content, keepTime, target);
    }

    public AtMessage(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content, long keepTime, @NotNull List<UUID> target) {
        super(plugin, sender, content, keepTime, target);
    }

}
