package com.am.adastra.util.wx;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author 马强
 * @Description
 * @create 2022-06-30 9:45
 */
public class JwtUtils {

    public static final long EXPIRE = 1000 * 60 * 60 * 24;  //token过期时间
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";  //密钥

    //生成token字符串方法
    public static String getJwtToken(String id, String nickname){

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")   //设置jwt的头信息
                .setHeaderParam("alg", "HS256")

                .setSubject("libilibi-user")            //设置jwt过期时间
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                .claim("id", id)        //设置主题部分，存储用户信息
                .claim("nickname", nickname)

                .signWith(SignatureAlgorithm.HS256, APP_SECRET)  //编码
                .compact();

        return JwtToken;
    }


    /**
     * 判断token是否存在与有效
     */
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 判断token是否存在与有效
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 根据token获取会员id （信息）
     */
    public static String getMemberIdByJwtToken(String token) {
        if(StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("id");
    }
}
