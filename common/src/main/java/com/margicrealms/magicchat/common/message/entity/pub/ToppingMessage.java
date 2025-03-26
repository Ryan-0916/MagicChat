package com.margicrealms.magicchat.common.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.margicrealms.magicchat.common.message.entity.PublicMessage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 置顶消息
 * @date 2025-03-25
 */
@Getter
public class ToppingMessage extends AbstractToppingMessage {

    /** 置顶成员 */
    private final List<UUID> target;

    public ToppingMessage(@NotNull MagicRealmsPlugin plugin, @NotNull String content, long keepTime, @NotNull List<UUID> target) {
        this(plugin, null, content, keepTime, target);
    }

    public ToppingMessage(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content, long keepTime, @NotNull List<UUID> target) {
        super(plugin, sender, content, keepTime);
        this.target = target;
    }

}
