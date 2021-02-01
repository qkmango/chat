package cn.qkmango.chat.chat.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import cn.qkmango.chat.chat.utils.WebSocketMapUtil;
import cn.qkmango.chat.chat.utils.httpsesson.HttpSessionUtil;
import cn.qkmango.chat.setting.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 一个连接就是一个ChatWebSocket对象，ChatWebSocket对象存放到 WebSocketMapUtil.ConcurrentMap中
 */
@ServerEndpoint("/websocket/{id}")
public class ChatWebSocket {

    private Session     session;        // WebSocket的session
    private HttpSession httpSession;    // HttpSession
    private User        user;           // 当前 WebSocket 连接的user对象



    /**
     * 连接建立后触发的方法
     */
    @OnOpen
    public void onOpen(@PathParam("id") String id, Session session) throws IOException {

        this.session = session;
        this.httpSession = HttpSessionUtil.httpSessionMap.get(id);


        // this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.user = (User)httpSession.getAttribute("user");

        //获取当前用户id是否已经存在有 ChatWebSocket
        ChatWebSocket existWS = WebSocketMapUtil.webSocketMap.get(user.getId());

        //如果存在，并且以及存在的ChatWebSocket.session.getId()与当前ChatWebSocket.session.getId()不一样
        //说明此id的用户登陆了两次
        /**
         * 1. 同一个浏览器刷新时，会执自动行onClose，关闭websocket和移除ChatWebSocket，但是Tomcat维护的session列表中，httpsession还是同一个
         *      所以在重新加载页面后，通过相同的httpsession.user.get()无法获取到在onClose()时移除的ChatWebSocket；
         *
         * 2. 不同浏览器登陆同一个账号，当第二个浏览器登陆同一个账号时，通过 WebSocketMapUtil.webSocketMap.get(user.getId())获取CharWebSocket时，
         *      此时已经存在有一个CharWebSocket对象了（因为存储CharWebSocket对象时使用的是user.getId()，相同的id，
         *      固然会获取到第一个浏览器登陆账号时存储的CharWebSocket对象），但是两个浏览器与服务器建立的websocket.session.id()是不同的，
         *      通过比较两个websocket.session.id()可以判定 此账号是否是不同浏览器或者不同电脑登陆的，当下面if执行时，说明就是不同浏览器或不同电脑登陆的了，
         *      那么就将最早登陆的存储的ChatWebSocket对象从列表中移除，并关闭WebSocket的连接，在if后，会将新的ChatWebSocket对象存入列表
         */


        if (existWS != null && existWS.session.getId() != this.session.getId()) {
            //将已经存在的 ChatWebSocket 移除，并且关闭 HttpSession 和 session

            // ChatWebSocket chatWebSocket = WebSocketMapUtil.webSocketMap.get(user.getId());
            //执行此方法，会使HttpSession失效，有一个HttpSession监听器OnlineUserHttpSessionListener，
            //会在失效时执行sessionDestroyed()，会将所有的相关事情处理，
            //包括从容器中移除HttpSession、移除ChatWebSocket、关闭ChatWebSocket的连接
            existWS.httpSession.invalidate();

            // ChatWebSocket remove = WebSocketMapUtil.webSocketMap.remove(existWS.user.getId());
            // remove.getHttpSession().invalidate();
            // remove.session.close();
            // System.out.println(">>>>>>>已存在，移除");
        }


        WebSocketMapUtil.webSocketMap.put(user.getId(), this);

        //推送给所有客户端在线信息
        reloadOnlineInfo();
        System.out.println("-----> onOpen: 当前在线人数 " + WebSocketMapUtil.getOnlineCount() + "，连接人 " + user.getName() + " --------");
    }

    /**
     * 连接关闭后触发的方法
     * 当浏览器刷新或关闭时，会自动关闭WebSocket，执行onClose()，将ChatWebSocket从容器列表中移除
     *
     * 存在一个问题：当用户点击退出时
     *      1. 客户端会先执行websocket关闭方法，服务端就会进入到onClose()方法，移除ChatWebSocket
     *      2. 客户端浏览器执行完websocket关闭方法后，发送ajax请求，请求服务器 user/logout.do
     *      3. 服务器的 logout()方法内会执行 HttpSession.invalidate()，销毁HttpSession，
     *          但是在销毁前会执行监听器 OnlineUserHttpSessionListener.sessionDestroyed()方法，
     *          方法内会将ChatWebSocket、HttpSession充容器中移除，并关闭session（WebSocket的），
     *          注意，因为在第一步中已经移除了 ChatWebSocket ，所以在此步骤时，是无法成功移除的，因为已经被移除过了，
     *          所以 OnlineUserHttpSessionListener.sessionDestroyed() 中会抛异常
     *      4. 浏览器会自动跳转到登陆页面
     *
     * 不过在两台电脑登陆同一账号时是不会发送上面的问题的，因为客户端没有主动关闭 WebSocket，所以不会执行onClose(),
     * 第二台电脑登陆账号时会被服务器自动检测出是多次登陆，服务器会主动去销毁第一个登陆的用户的HttpSession，销毁时会执行
     * OnlineUserHttpSessionListener.sessionDestroyed()，所以并不会有 步骤 1 的操作，固然不会有抛异常（空指针异常）的问题
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