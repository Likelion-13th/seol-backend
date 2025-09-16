package likelion13th.shop.login.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import likelion13th.shop.global.api.ErrorCode;
import likelion13th.shop.global.exception.GeneralException;
import likelion13th.shop.login.auth.dto.JwtDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private final Key secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public TokenProvider(
            @Value("${JWT_SECRET}") String secretKey,
            @Value("${JWT_EXPIRATION}") long accessTokenExpiration,
            @Value("${JWT_REFRESH_EXPIRATION}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public JwtDto generateTokens(UserDetails userDetails) {
        log.info("JWT 생성 시작: 사용자 {}", userDetails.getUsername());
        String userId = userDetails.getUsername();
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String accessToken = createToken(userId, authorities, accessTokenExpiration);
        String refreshToken = createToken(userId, null, refreshTokenExpiration);
        log.info("Access/Refresh 토큰 생성 완료 (userId: {})", userId);
        return new JwtDto(accessToken, refreshToken);
    }

    private String createToken(String providerId, String authorities, long expirationTime) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256);

        if (authorities != null) {
            jwtBuilder.claim("authorities", authorities);
        }

        return jwtBuilder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("JWT 검증 실패: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료: {}", e.getClass().getSimpleName());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT 파싱 실패: {}", e.getClass().getSimpleName());
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }
    }

    public Collection<? extends GrantedAuthority> getAuthFromClaims(Claims claims) {
        String authoritiesString = claims.get("authorities", String.class);
        if (authoritiesString == null || authoritiesString.isEmpty()) {
            log.warn("권한 정보 없음 - 기본 ROLE_USER 부여");
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Claims parseClaimsAllowExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
/*
1) 왜 필요한가
카카오 원본 attributes를 우리 서비스 표준 키로 정규화하기 위해 필요.
 (id → provider_id, properties.nickname → nickname)
Spring Security가 principal의 name으로 사용할 키를 명확히 지정해 이후 핸들러/서비스가 동일 식별자(provider_id)로 동작하게 만듭니다.

2) 없으면/틀리면?
getName()이 우리가 기대하는 provider_id가 아니어서 신규 가입/토큰 발급 등 후속 로직이 깨짐.
닉네임 키가 일관되지 않아 화면/로그/저장 로직에서 속성명이 뒤섞임.

3) 핵심 설계 포인트(코드와 함께)
표준 키 확장: 원본 복사 후 명시 키 추가
  Map<String, Object> extended = new HashMap<>(oAuth2User.getAttributes());
  extended.put("provider_id", providerId);
  extended.put("nickname", nickname);

Security 식별 키 고정: nameAttributeKey를 provider_id로 지정
  return new DefaultOAuth2User(
      Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
      extended,
      "provider_id" // getName() == provider_id
  );

최소 권한 부여: 최초 로그인 사용자는 ROLE_USER로 시작
  Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

입력 정규화의 위치: OAuth2UserService 레벨에서 한 번에 표준화하여
  이후 계층이 provider_id/nickname만 신뢰하면 되도록 단순화.
*/

