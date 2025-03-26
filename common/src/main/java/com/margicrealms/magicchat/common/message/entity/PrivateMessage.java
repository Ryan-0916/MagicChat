package com.margicrealms.magicchat.common.message.entity;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 私有消息，该消息仅玩家本人可见
 * @date 2025-03-25
 */
@Getter
public class PrivateMessage extends AbstractMessage {

    /** 消息的接收者者 */
    private final UUID exclusive;

    public PrivateMessage(@NotNull MagicRealmsPlugin plugin, @NotNull String content, @NotNull UUID exclusive) {
        this(plugin,null, content, exclusive);
    }

    public PrivateMessage(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content, @NotNull UUID exclusive) {
        super(plugin, sender, content);
        this.exclusive = exclusive;
    }

    @Override
    protected String handleMessage(@Nullable UUID sender, @NotNull String content) {
        return "";
    }


}
