package cn.qkmango.chat.setting.service;

import cn.qkmango.chat.setting.domain.User;
import cn.qkmango.chat.setting.exception.LoginException;
import cn.qkmango.chat.setting.exception.RegisterException;

/**
 * @version 1.0
 * @Description: //TODO
 * <p>类简介</p>
 * <p>类详细介绍</p>
 * @className UserService
 * @author: Mango
 * @date: 2021-01-30 14:05
 */
public interface UserService {
    User login(User user) throws LoginException;

    boolean queryLoginAct(String loginAct);

    boolean register(User user) throws RegisterException;
}
