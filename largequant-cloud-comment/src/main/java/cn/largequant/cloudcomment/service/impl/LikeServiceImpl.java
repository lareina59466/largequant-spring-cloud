package cn.largequant.cloudcomment.service.impl;

import cn.largequant.cloudcomment.dao.LikeDao;
import cn.largequant.cloudcomment.service.LikeService;
import cn.largequant.cloudcomment.utils.RedisKeyUtil;
import cn.largequant.cloudcommon.util.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private RedisUtil redisUtil;

    private LikeDao likeDao;

    // 当前喜欢人数
    public long getLikeCount(int typeId, int type) {
        //先从缓存读
        String likeKey = RedisKeyUtil.getLikeKey(typeId, type);
        long count = redisUtil.getSetSize(likeKey);
        if (count != 0) {
            return count;
        } else {
            //读不到，再从数据库读
            return likeDao.countByType(typeId, type);
        }
    }

    // 获取当前user是否点过赞
    public int getLikeStatus(int userId, int typeId, int type) {
        String likeKey = RedisKeyUtil.getLikeKey(typeId, type);
        if (redisUtil.existsSet(likeKey, String.valueOf(userId))) {
            return 1;
        }
        return 0;
    }

    //点赞
    public long like(int userId, int typeId, int type) {
        // 从点赞集合里添加这个userId
        String likeKey = RedisKeyUtil.getLikeKey(typeId, type);
        redisUtil.setSet(likeKey, String.valueOf(userId));
        // 返回喜欢人数
        return redisUtil.getSetSize(likeKey);
    }

    //取消点赞
    public long disLike(int userId, int typeId, int type) {
        // 从点赞集合里删除这个userId
        String likeKey = RedisKeyUtil.getLikeKey(typeId, type);
        redisUtil.deleteSet(likeKey, String.valueOf(userId));
        // 返回喜欢人数
        return redisUtil.getSetSize(likeKey);
    }
}