package com.kieran.mallchat.common.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtil {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    @Value("${jwt.secret}")
    private String secret;

    private static final String UID_CLAIM = "uid";
    private static final String CREATE_TIME = "createTime";

    public String createToken(Long uid) {
        return JWT.create()
                .withClaim(UID_CLAIM, uid)
                .withClaim(CREATE_TIME, new Date())
                .sign(Algorithm.HMAC256(secret));
    }


    public Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        try {
            return JWT.require(Algorithm.HMAC256(secret)).build()
                    .verify(token).getClaims();
        } catch (Exception e) {
            log.error("decode error,token:{}", token, e);
            return null;
        }
    }

    public Long getUidOrNull(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(z -> z.get(UID_CLAIM))
                .map(Claim::asLong)
                .orElse(null);
    }

}
