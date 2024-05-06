package com.fir.flow.config;

import com.fir.flow.common.exception.DescriptionException;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.sql.SQLException;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 空指针异常
     * @param e NullPointerException
     * @return 400
     */
    @ResponseBody
    @ExceptionHandler(value = NullPointerException.class)
    public AjaxResult nullPointerExceptionHandler(NullPointerException e){
        log.error("空指针异常:",e);
        return AjaxResult.error(AjaxResultCode.FAILED_EXC);
    }


    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public AjaxResult exception(Exception e){
        log.error("捕获到自处理异常:",e);
        return AjaxResult.error(AjaxResultCode.FAILED_EXC);
    }


    @ResponseBody
    @ExceptionHandler(value = SQLException.class)
    public AjaxResult communicationsException(SQLException e){
        log.error("数据库连接异常:",e);
        return AjaxResult.error(AjaxResultCode.FAILED_EXC);
    }

    @ResponseBody
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public AjaxResult maxUploadSizeExceededException(MaxUploadSizeExceededException e){
        log.error("上传文件大小超出限制: ",e);
        return AjaxResult.error(AjaxResultCode.FAILED_EXC);
    }


    /**
     * 自定义异常捕捉描述
     *
     * @param e 自定义异常
     * @return 像前端抛出异常描述的Ajax返回值对象
     */
    @ResponseBody
    @ExceptionHandler(value = DescriptionException.class)
    public AjaxResult descriptionException(DescriptionException e){
        String message = e.getMessage();
        log.error(message);
        return AjaxResult.error(message);
    }

}
