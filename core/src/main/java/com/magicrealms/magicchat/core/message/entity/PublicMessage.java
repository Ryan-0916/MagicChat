package com.magicrealms.magicchat.core.message.entity;

import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 公共消息 - 此消息全员可见
 * @date 2025-03-25
 */
@Getter
public class PublicMessage extends AbstractMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 拥有某项权限的玩家可见 */
    private final String permissionNode;

    public PublicMessage(@Nullable UUID sender, String permissionNode, String content) {
        super(sender, content);
        this.permissionNode = permissionNode;
    }

    @Override
    protected String handleMessage(@Nullable UUID sender, String content) {
        return AdventureHelper.serializeComponent(
                AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(content)));
    }

}
