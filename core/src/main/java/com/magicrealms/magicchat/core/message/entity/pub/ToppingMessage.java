package com.magicrealms.magicchat.core.message.entity.pub;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 置顶消息
 * @date 2025-03-25
 */
@Getter
public class ToppingMessage extends AbstractToppingMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 置顶成员 */
    private final List<UUID> target;

    public ToppingMessage(@Nullable UUID sender, @Nullable String permissionNode, String content, long keepTime, List<UUID> target) {
        super(sender, permissionNode, content, keepTime);
        this.target = target;
    }

}
