package cn.qkmango.chat.chat.web.controller;

import cn.qkmango.chat.setting.domain.User;
import cn.qkmango.chat.utils.PrintJson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

/**
 * @version 1.0
 * @Description:
 * <p>聊天界面控制器</p>
 * @className ChatController
 * @author: Mango
 * @date: 2021-01-30 16:04
 */
public class ChatController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("====> 进入到用户控制器");

        String path = request.getServletPath();
        if ("/chat/getUserInfo.do".equals(path)) {
            getUserInfo(request,response);
        }


    }

    /**
     * 获取当前请求登陆的用户信息（name，id）
     * @param request
     * @param response
     */
    private void getUserInfo(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);

        User user = (User)session.getAttribute("user");
        String name = user.getName();
        String id = user.getId();

        HashMap<String, String> map = new HashMap<>();
        map.put("name",name);
        map.put("id",id);

        PrintJson.printJsonObj(response,map);
    }
}
