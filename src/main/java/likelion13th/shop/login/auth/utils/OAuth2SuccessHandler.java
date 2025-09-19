package likelion13th.shop.login.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13th.shop.domain.Address;
import likelion13th.shop.domain.User;
import likelion13th.shop.login.auth.dto.JwtDto;
import likelion13th.shop.login.auth.jwt.CustomUserDetails;
import likelion13th.shop.login.auth.service.JpaUserDetailsManager;
import likelion13th.shop.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JpaUserDetailsManager jpaUserDetailsManager;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String providerId = (String) oAuth2User.getAttribute("provider_id");
        String nickname   = (String) oAuth2User.getAttribute("nickname");

        String maskedPid  = (providerId != null && providerId.length() > 4) ? providerId.substring(0, 4) + "***" : "***";
        String maskedNick = (nickname != null && !nickname.isBlank()) ? "*(hidden)*" : "(none)";
        log.info("OAuth2 Success - providerId(masked)={}, nickname={}", maskedPid, maskedNick);

        if (!jpaUserDetailsManager.userExists(providerId)) {
            User newUser = User.builder()
                    .providerId(providerId)
                    .usernickname(nickname)
                    .deletable(true)
                    .build();

            newUser.setAddress(new Address("10540", "경기도 고양시 덕양구 항공대학로 76", "한국항공대학교"));

            CustomUserDetails userDetails = new CustomUserDetails(newUser);
            jpaUserDetailsManager.createUser(userDetails);
            log.info("신규 회원 등록 완료 - providerId(masked)={}", maskedPid);
        } else {
            log.info("기존 회원 로그인 - providerId(masked)={}", maskedPid);
        }

        JwtDto jwt = userService.jwtMakeSave(providerId);
        log.info("JWT 발급 완료 - providerId(masked)={}", maskedPid);

        String frontendRedirectUri = request.getParameter("redirect_uri");
        List<String> authorizedUris = List.of(
                "https://seolshop.netlify.app/",
                "http://localhost:3000"
        );
        if (frontendRedirectUri == null || !authorizedUris.contains(frontendRedirectUri)) {
            frontendRedirectUri = "https://seolshop.netlify.app/";
        }

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUri)
                .queryParam("accessToken", jwt.getAccessToken())
                .build()
                .toUriString();

        log.info("Redirecting to authorized frontend host: {}", frontendRedirectUri);

        response.sendRedirect(redirectUrl);
    }
}
/*
1) 왜 필요한가? OAuth2 인증 성공 이후 후속 처리(신규 회원 가입, JWT 발급, 프런트로 리다이렉트)를 한 지점에서 수행해 로그인 플로우를 표준화.
- 정규화된 attributes(provider_id/nickname)를 사용해 DB 사용자와 보안 컨텍스트를 일관되게 연결.
- 오픈 리다이렉트 방지(허용 리스트)와 PII 마스킹 로깅으로 보안과 운영 안정성을 확보.

2) 없으면/틀리면?
신규 사용자 자동 가입/기존 사용자 인식이 누락되어 로그인 후 화면 진입이 실패하거나 매번 수동 가입이 필요.
JWT 미발급/오발급 시 프런트가 인증 상태를 설정하지 못해 즉시 로그아웃 상태로 보이거나 권한 자원이 401/403을 유발합니다.
리다이렉트 검증이 없으면 임의의 외부 URL로 토큰이 유출될 수 있는 오픈 리다이렉트 취약점이 생김.
PII(개인식별정보)를 그대로 로그에 남기면 보안/컴플라이언스 리스크가 커짐.


 */

