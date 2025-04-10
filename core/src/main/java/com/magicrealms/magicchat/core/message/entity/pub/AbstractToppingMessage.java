package com.magicrealms.magicchat.core.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magicchat.core.message.entity.PublicMessage;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 置顶消息
 * 创建一条置顶消息，它将永远位于聊天框的最下端，直到超出保持时间。
 * 超出保持时间后它将成为一条普通消息。
 * @date 2025-03-26
 */
@Getter
public abstract class AbstractToppingMessage extends PublicMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 保持时间 */
    private final long keepTime;

    public AbstractToppingMessage(@Nullable UUID sender, @Nullable String permissionNode ,String content, long keepTime) {
        super(sender, permissionNode, content);
        this.keepTime = keepTime;
    }

}
