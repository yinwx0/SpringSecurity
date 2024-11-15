package top.wxyin.handler;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 自定义登录失败后直接返回 JSON 的认证处理器
 **/
public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 返回JSON
        response.setContentType("application/json;charset=utf-8");
        // 状态码 400
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("msg", "登录失败");
        result.put("code", 400);
        result.put("data", exception.getMessage());
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}