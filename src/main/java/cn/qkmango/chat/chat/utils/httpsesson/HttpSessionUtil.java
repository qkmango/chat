package cn.qkmango.chat.chat.utils.httpsesson;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @version 1.0
 * @Description: //TODO
 * <p>类简介</p>
 * <p>类详细介绍</p>
 * @className HttpSessionUtil
 * @author: Mango
 * @date: 2021-02-01 08:32
 */
public class HttpSessionUtil {

    public static ConcurrentMap<String, HttpSession> httpSessionMap = new ConcurrentHashMap<>();

}
