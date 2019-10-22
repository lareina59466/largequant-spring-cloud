package cn.largequant.cloudcomment.service;

public interface LikeService {

    //获取某个类型的点赞数量
    long getLikeCount(int typeId, int type);

    //获取用户是否对某个类型的点赞状态
    int getLikeStatus(int userId, int typeId, int type);

    //用户点赞
    long like(int userId, int typeId, int type);

    //用户取消点赞
    long disLike(int userId, int typeId, int type);

}
