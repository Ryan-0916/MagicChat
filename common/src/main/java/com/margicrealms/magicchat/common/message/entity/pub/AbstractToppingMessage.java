package com.margicrealms.magicchat.common.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.margicrealms.magicchat.common.message.entity.PublicMessage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 抽象置顶消息
 * @date 2025-03-26
 */
@Getter
public abstract class AbstractToppingMessage extends PublicMessage {
    /** 保持时间 */
    private final long keepTime;

    public AbstractToppingMessage(@NotNull MagicRealmsPlugin plugin, @Nullable UUID sender, @NotNull String content, long keepTime) {
        super(plugin, sender, content);
        this.keepTime = keepTime;
    }

}
