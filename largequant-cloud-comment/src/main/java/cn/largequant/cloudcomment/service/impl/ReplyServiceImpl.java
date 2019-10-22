package cn.largequant.cloudcomment.service.impl;

import cn.largequant.cloudcomment.dao.ReplyDao;
import cn.largequant.cloudcomment.entity.Reply;
import cn.largequant.cloudcomment.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyDao replyDao;

    //新增回复
    @Override
    public int addReply(Reply reply) {
        //防止xss
        reply.setContent(HtmlUtils.htmlEscape(reply.getContent()));
        //调用敏感词过滤
        //comment.setContent(sensitiveService.filter(comment.getContent()));
        return replyDao.insert(reply);
    }

    //获取根评论下的所有回复
    @Override
    public List<Reply> getAllReplyByCommentId(long commentId) {
        //考虑分页或包装两个事
        return replyDao.selectByCommentId(commentId);
    }

    //获取某个回复的所有回复
    @Override
    public List<Reply> getAllReplyByReplyId(long replyId) {
        return replyDao.selectByReplyId(replyId);
    }

    //获取根评论下的回复数量
    @Override
    public long getReplyCountByCommentId(long commentId) {
        return replyDao.countByCommentId(commentId);
    }

}
