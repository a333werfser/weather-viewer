package edu.example.interceptor;

import edu.example.model.AuthSession;
import edu.example.model.User;
import edu.example.repository.AuthSessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthSessionInterceptor implements HandlerInterceptor {

    private final AuthSessionRepository authSessionRepository;

    public AuthSessionInterceptor(AuthSessionRepository authSessionRepository) {
        this.authSessionRepository = authSessionRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String authSessionId = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("id")) {
                    authSessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (authSessionId != null) {
            AuthSession session = authSessionRepository.findAuthSessionById(UUID.fromString(authSessionId));
            User user = session.getUser();
            request.setAttribute("user", user);
            return true;
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return false;
        }
    }

}
