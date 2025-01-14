package com.cong.springbootinit.constant;

/**
 * 通用常量
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";


    /**
     * https请求
     */
    String PORT = "8101";

    /**
     * api
     */
    String PREFIX = "/api";

    /**
     * :
     */
    String JOIN = ":";

    /**
     * 重复提交间隔时间（秒）
     */
    Integer REPEAT_SUBMIT_INTERVAL = 5;

}
