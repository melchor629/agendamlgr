package servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = { "/oauth_init" })
public class LoginServlet extends AbstractAuthorizationCodeServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
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
}
