package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，针对每一个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话中包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话中包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 更新未读消息为已读
    int updateStatus(List<Integer> ids, int status);
}
