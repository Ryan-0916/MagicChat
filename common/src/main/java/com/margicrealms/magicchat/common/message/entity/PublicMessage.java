package com.margicrealms.magicchat.common.message.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 公共消息 - 此消息全员可见
 * @date 2025-03-25
 */
public class PublicMessage extends AbstractMessage {

    public PublicMessage(@NotNull String content) {
        this(null, content);
    }

    /**
     * 创建一条公共消息
     * @param
     * @param content 消息的原始内容
     */
    public PublicMessage(@Nullable UUID sender, @NotNull String content) {
        super(sender, content);
    }

    @Override
    protected String handleMessage(@Nullable UUID sender, @NotNull String content) {
        return "";
    }

}
