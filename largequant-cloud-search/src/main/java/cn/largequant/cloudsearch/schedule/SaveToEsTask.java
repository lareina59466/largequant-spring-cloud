package cn.largequant.cloudsearch.schedule;

import cn.largequant.cloudcommon.entity.Post;
import cn.largequant.cloudcommon.entity.User;
import cn.largequant.cloudsearch.repository.PostRepository;
import cn.largequant.cloudsearch.repository.UserRepository;
import cn.largequant.cloudsearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configuration
@EnableScheduling
public class SaveToEsTask {

    @Autowired
    private PostService postService;

    @Autowired
    private SearchService searchService;

    @Scheduled(cron = "0 15 0 * * ?")
    public void savePostToEs() {
        List<Post> posts = postService.getAllPost();
        searchService.savePost(posts);
    }
}
