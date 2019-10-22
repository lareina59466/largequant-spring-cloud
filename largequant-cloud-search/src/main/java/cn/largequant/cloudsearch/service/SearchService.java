package cn.largequant.cloudsearch.service;

import cn.largequant.cloudcommon.entity.Post;
import cn.largequant.cloudcommon.entity.User;

import java.util.List;

public interface SearchService {

    //保存
    void savePost(List<Post> posts);

    List<Post> testSearch(String keywords, int offset, int limit);
}
