package pw.yumc.YumCore.tellraw;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pw.yumc.YumCore.bukkit.Log;

/**
 *
 * @since 2016年9月9日 下午3:47:17
 * @author 喵♂呜
 */
public abstract class ItemSerialize {
    static ItemSerialize itemSerialize = new Manual();
    static {
        try {
            itemSerialize = new Automatic();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            itemSerialize = new Manual();
            Log.debug("初始化自动物品序列化失败!", e);
        }
    }

    public static String $(final ItemStack item) {
        return itemSerialize.parse(item);
    }

    public abstract String parse(final ItemStack item);

    static class Automatic extends ItemSerialize {
        Method asNMSCopyMethod;
        Method nmsSaveNBTMethod;
        Class<?> nmsNBTTagCompound;
        String ver = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

        public Automatic() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
            final Class<?> cis = getOBCClass("inventory.CraftItemStack");
            asNMSCopyMethod = cis.getMethod("asNMSCopy", ItemStack.class);
            final Class<?> nmsItemStack = getNMSClass("ItemStack");
            nmsNBTTagCompound = getNMSClass("NBTTagCompound");
            nmsSaveNBTMethod = nmsItemStack.getMethod("save", nmsNBTTagCompound);
        }

        public Class<?> getNMSClass(final String cname) throws ClassNotFoundException {
            return Class.forName("net.minecraft.server." + ver + "." + cname);
        }

        public Class<?> getOBCClass(final String cname) throws ClassNotFoundException {
            return Class.forName("org.bukkit.craftbukkit." + ver + "." + cname);
        }

        @Override
        public String parse(final ItemStack item) {
            try {
                return new JsonBuilder((nmsSaveNBTMethod.invoke(asNMSCopyMethod.invoke(null, item), nmsNBTTagCompound.newInstance()).toString())).toString();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                itemSerialize = new Manual();
                return itemSerialize.parse(item);
            }
        }
    }

    static class Manual extends ItemSerialize {

        @Override
        public String parse(final ItemStack item) {
            return serialize(item);
        }

        /**
         * 显示序列化
         *
         * @param im
         *            物品属性
         * @return 获取显示序列化
         */
        private String getDisplay(final ItemMeta im) {
            final StringBuffer display = new StringBuffer();
            display.append("{");
            if (im.hasDisplayName()) {
                display.append(String.format("Name:\"%s\",", im.getDisplayName()));
            }
            if (im.hasLore()) {
                display.append("Lore:[");
                int i = 0;
                for (final String line : im.getLore()) {
                    display.append(String.format("%s:%s,", i, new JsonBuilder(line).toString()));
                    i++;
                }
                display.deleteCharAt(display.length() - 1);
                display.append("],");
            }
            display.deleteCharAt(display.length() - 1);
            display.append("}");
            return display.toString();
        }

        /**
         * 附魔序列化
         *
         * @param set
         *            附魔集合
         * @return 获得附魔序列化
         */
        private String getEnch(final Set<Entry<Enchantment, Integer>> set) {
            final StringBuffer enchs = new StringBuffer();
            for (final Map.Entry<Enchantment, Integer> ench : set) {
                enchs.append(String.format("{id:%s,lvl:%s},", ench.getKey().getId(), ench.getValue()));
            }
            enchs.deleteCharAt(enchs.length() - 1);
            return enchs.toString();
        }

        /**
         * 属性序列化
         *
         * @param im
         *            物品属性
         * @return 获得属性序列化
         */
        private String getTag(final ItemMeta im) {
            final StringBuffer meta = new StringBuffer("{");
            if (im.hasEnchants()) {
                meta.append(String.format("ench:[%s],", getEnch(im.getEnchants().entrySet())));
            }
            im.getItemFlags();
            if (im.hasDisplayName() || im.hasLore()) {
                meta.append(String.format("display:%s,", getDisplay(im)));
            }
            meta.deleteCharAt(meta.length() - 1);
            meta.append("}");
            return meta.toString();
        }

        /**
         * 序列化物品
         *
         * @param item
         *            {@link ItemStack}
         * @return 物品字符串
         */
        private String serialize(final ItemStack item) {
            final JsonBuilder json = new JsonBuilder("{");
            json.append(String.format("id:\"%s\",Damage:\"%s\"", item.getTypeId(), item.getDurability()));
            if (item.getAmount() > 1) {
                json.append(String.format(",Count:%s", item.getAmount()));
            }
            if (item.hasItemMeta()) {
                json.append(String.format(",tag:%s", getTag(item.getItemMeta())));
            }
            json.append("}");
            return json.toString();
        }
    }
}