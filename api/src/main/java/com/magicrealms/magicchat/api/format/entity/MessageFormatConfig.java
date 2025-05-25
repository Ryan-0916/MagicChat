package com.magicrealms.magicchat.api.format.entity;

import com.magicrealms.magicchat.api.format.enums.FormatRole;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 格式化配置
 * @date 2025-04-24
 */
@Getter
@Builder
public class MessageFormatConfig {
    /* 优先级 */
    private int priority;
    /* 格式化角色 */
    private FormatRole role;
    /* 权限节点 */
    private String permissionNode;
    /* 前缀 */
    private List<FormatDecoration> prefixes;
    /* 格式化消息 */
    private FormatMessage message;
    /* 后缀 */
    private List<FormatDecoration> suffixes;
}
