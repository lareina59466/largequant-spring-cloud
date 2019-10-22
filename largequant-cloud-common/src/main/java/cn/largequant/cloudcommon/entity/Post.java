package cn.largequant.cloudcommon.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "forum", type = "post")
public class Post {

    private int id;

    private String title;

    private String content;

    private Date created_date;

    private int user_id;

    private int comment_count;

}