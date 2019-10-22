package cn.largequant.cloudcomment.service;

import cn.largequant.cloudcomment.entity.Reply;

import java.util.List;

public interface ReplyService {

    //新增回复
    int addReply(Reply reply);

    //获取根评论下的所有回复
    List<Reply> getAllReplyByCommentId(long commentId);

    //获取某个回复的所有回复
    List<Reply> getAllReplyByReplyId(long replyId);

    //获取根评论下的回复数量
    long getReplyCountByCommentId(long commentId);

}
