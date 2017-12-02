package service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * @author Melchor Alejo Garau Madrigal
 */
public class TokensUtils {

    public static final String googleApiKey;
    public static final String flickrApiKey;
    private static final Algorithm algorithm;
    private static final String issuer = "agendamlgr";

    static {
        @SuppressWarnings("unchecked")
        Map<String, String> tok = new Gson().fromJson(
            new InputStreamReader(TokensUtils.class.getResourceAsStream("tokens.json")),
            Map.class
        );
        googleApiKey = tok.get("google_api_key");
        flickrApiKey = tok.get("flickr_api_key");
        try {
            algorithm = Algorithm.HMAC512(tok.get("jwt_token"));
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a JWT token using a User ID.
     * @param id User ID
     * @return The JWT Token
     */
    public static String createJwtTokenForUserId(String id) {
        try {
            return JWT.create()
                    .withIssuer(issuer)
                    .withExpiresAt(Date.from(Instant.ofEpochMilli(System.currentTimeMillis() + 1000 * 3600)))
                    .withClaim("userId", id)
                    .sign(algorithm);
        } catch(JWTCreationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decodes the JWT, if it is valid.
     * @param token The JWT Token
     * @return returns the decoded token or {@code null} if it is invalid
     */
    public static DecodedJWT decodeJwtToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        try {
            return verifier.verify(token);
        } catch(JWTDecodeException ignore) {
            return null;
        }
    }

    /**
     * From a DecodedJWT, returns the user id
     * @param decodedJWT A DecodedJWT token
     * @return the user id
     */
    public static String getUserIdFromJwtToken(DecodedJWT decodedJWT) {
        Claim c =  decodedJWT.getClaim("userId");
        return c != null ? c.asString() : null;
    }

}
