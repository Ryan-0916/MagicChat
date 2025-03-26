package com.margicrealms.magicchat.common.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 全员置顶消息
 * @date 2025-03-25
 */
@Getter
public class ToppingAllMessage extends AbstractToppingMessage {

    public ToppingAllMessage(@NotNull MagicRealmsPlugin plugin, @NotNull String content, long keepTime) {
        this(plugin, null, content, keepTime);
    }

    public ToppingAllMessage(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content, long keepTime) {
        super(plugin, sender, content, keepTime);
    }

}
