package cn.qkmango.chat.chat.utils;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.servlet.http.HttpSession;


/**
 * @version 1.0
 * <p>类简介</p>
 * <p>类详细介绍</p>
 * @className GetHttpSessionConfigurator
 * @author: Mango
 * @date: 2021-01-30 08:21
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
