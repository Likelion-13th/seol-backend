package likelion13th.shop.login.auth.utils;

import likelion13th.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String providerId = oAuth2User.getAttributes().get("id").toString();
        
        @SuppressWarnings("unchecked")
        Map<String,Object> properties=
                (Map<String, Object>) oAuth2User.getAttributes().getOrDefault("properties", Collections.emptyMap());
        String nickname= properties.getOrDefault("nickname","카카오사용자").toString();
        
        Map<String,Object>  extendAttributes= new HashMap<>(oAuth2User.getAttributes());
        extendAttributes.put("provider_id",providerId);
        extendAttributes.put("nickname",nickname);
        
        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                extendAttributes,
                "provider_id"
        );
        
    }
    
}
/*
1) 왜 필요한가?
카카오 OAuth2 응답의 속성들을 우리 서비스에서 일관되게 쓰기 위한 정규화 (id → provider_id, properties.nickname → nickname)
Spring Security가 principal의 식별자로 사용할 키(nameAttributeKey)를 명확히 지정하여 이후 핸들러/서비스에서 동일 식별자(provider_id)로 로직을 단순화합니다.

2) 없으면/틀리면?
getName()이 기대하는 provider_id가 아니어서 신규 가입/토큰 발급/조회 등 후속 로직이 깨짐.
닉네임 키가 일관되지 않으면 화면/로그/저장 로직에서 속성명이 뒤섞여 버그가 발생.
-nameAttributeKey를 잘못 지정하면 SecurityContext에 들어가는 주체 식별이 달라져 인가가 실패.

3) 핵심 설계 포인트 (코드와 함께) — 선택
- 표준 키 확장:
  Map<String, Object> extended = new HashMap<>(oAuth2User.getAttributes());
  extended.put("provider_id", providerId);
  extended.put("nickname", nickname);

- Security 식별 키 고정:
  return new DefaultOAuth2User(
      Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
      extended,
      "provider_id" // getName() == provider_id);

 */