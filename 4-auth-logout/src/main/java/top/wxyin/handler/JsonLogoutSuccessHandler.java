package top.wxyin.handler;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 自定义注销成功处理器
 **/
public class JsonLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // 返回JSON
        response.setContentType("application/json;charset=utf-8");
        // 状态码 200
        response.setStatus(HttpStatus.OK.value());
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("msg", "注销成功");
        result.put("code", 200);
        result.put("data", authentication.getName());
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}