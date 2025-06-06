package com.magicrealms.magicchat.common;

/**
 * @author Ryan-0916
 * @Desc 常量
 * @date 2025-04-03
 */
public class MagicChatConstant {

    /** 插件所需 */
    /* 插件名称 */
    public static String PLUGIN_NAME = "MagicChat";
    /* 消息记录最大长度 */
    public static final int MAX_HISTORY_SIZE = 100;

    /** 配置文件部分常量 */
    public static final String YML_CONFIG = "config";
    public static final String YML_REDIS = "redis";
    public static final String YML_LANGUAGE = "language";
    public static final String YML_FORMAT = "format";

    /** Redis 相关 key */
    public static final String BUNGEE_CHANNEL = "BUNGEE_CHANNEL_MAGIC_CHAT";
    /* 成员频道 */
    public static final String MEMBERS_CHANNEL = "MAGIC_CHAT_MEMBERS_CHANNEL";
    /* 成员聊天记录 */
    public static final String MEMBER_MESSAGE_HISTORY = "MAGIC_CHAT_MEMBER_MESSAGE_HISTORY_%s";
    /* 频道聊天记录 */
    public static final String CHANNEL_MESSAGE_HISTORY = "MAGIC_CHAT_CHANNEL_MESSAGE_HISTORY_%s";
    /* 频道专属发布/订阅 KEY */
    public static final String BUNGEE_CHANNEL_CHAT = "MAGIC_CHAT_BUNGEE_CHANNEL_%s";
    /* MAGIC_LIB_DIALOG 文本 */
    public static final String DIALOG_PATH = "MAGIC_LIB_DIALOG";
    /* 重置玩家 DIALOG 时分布式锁 */
    public static final String RESET_DIALOG_LOCK = "MAGIC_CHAT_RESET_DIALOG_LOCK_%s";
}
