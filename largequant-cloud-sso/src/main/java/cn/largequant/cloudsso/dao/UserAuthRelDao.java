package cn.largequant.cloudsso.dao;

import cn.largequant.cloudcommon.entity.sso.UserAuthRel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserAuthRelDao {

    String TABLE_NAME = " user_auth_rel ";
    String INSERT_FIELDS = " user_id, auth_id, auth_type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId},#{authId},#{authType})"})
    long save(UserAuthRel userAuthRel);

}
