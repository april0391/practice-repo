package toy.board.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import toy.board.domain.entity.User;
import toy.board.util.CookieUtils;
import toy.board.util.JwtUtils;
import toy.board.util.cache.UserCache;

import static toy.board.util.JwtConst.*;

@Slf4j
@RequiredArgsConstructor
public class JwtSessionManager implements SessionManager {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final UserCache userCache;

    @Override
    public void createSession(User user, HttpServletRequest request, HttpServletResponse response) {
        Long id = user.getId();
        String accessToken = jwtUtils.createToken(String.valueOf(id), EXPIRATION_TIME_SECOND_OF_ACCESS_TOKEN);
        String refreshToken = jwtUtils.createToken(String.valueOf(id), EXPIRATION_TIME_SECOND_OF_REFRESH_TOKEN);

        cookieUtils.createCookieAndAddToResponse(ACCESS_TOKEN_NAME, accessToken, EXPIRATION_TIME_SECOND_OF_ACCESS_TOKEN, response);
        cookieUtils.createCookieAndAddToResponse(REFRESH_TOKEN_NAME, refreshToken, EXPIRATION_TIME_SECOND_OF_REFRESH_TOKEN, response);

        userCache.putSession(user);
    }

    @Override
    public Object getSessionData(HttpServletRequest request, HttpServletResponse response) {
        return request.getAttribute(ACCESS_ATTRIBUTE);
    }

    @Override
    public void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieUtils.getCookieValue(ACCESS_TOKEN_NAME, request);

        String subject = jwtUtils.getSubject(accessToken);
        userCache.removeSession(Long.valueOf(subject));

        cookieUtils.invalidateCookie(ACCESS_TOKEN_NAME, request, response);
        cookieUtils.invalidateCookie(REFRESH_TOKEN_NAME, request, response);
    }

}
