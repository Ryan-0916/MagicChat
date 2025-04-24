package com.magicrealms.magicchat.core.format.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc 前缀/后缀
 * @date 2025-04-24
 */
@Getter
@Builder
public class FormatDecoration {
    private String text;
    private FormatEvent event;
}
