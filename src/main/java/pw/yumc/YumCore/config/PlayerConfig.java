package pw.yumc.YumCore.config;

import java.io.File;

import org.bukkit.entity.Player;

/**
 * 玩家配置管理类
 *
 * @author 喵♂呜
 * @version 1.0
 */
public class PlayerConfig extends FileConfig {
    private static String CONFIG_FOLDER = "userdata";

    /**
     * 获得玩家配置(保存在 CONFIG_FOLDER 文件夹)
     *
     * @param plugin
     *            插件
     * @param playername
     *            玩家名称
     */
    public PlayerConfig(final Player player) {
        this(player.getName());
    }

    /**
     * 获得玩家配置(保存在 CONFIG_FOLDER 文件夹)
     *
     * @param plugin
     *            插件
     * @param player
     *            玩家
     */
    public PlayerConfig(final String playername) {
        super(new File(plugin.getDataFolder(), CONFIG_FOLDER + File.separatorChar + playername + ".yml"));
    }
}
