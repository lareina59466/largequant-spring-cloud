package cn.largequant.cloudcomment.dao;

import cn.largequant.cloudcomment.entity.Reply;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyDao {

    String TABLE_NAME = " reply ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int insert(Reply reply);

    List<Reply> selectByCommentId(long commentId);

    List<Reply> selectByReplyId(long replyId);

    long countByCommentId(long commentId);
}
