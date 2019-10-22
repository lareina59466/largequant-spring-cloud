package cn.largequant.cloudcomment.controller;

import cn.largequant.cloudcomment.service.CommentService;
import cn.largequant.cloudcomment.entity.Comment;
import cn.largequant.cloudcommon.entity.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    PostService postService;

    @Autowired
    CommentProducer commentProducer;

    /**
     * 评论
     */
    @PostMapping(value = {"/comment"})
    public String comment(int postId, String content) {
        try {
            // 增加question的评论
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                // 默认的游客id
                comment.setUserId(0);
            }
            comment.setComposeId(postId);
            comment.setCreateTime(new Date());
            commentService.addComment(comment);

            long toUserId = postService.getUserId(postId);
            // 推送异步事件
            MessageVo messageVo = new MessageVo();
            messageVo.setFromUserId(hostHolder.getUser().getId());
            messageVo.setToUserId(toUserId);
            messageVo.setContent("用户" + hostHolder.getUser().getNickName() + "评论了你的文章,/post/" + postId);
            messageVo.setCreateTime(new Date());
            messageVo.setHasRead(0);
            commentProducer.send(messageVo);
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/post/" + postId;
    }

}