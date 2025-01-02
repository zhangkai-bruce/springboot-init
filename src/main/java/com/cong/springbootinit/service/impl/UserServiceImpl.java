package com.cong.springbootinit.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.springbootinit.common.ErrorCode;
import com.cong.springbootinit.config.GithubConfig;
import com.cong.springbootinit.constant.CommonConstant;
import com.cong.springbootinit.constant.RedisKey;
import com.cong.springbootinit.constant.SystemConstants;
import com.cong.springbootinit.exception.BusinessException;
import com.cong.springbootinit.mapper.UserMapper;
import com.cong.springbootinit.model.dto.user.UserAddRequest;
import com.cong.springbootinit.model.dto.user.UserLoginRequest;
import com.cong.springbootinit.model.dto.user.UserQueryRequest;
import com.cong.springbootinit.model.dto.user.UserRegisterRequest;
import com.cong.springbootinit.model.entity.User;
import com.cong.springbootinit.model.enums.UserRoleEnum;
import com.cong.springbootinit.model.vo.LoginUserVO;
import com.cong.springbootinit.model.vo.TokenLoginUserVo;
import com.cong.springbootinit.model.vo.UserVO;
import com.cong.springbootinit.service.UserService;
import com.cong.springbootinit.utils.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cong.springbootinit.constant.SystemConstants.SALT;

/**
 * 用户服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;
    private final GithubConfig githubConfig;

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public TokenLoginUserVo userLogin(UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        String code = userLoginRequest.getCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 校验验证码
        String verifyCode = stringRedisTemplate.opsForValue().get(RedisKey.CODE);
        if (StringUtils.isBlank(verifyCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已过期");
        }
        if (!StringUtils.equals(verifyCode, code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        User user = getUserByUserAccountAndPassword(userAccount, encryptPassword);
        // 3. 记录用户的登录态
        StpUtil.login(user.getId());
        StpUtil.getTokenSession().set(SystemConstants.USER_LOGIN_STATE, user);
        return this.getTokenLoginUserVO(user);
    }

    public TokenLoginUserVo loginUserVo(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户信息不存在！");
        }
        // 3. 记录用户的登录态
        StpUtil.login(user.getId());
        StpUtil.getTokenSession().set(SystemConstants.USER_LOGIN_STATE, user);
        return this.getTokenLoginUserVO(user);
    }

    @NotNull
    private User getUserByUserAccountAndPassword(String userAccount, String encryptPassword) {
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        return user;
    }

    public TokenLoginUserVo getTokenLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        TokenLoginUserVo loginUserVO = new TokenLoginUserVo();
        BeanUtils.copyProperties(user, loginUserVO);
        //获取 Token  相关参数
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        loginUserVO.setSaTokenInfo(tokenInfo);
        return loginUserVO;
    }


    /**
     * 获取登录用户
     * 获取当前登录用户
     * <p>
     * User}
     */
    @Override
    public User getLoginUser() {
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取登录用户许可 null
     * 获取当前登录用户（允许未登录）
     * <p>
     * User}
     */
    @Override
    public User getLoginUserPermitNull() {
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @return boolean
     */
    @Override
    public boolean isAdmin() {
        // 仅管理员可查询
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @return boolean
     */
    @Override
    public boolean userLogout() {
        if (!StpUtil.isLogin() || StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        StpUtil.logout();
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public long addUser(UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userAddRequest.getUserAccount();
        User one = this.lambdaQuery().eq(User::getUserAccount, userAccount).one();
        if (one != null) {
            throw new BusinessException(500, "账号重复，请修改！");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        if (StringUtils.isNotBlank(user.getUserPassword())) {
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + user.getUserPassword()).getBytes());
            user.setUserPassword(encryptPassword);
        }
        boolean save = this.save(user);
        if (save) {
            return user.getId();
        }
        return 0;
    }

    @Override
    public TokenLoginUserVo userLoginByGithub(AuthCallback callback) {
        AuthRequest authRequest = githubConfig.getAuthRequest();
        AuthResponse<?> response = authRequest.login(callback);
        // 获取用户信息
        AuthUser authUser = (AuthUser) response.getData();
        if (authUser == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Github 登录失败，获取用户信息失败");
        }
        //判断用户是否存在
        String githubId = authUser.getUuid();
        //不存在则注册、存在则直接返回用户信息
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getGithubId, githubId));
        if (user == null) {
            saveGithubUser(authUser);
        }
        return this.loginUserVo(user);
    }


    private void saveGithubUser(AuthUser authUser) {
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + authUser.getUuid()).getBytes());
        User user = new User().setUserPassword(encryptPassword)
                .setUserAccount(authUser.getUsername())
                .setGithubId(authUser.getUuid())
                .setUserAvatar(authUser.getAvatar())
                .setUserProfile(authUser.getRemark())
                .setUserName(authUser.getNickname())
                .setUserRole(UserRoleEnum.USER.getValue());
        this.save(user);
    }
}
