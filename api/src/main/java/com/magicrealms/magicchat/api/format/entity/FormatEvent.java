package com.magicrealms.magicchat.api.format.entity;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 格式化事件
 * @date 2025-04-24
 */
@Getter
@Builder
public class FormatEvent {

    /* 鼠标悬停文本 */
    private List<String> hover;

    /* 鼠标点击执行的指令 */
    private String command;

    /* 鼠标点击替换输入框内容 */
    private String suggest;

    /* 鼠标点击往输入框后添加内容 */
    private String insertion;

    /* 鼠标点击跳转到 url 地址 */
    private String url;

    /* 鼠标点击后 copy 内容 */
    private String copy;

    public boolean hasHover() {
        return hover != null && !hover.isEmpty();
    }

    public boolean hasCommand() {
        return StringUtils.isNotBlank(command);
    }

    public boolean hasUrl() {
        return StringUtils.isNotBlank(url);
    }

    public boolean hasSuggest() {
        return StringUtils.isNotBlank(suggest);
    }

    public boolean hasInsertion() {
        return StringUtils.isNotBlank(insertion);
    }

    public boolean hasCopy() {
        return StringUtils.isNotBlank(copy);
    }

    public boolean hasAnyClick() {
        return hasCommand() || hasUrl() || hasSuggest() || hasCopy();
    }
}
