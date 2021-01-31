package cn.qkmango.chat.chat.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import cn.qkmango.chat.chat.utils.WebSocketMapUtil;
import cn.qkmango.chat.chat.utils.GetHttpSessionConfigurator;
import cn.qkmango.chat.setting.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 一个连接就是一个ChatWebSocket对象，ChatWebSocket对象存放到 WebSocketMapUtil.ConcurrentMap中
 */
@ServerEndpoint(value="/websocket",configurator= GetHttpSessionConfigurator.class)
public class ChatWebSocket {

    private Session     session;        // WebSocket的session
    private HttpSession httpSession;    // HttpSession
    private User        user;           // 当前 WebSocket 连接的user对象

    /**
     * 连接建立后触发的方法
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) throws IOException {

        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.user = (User)httpSession.getAttribute("user");



        //获取当前用户id是否已经存在有 ChatWebSocket
        ChatWebSocket existWS = WebSocketMapUtil.webSocketMap.get(user.getId());
        //如果存在，并且以及存在的ChatWebSocket.session.getId()与当前ChatWebSocket.session.getId()不一样
        //说明此id的用户登陆了两次
        if (existWS != null && existWS.session.getId() != this.getSession().getId()) {
            //将已经存在的 ChatWebSocket 移除，并且关闭 HttpSession 和 session
            ChatWebSocket remove = WebSocketMapUtil.webSocketMap.remove(existWS.getUser().getId());
            remove.getHttpSession().invalidate();
            remove.session.close();
        }


        WebSocketMapUtil.put(user.getId(), this);

        //推送给所有客户端在线信息
        reloadOnlineInfo();
        System.out.println("-----> onOpen: 当前在线人数 " + WebSocketMapUtil.getOnlineCount() + "，连接人 " + user.getName() + " --------");
    }

    /**
     * 连接关闭后触发的方法
     */
    @OnClose
    public void onClose() {
        // 从map中删除
        WebSocketMapUtil.remove(user.getId());
        //推送给所有客户端在线信息
        reloadOnlineInfo();
        System.out.println("-----> onClose: 当前在线人数 " + WebSocketMapUtil.getOnlineCount() + "，关闭人 " + user.getName() + " --------");
    }

    /**
     * 接收到客户端消息时触发的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        // 获取服务端到客户端的通道
        ChatWebSocket chatWebSocket = WebSocketMapUtil.webSocketMap.get(user.getId());
        System.out.println("收到来自 " + user.getName() + " 的消息：" + message);

        // 返回消息给Web Socket客户端（浏览器）
        chatWebSocket.sendMessageAll(message);
    }

    /**
     * 发生错误时触发的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("-----> onError: 当前在线人数 " + WebSocketMapUtil.getOnlineCount() + "，连接发生错误 " + user.getName() + "-"+ error.getMessage() + " --------");
        error.printStackTrace();
    }

    /**
     * 给单个客户端发送消息
     * @param message
     * @param sessionId
     * @throws IOException
     */
    public void sendMessageSingle(String message, String sessionId) throws IOException {

        // session.getBasicRemote().sendText(message); 同步消息
        // session.getAsyncRemote().sendText(message); 异步消息

        ChatWebSocket chatWebSocket = WebSocketMapUtil.get(sessionId);
        if(chatWebSocket != null) {
            chatWebSocket.session.getBasicRemote().sendText(message);
        }
    }

    /**
     * 给所有客户端发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessageAll(String message)  {
        for (ChatWebSocket item : WebSocketMapUtil.getValues()) {
            try {
                item.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                WebSocketMapUtil.remove(item.user.getId());
                e.printStackTrace();
            }
        }
    }


    /**
     * 将在线信息推送给所有客户端
     */
    public void reloadOnlineInfo() {

    /*
        {
            'isSysMsg':true,
            'onlineCount':20,
            'onlineList'['zs','ls','ww']
        }

    */
        //获取webSocket 对象集合
        Collection<ChatWebSocket> webSockets = WebSocketMapUtil.getValues();
        ArrayList<String> list = new ArrayList<>();

        for (ChatWebSocket item : webSockets) {
            //往List中添加在线的用户名
            list.add(item.user.getName());
        }


        HashMap<String, Object> map = new HashMap<>();
        map.put("isSysMsg",true);
        map.put("onlineCount",webSockets.size());
        map.put("onlineList",list);

        //转json
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //推送给所有客户端
        sendMessageAll(json);

        System.out.println("=====> 推送给所有客户端");
    }


    public Session getSession() {
        return session;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public User getUser() {
        return user;
    }
}