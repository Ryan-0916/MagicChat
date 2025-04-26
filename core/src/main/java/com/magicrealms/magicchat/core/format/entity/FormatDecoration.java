package com.magicrealms.magicchat.core.format.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc 装饰
 * @date 2025-04-24
 */
@Getter
@Builder
public class FormatDecoration {
    /* 文本 */
    private String text;
    /* 事件 */
    private FormatEvent event;
}
