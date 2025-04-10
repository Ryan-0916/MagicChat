package com.magicrealms.magicchat.core.exception;

/**
 * @author Ryan-0916
 * @Desc
 * 自定义异常：HistoryStorageAlreadyExistsException
 * 当频道已存在时抛出此异常。通常在创建新频道时，如果指定的频道已经存在，程序会抛出此异常来阻止重复创建。
 * @date 2025-04-08
 */
public class HistoryStorageAlreadyExistsException extends RuntimeException {

    public HistoryStorageAlreadyExistsException(String message) {
        super(message);
    }

}
