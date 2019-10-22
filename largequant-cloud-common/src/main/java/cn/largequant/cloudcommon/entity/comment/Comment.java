package cn.largequant.cloudcommon.entity.comment;

import lombok.Data;

import java.util.Date;

/**
 * 评论
 */
@Data
public class Comment {

    private long id;

    //作品id(外键)
    private long composeId;

    //作品类型(博客，商品。。)
    //private int composeType;

    //评论内容
    private String content;

    //评论用户
    private long userId;

    //评论时间
    private Date createTime;
}
