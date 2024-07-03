package com.nowcoder.community.controller.advice;

import com.nowcoder.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 全局异常处理器
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: " + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 获取到当前的服务器的返回数据类型
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) { // 说明是json格式的数据
            response.setContentType(
                    "application/plain;charset=utf-8"
            );
            PrintWriter pw = response.getWriter();
            pw.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else { // 是网页，则重定向
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
