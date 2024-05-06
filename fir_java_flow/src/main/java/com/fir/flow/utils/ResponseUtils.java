package com.fir.flow.utils;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author fir
 */
@Slf4j
public class ResponseUtils {

    /**
     * 设置excel下载响应头属性
     */
    public static void setExcelRespProp(HttpServletResponse response, String rawFileName) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setCharacterEncoding("utf-8");
        String fileName = "data";
        try {
            fileName = URLEncoder.encode(rawFileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.info("文件名称转码失败 {}", e.getMessage());
        }

        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName + ".xlsx");
    }
}
