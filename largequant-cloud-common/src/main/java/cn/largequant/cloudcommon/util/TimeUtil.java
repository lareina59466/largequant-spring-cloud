package cn.largequant.cloudcommon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(new Date());
    }
}
