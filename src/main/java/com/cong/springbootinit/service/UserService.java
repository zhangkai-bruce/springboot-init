package com.cong.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.springbootinit.model.dto.user.UserAddRequest;
import com.cong.springbootinit.model.dto.user.UserLoginRequest;
import com.cong.springbootinit.model.dto.user.UserQueryRequest;
import com.cong.springbootinit.model.dto.user.UserRegisterRequest;
import com.cong.springbootinit.model.entity.User;
import com.cong.springbootinit.model.vo.LoginUserVO;
import com.cong.springbootinit.model.vo.UserVO;

import java.util.List;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * userAccount   用户账户
     * userPassword  用户密码
     * checkPassword 校验密码
     *
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     * userAccount  用户账户
     * userPassword 用户密码
     *
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest);


    /**
     * 获取当前登录用户
     */
    User getLoginUser();

    /**
     * 获取当前登录用户（允许未登录）
     */
    User getLoginUserPermitNull();

    /**
     * 是否为管理员
     *
     * @return boolean
     */
    boolean isAdmin();

    /**
     * 是否为管理员
     * user 用户
     *
     * @return boolean
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @return boolean
     */
    boolean userLogout();

    /**
     * 获取脱敏的已登录用户信息
     * user 用户
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     * user 用户
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     * userList 用户列表
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     * userQueryRequest 用户查询请求
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 管理员添加用户
     *
     * @param userAddRequest userAddRequest
     * @return return
     */
    long addUser(UserAddRequest userAddRequest);
}
