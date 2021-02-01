package cn.qkmango.chat.setting.web.controller;

import cn.qkmango.chat.chat.utils.WebSocketMapUtil;
import cn.qkmango.chat.chat.utils.httpsesson.HttpSessionUtil;
import cn.qkmango.chat.chat.web.controller.ChatWebSocket;
import cn.qkmango.chat.setting.domain.User;
import cn.qkmango.chat.setting.exception.LoginException;
import cn.qkmango.chat.setting.exception.RegisterException;
import cn.qkmango.chat.setting.service.UserService;
import cn.qkmango.chat.setting.service.impl.UserServiceImpl;
import cn.qkmango.chat.utils.PrintJson;
import cn.qkmango.chat.utils.ServiceFactory;
import cn.qkmango.chat.utils.UUIDUtil;
import sun.misc.UUDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

/**
 * @version 1.0
 * <p>登陆控制器</p>
 * @className LoginController
 * @author: Mango
 * @date: 2021-01-30 13:53
 */


public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("=====> 进入到登陆控制器");

        String path = request.getServletPath();
        if ("/user/login.do".equals(path)) {
            login(request,response);
        } else if ("/user/queryLoginAct.do".equals(path)) {
            queryLoginAct(request,response);
        } else if ("/user/register.do".equals(path)) {
            register(request,response);
        } else if ("/user/logout.do".equals(path)) {
            logout(request,response);
        }
    }

    /**
     * 退出，注销session
     * @param request
     * @param response
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);

        httpSession.invalidate();

        for (HttpSession value : HttpSessionUtil.httpSessionMap.values()) {
            System.out.println("HttpSession = " + value);
        }

        for (ChatWebSocket value : WebSocketMapUtil.webSocketMap.values()) {
            System.out.println("ChatWebSocket = " + value);
        }


        System.out.println("logout invalidate");
        // System.out.println("=====> httpSession = " + httpSession);
    }

    /**
     * 注册
     * @param request
     * @param response
     */
    private void register(HttpServletRequest request, HttpServletResponse response) {

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        String name = request.getParameter("name");

        User user = new User();
        user.setLoginAct(loginAct);
        user.setLoginPwd(loginPwd);
        user.setName(name);
        user.setId(UUIDUtil.getUUID());

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        boolean flag = false;

        try {
            flag = us.register(user);
        } catch (RegisterException e) {
            e.printStackTrace();
        }
        PrintJson.printJsonFlag(response,flag);
        System.out.println("=====> register ");
    }


    /**
     * 查询账户是否存在可用，{success:true} 不存在，账户可用
     * @param request
     * @param response
     */
    private void queryLoginAct(HttpServletRequest request, HttpServletResponse response) {

        String loginAct = request.getParameter("loginAct");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        boolean flag = us.queryLoginAct(loginAct);

        System.out.println("=====> queryLoginAct "+flag);
        PrintJson.printJsonFlag(response,flag);
    }


    /**
     * 用户登陆
     * @param request
     * @param response
     */
    private void login(HttpServletRequest request, HttpServletResponse response) {

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");

        User user = new User();
        user.setLoginAct(loginAct);
        user.setLoginPwd(loginPwd);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        // UserService us = new UserServiceImpl();

        boolean flag = false;
        HashMap<String, Object> map = new HashMap<>();

        try {
            user = us.login(user);
            flag = true;

            map.put("success",flag);
            map.put("user",user);

            //如果用户没有登录过，则获取httpsession，并将其放入HttpSession列表中
            if (request.getSession(false) == null) {
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("user",user);
                HttpSessionUtil.httpSessionMap.put(user.getId(),httpSession);
                System.out.println("=====> UserController.login 添加httpSession");
                // System.out.println("=====> httpSession = " + httpSession);
            }

        } catch (LoginException e) {
            e.printStackTrace();
        }

        PrintJson.printJsonObj(response,map);
    }
}
