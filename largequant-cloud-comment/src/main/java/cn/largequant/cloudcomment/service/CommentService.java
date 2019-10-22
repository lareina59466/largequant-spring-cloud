package cn.largequant.cloudcomment.service;

import cn.largequant.cloudcomment.entity.Comment;

import java.util.List;

public interface CommentService {

    //新增评论
    int addComment(Comment comment);

    //获取某个作品下的所有主评论
    List<Comment> getAllComment(int composeId);

    //获取某个作品的所有评论
    long getAllCommentCount(int composeId);

}
