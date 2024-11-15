package top.wxyin.handler;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 自定义登录成功后直接返回 JSON 的认证处理器
 **/
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 登录成功后直接返回 JSON
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 成功认证的用户信息
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 返回JSON
        response.setContentType("application/json;charset=utf-8");
        // 状态码 200
        response.setStatus(HttpStatus.OK.value());
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("msg", "登录成功");
        result.put("code", 200);
        result.put("data", authentication);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}