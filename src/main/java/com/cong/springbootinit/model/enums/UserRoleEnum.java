package com.cong.springbootinit.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 */
@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin"),
    BAN("被封号", "ban");

    private final String text;
    private final String value;


    /**
     * 获取值
     * 获取值列表
     * <p>
     * List}<{@link String}>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 按值获取枚举
     * 根据 value 获取枚举
     * <p>
     * value 价值
     * UserRoleEnum}
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
