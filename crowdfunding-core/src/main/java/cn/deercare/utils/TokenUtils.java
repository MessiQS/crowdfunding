package cn.deercare.utils;

import cn.deercare.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class TokenUtils {
    /**
     * 签名秘钥
     */
    public static final String SECRET = "care";
    // 有效期
    public static final Long EXPIRE = (long) (1000 * 60 * 60 * 3);
    
    public static final String URL_AUTHENTICATION = "URL";
    
    public static final String WS_AUTHENTICATION = "WS";

    /**
     * 生成token
     * @param id user表id
     * @param userType user类型
     * @return
     */
    public static String createJwtToken(Long id, Integer userType){
        String issuer = "www.deercare.cn";
        String subject = userType.toString();
        long ttlMillis = System.currentTimeMillis(); 
        return createJwtToken(id.toString(), issuer, subject, ttlMillis);
    }

    /**
     * 生成Token
     * 
     * @param id
     *            编号
     * @param issuer
     *            该JWT的签发者，是否使用是可选的
     * @param subject
     *            该JWT所面向的用户，是否使用是可选的；
     * @param ttlMillis
     *            签发时间
     * @return token String
     */
    public static String createJwtToken(String id, String issuer, String subject, long ttlMillis) {

        // 签名算法 ，将对token进行签名
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成签发时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 通过秘钥签名JWT
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
            .setIssuedAt(now)
            .setSubject(subject)
            .setIssuer(issuer)
            .signWith(signatureAlgorithm, signingKey);

        // if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            // long expMillis = nowMillis + ttlMillis;
        	long expMillis = nowMillis + EXPIRE;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();

    }

    // Sample method to validate and read the JWT
    public static Claims parseJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
            .parseClaimsJws(jwt).getBody();
        return claims;
    }
    
    public static String getUserType(String token) {
    	try {
    		Claims claims = TokenUtils.parseJWT(token);
    		return claims.getSubject();
    	}catch (Exception e) {
			// token格式不对
    		return null;
		}
    }

    public static String getUserId(String token) {
        try {
            Claims claims = TokenUtils.parseJWT(token);
            return claims.getId();
        }catch (Exception e) {
            // token格式不对
            return null;
        }
    }
    
    public static boolean tokenCheck(String token, String type){
    	Claims claims = TokenUtils.parseJWT(token);
		// 是否过期
		long nowMillis = System.currentTimeMillis();
		if(nowMillis > claims.getExpiration().getTime()){
			return false;
		}
    	return true;
    }

    public static void main(String[] args) {
    	User u = new User();
    	u.setId(new Long(1234));
    	u.setType(1);
    	// u.setUsername("sulu_source_Init");
    	// u.setEmail("admin@admin.com");
    	String jwt = TokenUtils.createJwtToken(u.getId(),u.getType());
    	System.out.println(jwt);
        System.out.println(parseJWT(jwt));
        System.out.println(parseJWT(jwt).getId());
        System.out.println(parseJWT(jwt).getSubject());
        // System.out.println(EXPIRE);
    }
}
