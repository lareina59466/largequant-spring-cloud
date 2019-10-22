package cn.largequant.cloudcomment.entity;

import lombok.Data;

/**
 * 点赞
 */
@Data
public class Like {

    private long id;

    //点赞对应的(作品，评论，回复)的Id
    private long typeId;

    //点赞类型(作品，评论，回复)
    private int type;

    //用户id
    private long userId;

    //点赞状态  0--取消赞   1--有效赞
    private int status;
}
