package cn.largequant.cloudcomment.controller;

import cn.largequant.cloudcomment.entity.Reply;
import cn.largequant.cloudcomment.service.ReplyService;
import cn.largequant.cloudcommon.entity.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

public class ReplyController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    ReplyService replyService;

    @Autowired
    CommentProducer commentProducer;

    /**
     * 回复
     */
    @PostMapping(value = {"/reply"})
    public String reply(int commentId, int replyId, int replyType, String content, int postId) {
        try {
            long toUserId = replyService.getUserByReplyId(replyId);
            // 增加question的评论
            Reply reply = new Reply();
            reply.setCommentId(commentId);
            if (hostHolder.getUser() != null) {
                reply.setFromUserid(hostHolder.getUser().getId());
            } else {
                reply.setFromUserid(0);
            }
            reply.setContent(content);
            reply.setCreateTime(new Date());
            reply.setReplyId(replyId);
            reply.setReplyType(replyType);
            reply.setToUserid(toUserId);
            replyService.addReply(reply);
            // 推送异步事件
            MessageVo messageVo = new MessageVo();
            messageVo.setFromUserId(hostHolder.getUser().getId());
            messageVo.setToUserId(toUserId);
            messageVo.setContent("用户" + hostHolder.getUser().getNickName() + "评论了你的评论,/post/" + postId);
            messageVo.setCreateTime(new Date());
            messageVo.setHasRead(0);
            commentProducer.send(messageVo);
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/post/" + postId;
    }
}
