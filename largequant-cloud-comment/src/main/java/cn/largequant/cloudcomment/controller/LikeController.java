package cn.largequant.cloudcomment.controller;

import cn.largequant.cloudcomment.service.LikeService;
import cn.largequant.cloudcommon.entity.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    /**
     * 点赞
     */
    @PostMapping(value = "/like")
    @ResponseBody
    public String like(int typeId, int type) {
        //登录才能点赞
        if (hostHolder.getUser() == null) {
            return ForumUtil.getJsonString(999);
        }
        // 返回前端点赞数
        long likeCount = likeService.like(hostHolder.getUser().getId(), typeId, type);
        return ForumUtil.getJsonString(0, String.valueOf(likeCount));
    }

    /**
     * 取消点赞
     */
    @PostMapping(value = "/disLike")
    @ResponseBody
    public String disLike(int typeId, int type) {
        //登录才能点赞
        if (hostHolder.getUser() == null) {
            return ForumUtil.getJsonString(999);
        }
        // 返回前端点赞数
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), typeId, type);
        return ForumUtil.getJsonString(0, String.valueOf(likeCount));
    }
}