package com.magicrealms.magicchat.core.format.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc 格式化消息
 * @date 2025-04-24
 */
@Getter
@Builder
public class FormatMessage {
    /* 前缀 */
    private String prefix;
    /* 事件 */
    private FormatEvent event;
}
