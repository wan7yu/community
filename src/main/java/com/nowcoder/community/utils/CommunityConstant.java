package com.nowcoder.community.utils;

public interface CommunityConstant {
    /*
     * 用户账号激活状态常量
     * */

    // 激活成功
    int ACTIVATION_SUCCESS = 0;
    // 重复激活
    int ACTIVATION_REPEAT = 1;
    // 激活失败
    int ACTIVATION_FAILURE = 2;

    /*
     * 关于登录凭证的常量
     * */
    // 默认状态的登录凭证的过期时间
    int DEFAULT_EXPIRED_SECONDS = 3600 * 24; // 1天
    // 记住状态的登录凭证的过期时间
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100; // 100 天


    /*
     * 实体类型常量
     * */

    // 实体类型：帖子
    int ENTITY_TYPE_POST = 1;

    // 实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;

    // 实体类型：异或
    int ENTITY_TYPE_USER = 3;

    /*
     * 主题类型常量
     * */

    // 主题类型：评论
    String TOPIC_COMMENT = "comment";

    // 主题类型：点赞
    String TOPIC_LIKE = "like";

    // 主题类型：关注
    String TOPIC_FOLLOW = "follow";

    // 主题类型：发帖
    String TOPIC_PUBLISH = "publish";

    /*
    * 系统用户id
    * */
    int SYSTEM_USER_ID = 1;
}
