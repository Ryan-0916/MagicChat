package com.magicrealms.magicchat.core.format;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.format.entity.FormatDecoration;
import com.magicrealms.magicchat.core.format.entity.FormatEvent;
import com.magicrealms.magicchat.core.format.entity.FormatMessage;
import com.magicrealms.magicchat.core.format.entity.MessageFormatConfig;
import com.magicrealms.magicchat.core.format.enums.FormatRole;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.manage.ConfigManager;
import com.magicrealms.magiclib.common.utils.EnumUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.magicrealms.magicchat.core.MagicChatConstant.YML_FORMAT;

/**
 * @author Ryan-0916
 * @Desc
 * 格式化文件加载器
 * 将 format.yml 文件格式化至 MessageFormatConfig 中
 * @date 2025-04-24
 */
public class FormatConfigLoader {

    private final ConfigManager configManager;

    @Getter
    private final List<MessageFormatConfig> configs = new ArrayList<>();

    public FormatConfigLoader(MagicChat plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllFormatConfig();
    }

    private void loadAllFormatConfig() {
        configManager
                .getYmlSubKeys(YML_FORMAT, "Format", false)
                .ifPresent(keys -> keys.forEach(this::loadFormatConfig));
    }

    private void loadFormatConfig(String configKey) {
        String basePath = "Format." + configKey;
        MessageFormatConfig config = MessageFormatConfig.builder()
                .priority(configManager.getYmlValue(YML_FORMAT, basePath + ".Priority", 0, ParseType.INTEGER))
                .role(EnumUtil.getMatchingEnum(FormatRole.class,
                                configManager.getYmlValue(YML_FORMAT, basePath + ".Role"))
                        .orElse(FormatRole.PLAYER))
                .permissionNode(getYmlValueIfExists(basePath + ".Permission"))
                .prefixes(loadDecorations(basePath + ".Prefix"))
                .suffixes(loadDecorations(basePath + ".Suffix"))
                .message(loadMessage(basePath + ".Message"))
                .build();
        configs.add(config);
    }

    private List<FormatDecoration> loadDecorations(String path) {
        return  getYmlSubKeyIfExists(path)
                    .map(keys -> keys.stream()
                        .map(key -> buildDecoration(path + "." + key))
                        .collect(Collectors.toList()))
                    .orElseGet(ArrayList::new);
    }

    private FormatMessage loadMessage(String path) {
        return FormatMessage.builder()
                .prefix(getYmlValueIfExists(path + ".Prefix"))
                .event(buildEvent(path))
                .build();

    }

    private FormatDecoration buildDecoration(String path) {
        return FormatDecoration.builder()
                .text(getYmlValueIfExists(path + ".Text"))
                .event(buildEvent(path))
                .build();
    }

    private FormatEvent buildEvent(String path) {
        return FormatEvent.builder()
                .url(getYmlValueIfExists(path + ".Url"))
                .copy(getYmlValueIfExists(path + ".Copy"))
                .suggest(getYmlValueIfExists(path + ".Suggest"))
                .insertion(getYmlValueIfExists(path + ".Insertion"))
                .hover(configManager.getYmlListValue(YML_FORMAT, path + ".Hover").orElse(List.of()))
                .command(getYmlValueIfExists(path + ".Command"))
                .build();
    }

    private String getYmlValueIfExists(String path) {
        if (configManager.containsYmlKey(YML_FORMAT, path)) {
            return configManager.getYmlValue(YML_FORMAT, path);
        }
        return StringUtils.EMPTY;
    }

    private Optional<Set<String>> getYmlSubKeyIfExists(String path) {
        if (configManager.containsYmlKey(YML_FORMAT, path)) {
            return configManager.getYmlSubKeys(YML_FORMAT, path, false);
        }
        return Optional.empty();
    }

}
