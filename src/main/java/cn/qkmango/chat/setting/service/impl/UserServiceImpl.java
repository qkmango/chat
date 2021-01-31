package cn.qkmango.chat.setting.service.impl;

import cn.qkmango.chat.setting.dao.UserDao;
import cn.qkmango.chat.setting.domain.User;
import cn.qkmango.chat.setting.exception.LoginException;
import cn.qkmango.chat.setting.exception.RegisterException;
import cn.qkmango.chat.setting.service.UserService;
import cn.qkmango.chat.utils.SqlSessionUtil;

/**
 * @version 1.0
 * @Description: //TODO
 * <p>类简介</p>
 * <p>类详细介绍</p>
 * @className UserServiceImpl
 * @author: Mango
 * @date: 2021-01-30 14:05
 */
public class UserServiceImpl implements UserService {


    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    /**
     * 登陆
     * @param user
     * @return
     * @throws LoginException
     */
    @Override
    public User login(User user) throws LoginException {
         user = userDao.login(user);

         if (user==null) {
             throw new LoginException("用户名或密码错误");
         }

        return user;
    }


    /**
     * 查询账户是否存在可用
     * @param loginAct
     * @return true 不存在，账户可用
     */
    @Override
    public boolean queryLoginAct(String loginAct) {

        int count = userDao.queryLoginAct(loginAct);

        //已存在账户，不可用，返回false
        if (count != 0) {
            return false;
        }
        return true;
    }


    /**
     * 注册账户
     * @return
     */
    @Override
    public boolean register(User user)throws RegisterException {

        boolean flag = false;
        try {
            flag = userDao.register(user)==1?true:false;
        } catch (Exception e) {
            throw new RegisterException("注册失败");
        }
        return flag;
    }
}
