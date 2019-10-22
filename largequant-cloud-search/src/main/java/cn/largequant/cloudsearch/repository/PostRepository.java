package cn.largequant.cloudsearch.repository;

import cn.largequant.cloudcommon.entity.Post;
import cn.largequant.cloudcommon.entity.Question;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface PostRepository extends ElasticsearchRepository<Post,Long>{
}