package servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Starts the Google OAuth 2.0 login process
 * @author Melchor Alejo Garau Madrigal
 */
@WebServlet(urlPatterns = { "/oauth/init" })
public class LoginServlet extends AbstractAuthorizationCodeServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return Shared.newFlow();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getParameter("url");
        String referer = httpServletRequest.getHeader("referer");
        String callbackUrl = referer != null && !referer.isEmpty() ? referer : url;
        if (callbackUrl != null && !callbackUrl.isEmpty()) {
            httpServletRequest.getSession().setAttribute("callbackUrl", callbackUrl);
        }
        return Shared.responseUrl(httpServletRequest);
    }

    @Override
    protected String getUserId(HttpServletRequest httpServletRequest) {
        return null;
    }

}
