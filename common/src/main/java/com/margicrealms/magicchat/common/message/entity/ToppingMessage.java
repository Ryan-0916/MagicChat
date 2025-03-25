package com.margicrealms.magicchat.common.message.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 置顶消息
 * @date 2025-03-25
 */
@Getter
public class ToppingMessage extends PrivateMessage {

    /** 保持时间 */
    private long keepTime;

    public ToppingMessage(@NotNull String content, @NotNull UUID exclusive, long keepTime) {
        this(null, content, exclusive, keepTime);
    }

    public ToppingMessage(@Nullable UUID sender, @NotNull String content, @NotNull UUID exclusive, long keepTime) {
        super(sender, content, exclusive);
        this.keepTime = keepTime;
    }



}
