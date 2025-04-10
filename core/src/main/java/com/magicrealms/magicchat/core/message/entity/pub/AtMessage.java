package com.magicrealms.magicchat.core.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 艾特消息
 * @date 2025-03-25
 */
public class AtMessage extends ToppingMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    public AtMessage(@Nullable UUID sender, @Nullable String permissionNode, String content, long keepTime, List<UUID> target) {
        super(sender, permissionNode, content, keepTime, target);
    }

}
