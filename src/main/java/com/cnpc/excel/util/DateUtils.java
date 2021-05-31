package com.cnpc.excel.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @packageName： com.cnpc.excel.util
 * @className: DateUtils
 * @description: TODO
 * @version: v1.0
 * @author: liuzhichao
 * @date: 2020-12-14 14:36
 */
public class DateUtils {

    /**
     *  获取当前日期 格式 yyyy-MM-dd
     * @param date
     * @return
     */
    public static String getNowDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}
