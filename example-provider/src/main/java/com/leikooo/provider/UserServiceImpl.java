package com.leikooo.provider;


import com.leikooo.Interface.UserService;
import com.leikooo.domain.User;

/**
 * 用户服务实现类
 * @author leikooo
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
