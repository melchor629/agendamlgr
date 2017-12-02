package servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = { "/oauth" })
public class OAuthCallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
        BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
        resp.setContentType("text/html");
        PrintWriter pw = new PrintWriter(bos);
        pw.println(credential.getAccessToken());
        pw.print("<br>");
        pw.println(credential.getClock().currentTimeMillis());
        pw.print("<br>");
        pw.println(credential.getExpirationTimeMilliseconds());
        pw.print("<br>");
        pw.println(credential.getExpiresInSeconds());
        pw.print("<br>");
        pw.println(credential.getRefreshToken());
        pw.print("<br>");
        pw.println(credential.getTokenServerEncodedUrl());
        pw.print("<br>");
        pw.println("<a href=\"https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="+credential.getAccessToken()+"\">Ver info</a>");

        HttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="+credential.getAccessToken());
        HttpResponse res = httpClient.execute(request);

        JsonParser parser = JacksonFactory.getDefaultInstance().createJsonParser(res.getEntity().getContent());
        parser.nextToken();
        UserInfo u = new UserInfo(parser);
        parser.close();

        pw.print("<br>");
        pw.println("Hola " + u.givenName);

        //https://coderanch.com/t/542459/java/GoogleAPI-user-info
        //https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=%ACCESS_TOKEN%
        pw.close();
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
        BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
        PrintWriter pw = new PrintWriter(bos);
        pw.println("Sad");
        pw.println(errorResponse.getCode());
        pw.println(errorResponse.getError());
        pw.println(errorResponse.getErrorDescription());
        pw.println(errorResponse.getErrorUri());
        pw.println(errorResponse.getState());
        pw.close();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        return Shared.newFlow();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest httpServletRequest) throws ServletException, IOException {
        return Shared.responseUrl();
    }

    @Override
    protected String getUserId(HttpServletRequest httpServletRequest) throws ServletException, IOException {
        return null;
    }

    public class UserInfo {
        public String id;
        public String email;
        public boolean verifiedEmail;
        public String name;
        public String givenName;
        public String familyName;
        public String picture;
        public String locale;

        UserInfo(JsonParser parser) throws IOException {
            while(parser.nextToken() == JsonToken.FIELD_NAME) {
                switch(parser.getCurrentName()) {
                    case "id": parser.nextToken(); id = parser.getText(); break;
                    case "email": parser.nextToken(); email = parser.getText(); break;
                    case "verified_email": parser.nextToken(); verifiedEmail = parser.getText().equals("true"); break;
                    case "name": parser.nextToken(); name = parser.getText(); break;
                    case "given_name": parser.nextToken(); givenName = parser.getText(); break;
                    case "family_name": parser.nextToken(); familyName = parser.getText(); break;
                    case "picture": parser.nextToken(); picture = parser.getText(); break;
                    case "locale": parser.nextToken(); locale = parser.getText(); break;
                    default: parser.nextToken();
                }
            }
        }

    }
}
