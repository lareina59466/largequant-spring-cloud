package cn.largequant.cloudcomment.entity;

import lombok.Data;

import java.util.Date;

/**
 * 评论回复
 */
@Data
public class Reply {

    private long id;

    //根评论id(外键)
    private long commentId;

    //回复目标id(外键)，可能是评论ID或回复ID
    private long replyId;

    //回复类型(评论的回复,回复的回复)
    private int replyType;

    //回复内容
    private String content;

    //回复用户
    private long fromUserid;

    //回复用户
    private long toUserid;

    //回复时间
    private Date createTime;
}
