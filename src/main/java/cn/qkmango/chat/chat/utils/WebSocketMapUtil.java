package cn.qkmango.chat.chat.utils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import cn.qkmango.chat.chat.web.controller.ChatWebSocket;

public class WebSocketMapUtil {

    public static ConcurrentMap<String, ChatWebSocket> webSocketMap = new ConcurrentHashMap<>();

    public static void put(String key, ChatWebSocket chatWebSocket) {
        webSocketMap.put(key, chatWebSocket);
    }

    public static ChatWebSocket get(String key) {
        return webSocketMap.get(key);
    }

    public static void remove(String key) {
        webSocketMap.remove(key);
    }

    public static Collection<ChatWebSocket> getValues() {
        return webSocketMap.values();
    }

    public static int getOnlineCount() {
        return webSocketMap.size();
    }
}