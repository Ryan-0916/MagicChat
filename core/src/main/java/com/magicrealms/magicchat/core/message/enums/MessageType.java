package com.magicrealms.magicchat.core.message.enums;

/**
 * @author Ryan-0916
 * @Desc
 * 消息的类型
 * 定义了消息的全部种类
 * @date 2025-03-24
 */
public enum MessageType {

    /* 公共消息 */
    PUBLIC,

    /* 私有消息 */
    PRIVATE,

    /* 置顶消息 */
    TOPPING,

    /* 全员置顶消息 */
    TOPPING_ALL,

    /* 艾特消息 */
    AT,

    /* 全员艾特消息 */
    AT_ALL
}
