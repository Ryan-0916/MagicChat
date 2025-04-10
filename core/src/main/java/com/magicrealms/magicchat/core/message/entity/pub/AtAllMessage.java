package com.magicrealms.magicchat.core.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 艾特全体频道内成员消息
 * @date 2025-03-25
 */
public class AtAllMessage extends ToppingAllMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    public AtAllMessage(@Nullable UUID sender, @Nullable String permissionNode,  String content, long keepTime) {
        super(sender, permissionNode, content, keepTime);
    }

}
