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

    // 主题类型：删贴
    String TOPIC_DELETE = "delete";

    // 主题类型：分享
    String TOPIC_SHARE = "share";

    /*
    * 系统用户id
    * */
    int SYSTEM_USER_ID = 1;

    /*
    * 用户权限常量
    * */

    // 权限：普通用户
    String AUTHORITY_USER = "user";

    // 权限：管理员
    String AUTHORITY_ADMIN = "admin";

    // 权限：版主
    String AUTHORITY_MODERATOR = "moderator";

    /*
     * 帖子类型常量
     * */

    // 帖子类型：0：普通帖子
    int POST_TYPE_NORMAL = 0;

    // 帖子类型：1：置顶帖子
    int POST_TYPE_TOP = 1;

    /*
     * 帖子状态常量
     * */

    // 帖子状态: 0：正常
    int POST_STATUS_NORMAL = 0;

    // 帖子状态: 1：加精
    int POST_STATUS_WONDERFUL = 1;

    // 帖子状态: 2：删除
    int POST_STATUS_DELETE = 2;
}
