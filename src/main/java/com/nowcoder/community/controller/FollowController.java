package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @LoginRequired
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        // 关注
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        // 因为当前只能关注用户，并且关注后点击事件是跳转到 关注当前用户的人 的主页
        // 所以不需要postId
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注!");
    }

    @LoginRequired
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        // 取关
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注!");
    }

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        // 查询该用户关注的人
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 传入user，方便在页面中显示用户名
        model.addAttribute("user", user);

        // 设置分页参数
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        // 分页查询数据
        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");

                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        // 查询该用户的粉丝
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 传入user，方便在页面中显示用户名
        model.addAttribute("user", user);

        // 设置分页参数
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        // 分页查询数据
        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");

                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    // 查看当前用户是否已关注该实体(包含用户)
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
