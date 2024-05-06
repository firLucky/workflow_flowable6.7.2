package com.fir.flow.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author dpe
 */
public class DateToolUtils {
    private static final Logger log = LoggerFactory.getLogger(DateToolUtils.class);


    /**
     * 根据时间判断是星期几
     *
     * @param strDate yyyy-MM-dd
     * @return 例如：星期一
     */
    public static String getWeek(String strDate) {
        String week = "";
        try {
            //将'/'替换为'-'
            strDate = strDate.replace("/", "-");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(strDate);
            String[] weekDataName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            week = weekDataName[intWeek];
        } catch (ParseException e) {
            log.error("未能识别为 yyyy-MM-dd 的时间格式数据 {} ", strDate);
        }
        return week;
    }


    /**
     * 拆分时间格式为 yyyy-MM-dd 和 HH:mm:ss
     *
     * @param date yyyy-MM-dd HH:mm:ss 格式的日期
     * @return 返回 date 与 time 为k的日期与时间
     */
    public static HashMap<String, String> dateSplit(String date) {
        HashMap<String, String> map = new HashMap<>();
        //try {
            String[] strLis = date.split(" ");
            map.put("date", strLis[0]);
            map.put("time", strLis[1]);
//        } catch (NullPointerException e) {
//            log.error("未能识别的时间格式数据 {} 需确认是否为 yyyy-MM-dd HH:mm:ss ", date);
//            map.put("date", "");
//            map.put("time", "");
//        }
        return map;
    }


    /**
     * 获取当前日期(yyyy-MM-dd)
     *
     * @return yyyy-MM-dd
     */
    public static String newDate() {
        String str;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        str = sdf.format(date);
        return str;
    }

