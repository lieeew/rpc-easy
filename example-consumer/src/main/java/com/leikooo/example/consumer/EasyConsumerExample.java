package com.leikooo.example.consumer;


import com.leikooo.Interface.UserService;
import com.leikooo.domain.User;

/**
 * 简易服务消费者示例
 * @author leikooo
 */
public class EasyConsumerExample {

    public static void main(String[] args) {
        UserService userService = new UserServiceProxy();
        User user = new User();
        user.setName("哈哈哈");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
