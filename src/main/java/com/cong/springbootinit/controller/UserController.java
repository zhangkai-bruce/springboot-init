package com.cong.springbootinit.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.springbootinit.common.BaseResponse;
import com.cong.springbootinit.common.ErrorCode;
import com.cong.springbootinit.common.ResultUtils;
import com.cong.springbootinit.constant.UserConstant;
import com.cong.springbootinit.exception.BusinessException;
import com.cong.springbootinit.exception.ThrowUtils;
import com.cong.springbootinit.model.dto.user.*;
import com.cong.springbootinit.model.entity.User;
import com.cong.springbootinit.model.vo.LoginUserVO;
import com.cong.springbootinit.model.vo.UserVO;
import com.cong.springbootinit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 用户接口
 */
@Api(tags = "用户相关")
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户注销")
    public BaseResponse<Boolean> userLogout() {
        boolean result = userService.userLogout();
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    @ApiOperation(value = "获取当前登录用户")
    public BaseResponse<LoginUserVO> getLoginUser() {
        User user = userService.getLoginUser();
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "创建用户")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        long userId = userService.addUser(userAddRequest);
        ThrowUtils.throwIf(userId == 0, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(userId);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{ids}")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "删除用户")
    public BaseResponse<Boolean> deleteUser(@PathVariable Long[] ids) {
        boolean b = userService.removeBatchByIds(Arrays.asList(ids));
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新用户")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "根据 id 获取用户（仅管理员）")
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取包装类")
    public BaseResponse<UserVO> getUserVoById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取用户列表（仅管理员）")
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取用户封装列表")
    public BaseResponse<Page<UserVO>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVoPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVoPage.setRecords(userVO);
        return ResultUtils.success(userVoPage);
    }


    /**
     * 更新个人信息
     */
    @PostMapping("/update/my")
    @ApiOperation(value = "更新个人信息")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser();
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
