package com.example.user.dao;

import com.example.user.entity.User_information;
import com.example.user.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {

    /**
     * 查找用户
     * */
    @Select("SELECT * FROM User WHERE user_name =#{user_name}")
    User_information find_User(@Param("user_name") String user_name);


    /**
     * 查找用户
     * */
    @Select("SELECT user_id FROM User WHERE user_name =#{user_name}")
    String getUserId(@Param("user_name") String user_name);





    /**
     * 增加一个用户
     *
     * */
    @Insert("INSERT INTO User VALUES(#{user_name}, #{password}, #{user_id})")
    int add_User(@Param("user_name") String user_name, @Param("password") String password,@Param("user_id")  String user_id);


    /**
     * 获取用户列表
     *
     */
    @Results({
            @Result(property = "user_name", column = "user_name"),
            @Result(property = "user_id", column = "user_id")
    })
    @Select("select user_name,user_id from User")
    List<User> getAllUsers();



    /**
     * 删除所有用户
     */
    @Delete("DELETE FROM User")
    void delete_User();
}
