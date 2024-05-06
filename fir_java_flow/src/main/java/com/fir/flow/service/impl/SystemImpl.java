package com.fir.flow.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import com.fir.flow.singleton.Singleton;
import com.fir.flow.entity.user.User;
import com.fir.flow.mapper.SystemMapper;
import com.fir.flow.service.ISystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;


/**
 * @author fir
 * @date 2022/10/4 23:23
 */
@Slf4j
@Service
public class SystemImpl implements ISystemService {


    @Resource
    private SystemMapper systemMapper;


    /**
     * 读取注入配置文件jwt密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 用户登录
     *
     * @param username 用户名称
     * @param password 用户密码
     * @return 登录成功以及信息/登录失败
     */
    @Override
    public AjaxResult login(String username, String password){
        AjaxResult result;

        // 账户信息登录
        HashMap<Integer, Object> map = authenticate(username, password);

        int state = Integer.parseInt(map.get(0).toString());
        if(state == 1){
            User user = (User) map.get(2);
            // 生成token
            String token = generateToken(user);
            HashMap<String, Object> resultMap = new HashMap<>(2);
            resultMap.put("token", token);
            resultMap.put("name", user.getName());
            result = AjaxResult.success(AjaxResultCode.SUCCESS_LOGIN, resultMap);
        }else {
            result = AjaxResult.error(map.get(1).toString());
        }
        return result;
    }


    /**
     * 判断当前用户是否登陆成功
     * @param username 用户ID
     * @param password 用户密码
     * @return map key中存储 0：登陆成功/失败，1：错误描述，2：用户实体
     */
    private HashMap<Integer, Object> authenticate(String username, String password){
        User user = null;
        String mag = "";
        HashMap<Integer, Object> map = new HashMap<>(2);
        int key = 0;

        // 判断用户是否存在，用户密码是否正确（在其他方式下，可通过数据库，其他系统判断用户是否登录成功）
        Integer isPass = systemMapper.checkUsername(username);
        if(isPass > 0){
            user = systemMapper.checkLogin(username, password);
            if(user != null){
                key = 1;
            }else {
                mag = "用户密码不正确";
            }
        }else {
            mag = "用户不存在";
        }
        map.put(0, key);
        map.put(1, mag);

        // 生成用户信息实体（在数据库连接下， 查询出用户基本信息）
        if(key == 1){
            map.put(2, user);
        }

        return map;
    }


    private String generateToken(User user){
        //SECRET_KEY自定义密钥
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ","JWT");
        header.put("alg","HS256");
        // 取时间
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //把日期往后增加一天,整数  往后推,负数往前移动
        calendar.add(Calendar.DATE,1);
        // 这个时间就是日期往后推一天的结果
        date = calendar.getTime();

        // 生成 Token
        String token = JWT.create().withHeader(header)
                .withClaim("userId", user.getName())
                .withExpiresAt(date)
                .sign(algorithm);

        if (StringUtils.isNoneBlank(token)){
            token = "Bearer " + token;
        }

        Singleton.getSingleton().timerExpireHashMap.put(token, JSONObject.toJSON(user));

        return token;
    }


    /**
     * 用户登出
     *
     * @param request 请求头
     */
    @Override
    public void logout(HttpServletRequest request){
        String token = request.getHeader("Authorization");

        Singleton.getSingleton().timerExpireHashMap.remove(token);
    }
}
