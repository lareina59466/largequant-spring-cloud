package cn.largequant.cloudcomment.service.impl;

import cn.largequant.cloudcomment.entity.Reply;
import cn.largequant.cloudcomment.service.CommentService;
import cn.largequant.cloudcomment.entity.Comment;
import cn.largequant.cloudcomment.dao.CommentDao;
import cn.largequant.cloudcomment.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDAO;

    @Autowired
    private ReplyService replyService;


    //新增评论
    @Override
    public int addComment(Comment comment) {
        //防止xss
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //调用敏感词过滤
        //comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.insert(comment);
    }

    //获取某个作品下的所有主评论
    @Override
    public List<Comment> getAllComment(int composeId) {
        return commentDAO.selectByEntity(composeId);
    }

    //获取某个作品的所有评论
    @Override
    public long getAllCommentCount(int composeId) {
        long couont = commentDAO.countByEntity(composeId);
        List<Comment> comments = commentDAO.selectByEntity(composeId);
        for (Comment comment : comments) {
            couont += replyService.getReplyCountByCommentId(comment.getId());
        }
        return couont;
    }

}