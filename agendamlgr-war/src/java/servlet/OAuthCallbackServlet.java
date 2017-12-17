package servlet;

import app.ejb.UsuarioFacade;
import app.entity.Usuario;
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
import service.TokensUtils;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Ends the Google OAuth 2.0 login process. If everything went good, it will
 * look in our users table, add it if doesn't exist or modify it if there any
 * changes, and redirect to the index page.
 * @author Melchor Alejo Garau Madrigal
 */
@WebServlet(urlPatterns = { "/oauth/response" })
public class OAuthCallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

    @EJB private UsuarioFacade usuarioFacade;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws IOException {
        //https://coderanch.com/t/542459/java/GoogleAPI-user-info
        //https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=%ACCESS_TOKEN%
        HttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="+credential.getAccessToken());
        HttpResponse res = httpClient.execute(request);

        JsonParser parser = JacksonFactory.getDefaultInstance().createJsonParser(res.getEntity().getContent());
        parser.nextToken();
        UserInfo u = new UserInfo(parser);
        parser.close();

        Usuario usuario = usuarioFacade.find(u.id);
        boolean newcomer = false;
        if(usuario == null) {
            newcomer = true;
            usuario = new Usuario();
            usuario.setId(u.id);
            usuario.setNombre(u.givenName);
            usuario.setApellidos(u.familyName);
            usuario.setEmail(u.email);
            usuario.setTipo((short) 1); //By default, will be registering with type 1 (normal user)
            usuario.setImagen(u.picture);
            usuarioFacade.create(usuario);
        } else {
            //If the user has changed something, reflect that into the DB
            usuario.setNombre(u.givenName);
            usuario.setApellidos(u.familyName);
            usuario.setEmail(u.email);
            usuario.setImagen(u.picture);
            usuarioFacade.edit(usuario);
        }

        String token = TokensUtils.createJwtTokenForUserId(u.id);
        String callbackUrl = (String) req.getSession().getAttribute("callbackUrl");
        if(callbackUrl != null) {
            //If we stored the callback URL during the process, return it
            resp.sendRedirect(callbackUrl + "?token=" + token + "&newcomer=" + newcomer);
            req.getSession().removeAttribute("callbackUrl");
        } else {
            //Otherwise, return a json with the same info
            resp.setContentType("application/json");
            resp.setHeader("access-control-allow-origin", "*");
            resp.getOutputStream().print("{\"token\": \"" + token + "\", \"newcomer\": " + newcomer + "}"); //Token
            resp.getOutputStream().close();
        }
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws IOException {
        String callbackUrl = (String) req.getSession().getAttribute("callbackUrl");
        if(callbackUrl == null) {
            BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
            PrintWriter pw = new PrintWriter(bos);
            resp.setContentType("application/json");
            resp.setHeader("access-control-allow-origin", "*");
            pw.print(String.format(
                    "{\"error\": {\"code\": \"%s\", \"errorMessage\": \"%s\", \"errorDescription\": \"%s\", \"errorUri\": \"%s\", \"state\": \"%s\"}}",
                    errorResponse.getCode(),
                    errorResponse.getError(),
                    errorResponse.getErrorDescription(),
                    errorResponse.getErrorUri(),
                    errorResponse.getState()
            ));
            pw.close();
        } else {
            resp.sendRedirect(callbackUrl + "?notLoggedIn=true");
            req.getSession().removeAttribute("callbackUrl");
        }
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return Shared.newFlow();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest httpServletRequest) {
        return Shared.responseUrl(httpServletRequest);
    }

    @Override
    protected String getUserId(HttpServletRequest httpServletRequest) {
        return null;
    }

    class UserInfo {
        String id;
        String email;
        boolean verifiedEmail;
        String name;
        String givenName;
        String familyName;
        String picture;
        String locale;

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
