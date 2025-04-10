package com.magicrealms.magicchat.core.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 全员置顶消息
 * @date 2025-03-25
 */
@Getter
public class ToppingAllMessage extends AbstractToppingMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    public ToppingAllMessage(@Nullable UUID sender, @Nullable String permissionNode, String content, long keepTime) {
        super(sender, permissionNode, content, keepTime);
    }

}
