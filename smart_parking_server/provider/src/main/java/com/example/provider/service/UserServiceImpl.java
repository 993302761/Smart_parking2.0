package com.example.provider.service;

import com.example.provider.dao.UserDao;
import com.example.provider.entiry.User;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl  {

    @Resource
    private UserDao userDao;


    /**
     * 添加一名用户
     * */
    public String add_User(String user_name, String password, String user_id) {
        if (user_name.equals("")||password.equals("")||user_id.equals("")){
            return "所填信息不完整";
        }
        User user = userDao.find_User(user_name);
        if (user!=null){
            return "用户已注册";
        }
        int i= userDao.add_User(user_name,password,user_id);
        if (i<=0){
            return "注册失败";
        }
        else {
            return "注册成功";
        }
    }

    public String login_User(String user_name, String password) {
        if (user_name.equals("")||password.equals("")){
            return "用户名或密码为空";
        }
        User user=userDao.find_User(user_name);
        if (user==null){
            return "用户未注册";
        }
        if (user.getPassword().equals(password)){
            return "登录成功";
        }else {
            return "密码错误";
        }
    }


    public Integer getAllUsersNumber() {
        return userDao.getAllUsersNumber();
    }


    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

}
