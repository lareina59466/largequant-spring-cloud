package cn.largequant.cloudcommon.intercepter;

import cn.largequant.cloudcommon.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginIntercepter implements HandlerInterceptor {

    /**
     * 进入controller之前进行拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(token == null ){
            token = request.getParameter("token");
        }
        if(token != null ) {
           Claims claims = JWTUtil.checkJWT(token);
            if(claims !=null){
                Integer userId = (Integer)claims.get("id");
                String name = (String) claims.get("name");
                request.setAttribute("user_id",userId);
                request.setAttribute("name",name);
                return true;
            }
        }
        response.sendRedirect("/reglogin?next=" + request.getRequestURI());
        return false;
    }
}
