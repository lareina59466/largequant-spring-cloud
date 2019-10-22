package cn.largequant.cloudcommon.util;

import cn.largequant.cloudcommon.entity.sso.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * jwt工具类
 */
public class JWTUtil {


    private static final String SUBJECT = "xdclass";

    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7;  //过期时间，毫秒，一周

    //秘钥
    private static final String APPSECRET = "xd666";

    /**
     * 生成jwt
     */
    public static String geneJsonWebToken(User user) {

        if (user == null || user.getUserId() == 0) {
            return null;
        }
        String token = Jwts.builder()
                .setSubject(SUBJECT)
                .claim("id", user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, APPSECRET).compact();
        return token;
    }


    /**
     * 校验token
     */
    public static Claims checkJWT(String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(APPSECRET).
                    parseClaimsJws(token).getBody();
            return claims;

        } catch (Exception e) {
        }
        return null;
    }

}
