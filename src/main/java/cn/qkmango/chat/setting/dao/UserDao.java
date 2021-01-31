package cn.qkmango.chat.setting.dao;

import cn.qkmango.chat.setting.domain.User;

/**
 * @version 1.0
 * @Description: //TODO
 * <p>类简介</p>
 * <p>类详细介绍</p>
 * @className UserDao
 * @author: Mango
 * @date: 2021-01-30 13:57
 */
public interface UserDao {

    User login(User user);

    int queryLoginAct(String loginAct);

    int register(User user);
}
