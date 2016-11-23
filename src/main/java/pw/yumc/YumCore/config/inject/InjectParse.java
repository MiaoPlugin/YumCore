package pw.yumc.YumCore.config.inject;

import org.bukkit.configuration.ConfigurationSection;
import pw.yumc.YumCore.config.exception.ConfigParseException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注入配置类解析
 *
 * @author 喵♂呜
 * @since 2016年10月16日 下午3:26:48
 */
public class InjectParse {
    private static String DATE_PARSE_ERROR = "配置节点 {0} 日期解析失败 格式应该为: {1} 但输入值为: {2}!";
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static DateFormat df = new SimpleDateFormat(DATE_FORMAT);
    private static Map<Class, Parse> allparse = new HashMap<>();

    static {
        new ListParse();
        new MapParse();
        new DateParse();
        new DateFormatParse();
    }

    public static Object parse(Class clazz, ConfigurationSection config, String path) {
        return allparse.containsKey(clazz) ? allparse.get(clazz).parse(config, path) : null;
    }

    /**
     * 配置解析注册
     *
     * @param clazz
     *            类
     * @param parse
     *            解析类
     */
    public static void register(Class clazz, Parse parse) {
        allparse.put(clazz, parse);
    }

    public static class DateParse implements Parse<Date> {
        public DateParse() {
            allparse.put(Date.class, this);
        }

        @Override
        public Date parse(ConfigurationSection config, String path) {
            String value = "0000-00-00 00:00:00";
            try {
                value = config.getString(path);
                return df.parse(value);
            } catch (ParseException e) {
                throw new ConfigParseException(String.format(DATE_PARSE_ERROR, path, DATE_FORMAT, value), e);
            }
        }
    }

    public static class DateFormatParse implements Parse<DateFormat> {
        public DateFormatParse() {
            allparse.put(DateFormat.class, this);
        }

        @Override
        public DateFormat parse(ConfigurationSection config, String path) {
            return new SimpleDateFormat(config.getString(path));
        }
    }
    public static class ListParse implements Parse<List> {
        public ListParse() {
            allparse.put(List.class, this);
        }

        @Override
        public List parse(ConfigurationSection config, String path) {
            return config.getList(path);
        }
    }

    public static class MapParse implements Parse<Map> {
        public MapParse() {
            allparse.put(Map.class, this);
        }

        @Override
        public Map parse(ConfigurationSection config, String path) {
            return config.getConfigurationSection(path).getValues(false);
        }
    }

    public interface Parse<FC> {
        FC parse(ConfigurationSection config, String path);
    }
}
