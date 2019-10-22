package cn.largequant.cloudsso.dao;

import cn.largequant.cloudcommon.entity.sso.UserThirdAuth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserThirdAuthDao {

    String TABLE_NAME = " user_third_auth ";
    String INSERT_FIELDS = " open_id, login_type, access_token ";
    String SELECT_FIELDS = " auth_id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{openId},#{loginType},#{accessToken})"})
    long save(UserThirdAuth userThirdAuth);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, "where open_id=#{openId} and access_token=#{accessToken}"})
    UserThirdAuth getByOpenIdAndAccesstoken(String openId, String accessToken);

}
