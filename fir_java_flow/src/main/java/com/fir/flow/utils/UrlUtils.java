package com.fir.flow.utils;

import com.fir.flow.singleton.Singleton;


/**
 * @author fir
 */
public class UrlUtils {


    /**
     * 判断文件地址，决定是否组合url
     * @param url 数据库文件地址
     * @return 完整的文件url / 原url
     */
    public static String estimateUrl(String url){
        if(url != null && !url.equals("")){
            url = Singleton.getSingleton().fileUrl + url;
        }
        return url;
    }
}
