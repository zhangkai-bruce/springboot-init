package com.cong.springbootinit.interceptor;

import cn.hutool.json.JSONUtil;
import com.cong.springbootinit.annotation.NoRepeatSubmit;
import com.cong.springbootinit.common.BaseResponse;
import com.cong.springbootinit.common.ErrorCode;
import com.cong.springbootinit.common.ResultUtils;
import com.cong.springbootinit.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoRepeatSubmitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NoRepeatSubmit annotation = handlerMethod.getMethodAnnotation(NoRepeatSubmit.class);

        if (annotation == null) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null) {
            BaseResponse<?> baseResponse = ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限访问!");
            responseResultData(response, baseResponse);
            return false;
        }
        // 获取请求参数并转换为 JSON 字符串
        String params = JSONUtil.toJsonStr(request.getParameterMap());
        String methodName = handlerMethod.getResolvedFromHandlerMethod().getMethod().getName();
        String key = RedisKey.REPEAT_SUBMIT_KEY + token + RedisKey.JOIN + methodName + RedisKey.JOIN + params.hashCode();

        // 获取键的剩余时间
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ObjectUtils.isNotEmpty(ttl) && ttl > 0) {
            BaseResponse<?> baseResponse = ResultUtils.error(ErrorCode.REPEAT_SUBMIT, "您的操作太频繁了，请稍后重试！请在" + ttl + " 秒后重新尝试！");
            log.error("操作过于频繁！ {}", JSONUtil.toJsonStr(baseResponse));
            responseResultData(response, baseResponse);
            return false;
        }

        redisTemplate.opsForValue().set(key, "1", annotation.expireTime(), TimeUnit.SECONDS);
        return true;
    }

    private void responseResultData(final HttpServletResponse response, final BaseResponse<?> baseResponse) throws IOException {
        // 设置字符集和内容类型
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(baseResponse));
    }


}
