package com.magicrealms.magicchat.core.message.entity;

import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 私有消息，该消息仅玩家本人可见
 * @date 2025-03-25
 */
@Getter
public class PrivateMessage extends AbstractMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 消息的接收者者 */
    private final UUID exclusive;

    public PrivateMessage(@Nullable UUID sender, String content, UUID exclusive) {
        super(sender, content);
        this.exclusive = exclusive;
    }

    @Override
    protected String handleMessage(@Nullable UUID sender, String content) {
        return AdventureHelper.serializeComponent(
                AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(content)));
    }


}
