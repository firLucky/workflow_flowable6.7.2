package com.fir.flow.utils.system;

import com.alibaba.fastjson.JSONObject;
import com.fir.flow.entity.user.User;
import com.fir.flow.singleton.Singleton;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


/**
 * 根据token获取用户数据
 * @author sddl-yj
 */
public class UserInfoTools {


    /**
     * 从请求头中获取token数据
     * 
     * @return token
     */
    private static String getToken(){

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader(Singleton.getSingleton().TOKEN_HEADER_NAME);
    }
    

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    public static User userInfo(){
        String token = getToken();
        User user = new User();
        JSONObject jsonObject = (JSONObject) Singleton.getSingleton().timerExpireHashMap.get(token);
        if(jsonObject != null) {
            user = jsonObject.toJavaObject(User.class);
        }

        return user;
    }


    /**
     * 获取用户所属公司
     * 
     * @return 用户所属公司 / 空字符串
     */
    public static String userOrg(){
        String token = getToken();
        String orgName = "";
        JSONObject jsonObject = (JSONObject) Singleton.getSingleton().timerExpireHashMap.get(token);
        if(jsonObject != null) {
            User user = jsonObject.toJavaObject(User.class);
            orgName = user.getOrgName();
        }
        return orgName;
    }



    /**
     * 获取用户姓名
     * 
     * @return 用户姓名 / 空字符串
     */
    public static String userName(){
        String token = getToken();
        String userName = "";
        JSONObject jsonObject = (JSONObject) Singleton.getSingleton().timerExpireHashMap.get(token);
        if(jsonObject != null) {
            User user = jsonObject.toJavaObject(User.class);
            userName = user.getName();
        }

        return userName;
    }



    /**
     * 获取用户id
     * 
     * @return 用户id / 空字符串
     */
    public static String userId(){
        String token = getToken();
        String userId = "";
        JSONObject jsonObject = (JSONObject) Singleton.getSingleton().timerExpireHashMap.get(token);
        if(jsonObject != null) {
            User user = jsonObject.toJavaObject(User.class);
            userId = user.getUserid();
        }

        return userId;
    }


    /**
     * 获取当前用户所在部门
     * 
     * @return 公司等级 / 空字符串
     */
    public static String deptName(){
        String token = getToken();
        String departmentName = null;
        JSONObject jsonObject = (JSONObject) Singleton.getSingleton().timerExpireHashMap.get(token);
        if(jsonObject != null) {
            User user = jsonObject.toJavaObject(User.class);
            departmentName = user.getDeptName();
        }

        return departmentName;
    }
}
