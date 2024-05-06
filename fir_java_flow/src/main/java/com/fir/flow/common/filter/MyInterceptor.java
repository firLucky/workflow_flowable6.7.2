package com.fir.flow.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import com.fir.flow.singleton.Singleton;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author 18714
 */
@Component
public class MyInterceptor implements HandlerInterceptor {
 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在请求处理前进行拦截处理

        String token = request.getHeader(Singleton.getSingleton().TOKEN_HEADER_NAME);

        if(!"123".equals(token)) {
            JSONObject jsonObject = (JSONObject) Singleton.getSingleton().timerExpireHashMap.get(token);
            if (jsonObject == null) {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(200);
                response.getWriter().write(JSONObject.toJSONString(AjaxResult.error(AjaxResultCode.ACCESS_DENIED)));
                // 返回false时，不会再进入自己配置的addCorsMappings跨域方法，所以在此进行跨域的配置
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Headers", "*");
                return false;
            }
        }
        // 如果返回false表示请求被拦截，不会执行后续处理
        return true;
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在请求处理后进行拦截处理
    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在请求处理完成后进行拦截处理
    }
}
