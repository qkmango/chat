package cn.qkmango.chat.filter;

import cn.qkmango.chat.chat.utils.WebSocketMapUtil;
import cn.qkmango.chat.setting.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @version 1.0
 * <p>登陆过滤器</p>
 * <p>验证用户是否登陆，登陆则放行，未登录则重定向到登陆页面</p>
 * @className LoginFilter
 * @author: Mango
 * @date: 2021-01-28 16:51
 */
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);
        String path = request.getServletPath();
        System.out.println("=====> LoginFilter "+path);

        if (session != null ||
            "/user/login.do".equals(path)||
            "/user/queryLoginAct.do".equals(path)||
            "/user/register.do".equals(path)){
            chain.doFilter(req,resp);
        } else {
            response.sendRedirect(request.getContextPath()+"/login.html");
        }
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }
}
