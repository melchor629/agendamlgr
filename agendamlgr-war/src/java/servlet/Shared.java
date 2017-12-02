package servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Shared code between the OAuth Servlets.
 * @author Melchor Alejo Garau Madrigal
 */
class Shared {

    static AuthorizationCodeFlow newFlow() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
                new InputStreamReader(Shared.class.getResourceAsStream("tokens.json")));
        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                clientSecrets,
                Arrays.asList("https://www.googleapis.com/auth/userinfo.email;https://www.googleapis.com/auth/userinfo.profile".split(";"))
        )//.setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance()).setAccessType("offline")
        .build();
    }

    static String responseUrl(HttpServletRequest servletRequest) {
        return new GenericUrl(
                (servletRequest.isSecure() ? "https://" : "http://") +
                servletRequest.getServerName() + ":" + servletRequest.getServerPort() +
                servletRequest.getContextPath() + "/oauth/response"
        ).build();
    }

}
