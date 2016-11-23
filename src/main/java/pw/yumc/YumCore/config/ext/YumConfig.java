package pw.yumc.YumCore.config.ext;

import pw.yumc.YumCore.bukkit.Log;
import pw.yumc.YumCore.config.FileConfig;

import java.io.File;
import java.io.IOException;

public class YumConfig {
    protected static String REMOTEFILECENTER = "http://data.yumc.pw/config/";
    protected static String DataFolder = "plugins" + File.separatorChar + "YumCore";

    private static String fromYumc = "配置 %s 来自 YUMC 数据中心...";
    private static String createError = "从 YUMC 数据中心下载配置 %s 失败...";

    private YumConfig() {
    }

    /**
     * 获得本地配置文件
     *
     * @param filename
     *            本地文件名称
     * @return {@link FileConfig}
     */
    public static FileConfig getLocal(String filename) {
        File file = new File(DataFolder, filename);
        return new FileConfig(file);
    }

    /**
     * 获得远程配置文件
     *
     * @param url
     *            配置文件地址
     * @return {@link FileConfig}
     */
    public static FileConfig getRemote(String url) {
        FileConfig config = null;
        try {
            config = new RemoteConfig(REMOTEFILECENTER + url);
        } catch (IOException e) {
            Log.d(e);
        }
        Log.info(String.format(config == null ? createError : fromYumc, url));
        return config;
    }
}
