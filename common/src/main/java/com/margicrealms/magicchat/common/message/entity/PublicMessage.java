package com.margicrealms.magicchat.common.message.entity;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 公共消息 - 此消息全员可见
 * @date 2025-03-25
 */
public class PublicMessage extends AbstractMessage {

    public PublicMessage(@NotNull MagicRealmsPlugin plugin, @NotNull String content) {
        this(plugin, null, content);
    }


    public PublicMessage(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content) {
        super(plugin, sender, content);
    }

    @Override
    protected String handleMessage(@Nullable UUID sender, @NotNull String content) {
        return "";
    }

}