    /**
     * 转换时间格式(yyyy-MM-dd)
     *
     * @return yyyy年MM月dd
     */
    public static String changeDate(String strDate) {
        String str = "";
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd");
            Date date = sdfDate.parse(strDate);
            str = sdf.format(date);
        } catch (ParseException e) {
            log.error("异常");
        }
        return str;
    }

    /**
     * 获取当前日期(yyyy-MM-dd HH:mm:ss)
     *
     * @return yyyy-MM-dd
     */
    public static String newDateTime() {
        String str;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        str = sdf.format(date);
        return str;
    }


    /**
     * 获取当前时间是上午还是下午
     *
     * @return 0上午, 1下午
     */
    public static int newTimeClock() {
        int sate = 0;
        try {
            Date date = new Date();

            long dateLong = date.getTime();  // 当前时间戳
            SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dayStr = day.format(date);
            String afternoon = dayStr + " 12:00:00";
            long afternoonLong = dateTime.parse(afternoon).getTime();  // 中午时间戳
            // 判断当前时间是否为下午
            if (dateLong > afternoonLong) {
                sate = 1;
            }
        } catch (ParseException e) {
            log.error("异常");
        }
        return sate;
    }


    /**
     * 获取明天的日期(yyyy-MM-dd)
     *
     * @return yyyy-MM-dd
     */
    public static String tomorrowDate() {
        String str;
        Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        str = sdf.format(date);
        return str;
    }


    /**
     * 获取昨天的日期(yyyy-MM-dd)
     *
     * @return yyyy-MM-dd
     */
    public static String yesterdayDate() {
        String str;
        Date date = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        str = sdf.format(date);
        return str;
    }


    /**
     * 获取指定日期的前推一天的日期(yyyy-MM-dd)
     *
     * @param date yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    public static String hisAndYesterdayDate(String date) {
        String str = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date oldDate = sdf.parse(date);
            long oldDateLong = oldDate.getTime() - 1000 * 60 * 60 * 24;  // 获取指定日期的前一天
            str = sdf.format(oldDateLong);
        } catch (ParseException e) {
            log.error("异常");
        }
        return str;
    }


    /**
     * 获取指定日期的后推一天的日期(yyyy-MM-dd)
     *
     * @param date yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    public static String hisAndTomorrowDate(String date) {
        String str = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date oldDate = sdf.parse(date);
            long oldDateLong = oldDate.getTime() + 1000 * 60 * 60 * 24;  // 获取指定日期的前一天
            str = sdf.format(oldDateLong);
        } catch (ParseException e) {
            log.error("异常");
        }
        return str;
    }


    /**
     * 获取一个月的所有日期集合
     *
     * @param date yyyy-MM
     * @return 日期集合 yyyy-MM-dd
     */
    public static List<String> monthDatetime(String date) {
        ArrayList<String> list = new ArrayList<>();
        String[] dateInfo = date.split("-");
        if (dateInfo.length > 1) {
            int year = Integer.parseInt(dateInfo[0]);
            int month = Integer.parseInt(dateInfo[1]);
            int sum = monthSumDay(date);

            for (int num = 1; num <= sum; num++) {
                String dayTime = year + "-";

                if (month >= 10) {
                    dayTime = dayTime + month + "-";
                } else {
                    dayTime = dayTime + "0" + month + "-";
                }

                if (num >= 10) {
                    dayTime = dayTime + num;
                } else {
                    dayTime = dayTime + "0" + num;
                }
                list.add(dayTime);
            }
        }

        return list;
    }


    /**
     * 获取一个月的天数
     *
     * @param date yyyy-MM
     */
    public static Integer monthSumDay(String date) {
        int sum = 0;
        String[] dateInfo = date.split("-");
        if (dateInfo.length > 1) {
            int year = Integer.parseInt(dateInfo[0]);
            int month = Integer.parseInt(dateInfo[1]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DATE, 1);
            calendar.roll(Calendar.DATE, -1);
            sum = calendar.get(Calendar.DATE);
        }
        return sum;
    }


    /**
     * 获取当前年
     *
     * @return yyyy
     */
    public static String year() {
        String str;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        str = sdf.format(date);
        return str;
    }


    /**
     * 获取当前年月
     *
     * @return yyyy-MM
     */
    public static String monthYear() {
        String str;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        str = sdf.format(date);
        return str;
    }


    /**
     * 获取当前月日
     *
     * @return MM-dd
     */
    public static String monthDay() {
        String str;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        str = sdf.format(date);
        return str;
    }


    /**
     * 获取当前月日时分
     *
     * @return MM-dd HH:mm
     */
    public static String dayMonth() {
        String str;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        str = sdf.format(date);

        return str;
    }


    /**
     * 获取明天相同时间, 月日时分
     *
     * @return MM-dd HH:mm
     */
    public static String tomorrowDayMonth() {
        String str;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        str = sdf.format(date);

        return str;
    }


    /**
     * excel时间转化 yyyy/MM -> yyyy-MM
     *
     * @return MM-dd
     */
    public static String formatExcelYYYYHH(String time) {
        String str = "";
        if (StringUtils.isNoneBlank(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat excelSdf = new SimpleDateFormat("yyyy/MM");
          try {
            Date oldDate = excelSdf.parse(time);
            str = sdf.format(oldDate.getTime());
          } catch (ParseException e) {
              log.info("异常");
        }
      } else {
            str = time;
        }
        return str;
    }


    /**
     * excel时间转化 MM/dd -> MM-dd
     *
     * @return MM-dd
     */
    public static String formatExcelHHdd(String time) {
        String str = "";
        if (StringUtils.isNoneBlank(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            SimpleDateFormat excelSdf = new SimpleDateFormat("MM/dd");
            try {
                Date oldDate = excelSdf.parse(time);
                str = sdf.format(oldDate.getTime());
            } catch (ParseException e) {
                log.info("异常");
            }
        } else {
            str = time;
        }
        return str;
    }


    /**
     * excel时间转化 MM/dd HH:mm:ss -> MM-dd HH:mm:ss
     *
     * @return MM-dd
     */
    public static String formatExcelHHddTime(String time) {
        String str = "";
        if (StringUtils.isNoneBlank(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            SimpleDateFormat excelSdf = new SimpleDateFormat("MM/dd HH:mm");
            try {
                Date oldDate = excelSdf.parse(time);
                str = sdf.format(oldDate.getTime());
            } catch (ParseException e) {
                log.info("异常");
            }
        } else {
            str = time;
        }
        return str;
    }


    /**
     * excel时间转化 MM/dd HH:mm:ss -> MM-dd HH:mm:ss
     *
     * @return MM-dd
     */
    public static String formatYYYYHHddTime(String time) {
        String str = "";
        if (StringUtils.isNoneBlank(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
            try {
                Date oldDate = sdf.parse(time);
                str = sdf.format(oldDate.getTime());
            } catch (ParseException e) {
                log.info("异常");
            }
        } else {
            str = time;
        }
        return str;
    }


    /**
     * date转换为Str(yyyy-MM-dd HH:mm:ss)
     *
     * @return yyyy-MM-dd
     */
    public static String dateFormatStr(Date date) {
        String str = "";
        if(date != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = sdf.format(date);
        }else {
            log.error("数据date为空无法处理[{}]", date);
        }
        return str;
    }


    /**
     * yyyy-MM-dd HH:mm:ss转换为Date
     *
     * @return yyyy-MM-dd
     */
    public static Date strFormatDate(String str) {
        Date date = null;
        if(str != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(str);
            }catch (ParseException e){
                log.error("[{}] 无法处理时间格式", str);
            }
        }else {
            log.error("数据date为空无法处理");
        }
        return date;
    }
}
