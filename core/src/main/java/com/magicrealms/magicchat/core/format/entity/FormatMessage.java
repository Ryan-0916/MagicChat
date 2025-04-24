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
    private String defaultColor;
    private FormatEvent event;
}
