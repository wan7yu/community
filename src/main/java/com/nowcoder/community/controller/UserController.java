package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {

    // 日志对象
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 注入用户上传头像到本地的路径
    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    // 注入业务层和获取用户信息类
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    // 用户信息设置页面
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    // 用户头像上传
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还未选择图片！");
            return "/site/setting";
        }

        // 拿到图片名
        String fileName = headerImage.getOriginalFilename();
        // 得到图片后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "图片的格式不正确！");
            return "/site/setting";
        }

        // 随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        // 存入目录中
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e);
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 更新当前用户的头像
        User user = hostHolder.getUser(); // 拿到当前用户
        // 浏览器拿头像的地址
        // http://localhost:8080/community/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateUserHeader(user.getId(), headerUrl);

        // 更新完成后重定向到首页
        return "redirect:/index";
    }

    /*
     * 浏览器获取头像的路径
     * @param fileName: 文件名称
     * @param response: 响应给浏览器的头像数据存入response中
     */
    @GetMapping("/header/{fileName}")
    public void getUserHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放图片的路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);

        try (
                FileInputStream fis = new FileInputStream(fileName);
                ServletOutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0; // 读取的长度
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("/update")
    public String updatePassword(String password, String newPassword,
                                 String confirm, Model model) {
        // 1.拿到当前用户
        User user = hostHolder.getUser();
        // 2.检查密码格式以及是否正确
        if (StringUtils.isBlank(password)) {
            model.addAttribute("passwordMsg", "密码不能为空！");
        }
        if (!user.getPassword().equals(CommunityUtil.md5(password + user.getSalt()))) {
            model.addAttribute("passwordMsg", "密码错误，请重新输入！");
            return "/site/setting";
        }
        // 3.检查两次密码输入是否相同
        if (newPassword.length() < 8) {
            model.addAttribute("newPasswordMsg", "输入密码不能小于8位！");
            return "/site/setting";
        }
        if (!confirm.equals(newPassword)) {
            model.addAttribute("newPasswordMsg", "两次输入密码不相同，请重新输入！");
            return "/site/setting";
        }

        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        // 4.检查输入密码跟原密码是否相同
        if (user.getPassword().equals(newPassword)) {
            model.addAttribute("passwordMsg", "输入密码与原密码相同，请重新输入！");
            return "/site/setting";
        }

        // 5.更新密码
        userService.updateUserPassword(user.getId(), newPassword);
        return "redirect:/index";
    }

    // 个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        // 查询到当前用户
        User user = userService.findUserById(userId);
        if (user == null) throw new RuntimeException("该用户不存在！");

        // 在模型中加入用户
        model.addAttribute("user", user);

        // 查询点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        return "/site/profile";
    }
}
