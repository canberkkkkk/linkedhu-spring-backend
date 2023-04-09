package linkedhu.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JWTHandler {
    private static final String SECRET_KEY = "secret";
    private static final int TIME_GIVEN = 24 * 60 * 60 * 1000;
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());

    public static UsernamePasswordAuthenticationToken getAuthToken(String token) throws Exception {
        if (token == null)
            return null;

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (int i = 0; i < roles.length; i++)
            authorities.add(new SimpleGrantedAuthority(roles[i]));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public static String createToken(String subject, String issuer, Collection<? extends GrantedAuthority> claims) {
        if (subject == null || issuer == null)
            return null;

        return JWT
                .create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + TIME_GIVEN))
                .withIssuer(issuer)
                .withClaim("roles", claims.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }
}
