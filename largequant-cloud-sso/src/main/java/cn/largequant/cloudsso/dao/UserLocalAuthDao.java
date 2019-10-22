package cn.largequant.cloudsso.dao;

import cn.largequant.cloudcommon.entity.sso.UserLocalAuth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserLocalAuthDao {

    String TABLE_NAME = " user_local_auth ";
    String INSERT_FIELDS = " username, password, phone, salt ";
    String SELECT_FIELDS = " auth_id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{username},#{password},#{phone},#{salt})"})
    long save(UserLocalAuth userLocalAuth);

    void update(UserLocalAuth userLocalAuth);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where phone=#{phone}"})
    UserLocalAuth getByPhone(String phone);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where username=#{username} or phone=#{username}"})
    UserLocalAuth getByUsernameOrPhone(String username);

}
