package pw.yumc.YumCore.mc;

/**
 * Minecraft工具类
 * 
 * @author 喵♂呜
 * @since 2017/1/26 0026
 */
public class MinecraftTools {
    /**
     * 获得服务器信息
     * 
     * @param address
     *            服务器地址
     * @return {@link ServerInfo} 服务器信息
     */
    public static ServerInfo getServerInfo(String address) {
        if (address.contains(":")) {
            String[] args = address.split(":");
            return new ServerInfo(args[0], Integer.parseInt(args[1]));
        } else {
            return new ServerInfo(address);
        }
    }
}
