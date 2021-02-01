package cn.qkmango.chat.chat.web.listener;

import cn.qkmango.chat.chat.utils.WebSocketMapUtil;
import cn.qkmango.chat.chat.utils.httpsesson.HttpSessionUtil;
import cn.qkmango.chat.chat.web.controller.ChatWebSocket;
import cn.qkmango.chat.setting.domain.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;

/**
 * @version 1.0
 * <p>HttpSession的监听器</p>
 * <p>类详细介绍</p>
 * @className OnlineUserHttpSessionListener
 * @author: Mango
 * @date: 2021-02-01 21:15
 */
public class OnlineUserHttpSessionListener implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent event) {
        //将HttpSession放入 HttpSessionUtil.httpSessionMap 操作是在 UserController.login()中执行的
    }


    /**
     * 当httpSession失效时，执行此方法，此方法会将服务器与客户端的所有连接关闭并从容器中移除（ChatWebSocket，HttpSession）
     * @param event
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession httpSession = event.getSession();

        try {
            //获取此HttpSession中的user.id，通过id移除此HttpSession
            User user = (User) httpSession.getAttribute("user");
            String id = user.getId();
            //移除HttpSession
            HttpSession remove = HttpSessionUtil.httpSessionMap.remove(id);
            //移除ChatWebSocket
            ChatWebSocket removeChatWebSocket = WebSocketMapUtil.webSocketMap.remove(id);
            //关闭ChatWebSocket的连接
            removeChatWebSocket.getSession().close();

            System.out.println("sessionDestroyed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
