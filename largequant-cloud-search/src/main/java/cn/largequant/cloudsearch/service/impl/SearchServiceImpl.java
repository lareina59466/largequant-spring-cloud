package cn.largequant.cloudsearch.service.impl;

import cn.largequant.cloudcommon.entity.Post;
import cn.largequant.cloudcommon.entity.User;
import cn.largequant.cloudsearch.repository.PostRepository;
import cn.largequant.cloudsearch.repository.UserRepository;
import cn.largequant.cloudsearch.service.SearchService;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PostRepository postRepository;

    public void savePost(List<Post> questions) {
        postRepository.saveAll(questions);
    }

    public List<Post> testSearch(String keywords, int offset, int limit) {
        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(keywords);
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "created_date"));
        Page<Post> page = postRepository.search(builder, pageable);
        return page.getContent();
    }

}