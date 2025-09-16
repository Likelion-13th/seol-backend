package likelion13th.shop.login.auth.service;

import likelion13th.shop.domain.User;
import likelion13th.shop.global.api.ErrorCode;
import likelion13th.shop.global.exception.GeneralException;
import likelion13th.shop.login.auth.jwt.CustomUserDetails;
import likelion13th.shop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JpaUserDetailsManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String providerId) throws UsernameNotFoundException {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> {
                    log.warn("유저 정보 없음 (provider_id): {}", providerId);
                    throw new GeneralException(ErrorCode.USER_NOT_FOUND);
                });
        return CustomUserDetails.fromEntity(user);
    }

    @Override
    public void createUser(UserDetails user) {
        log.info("사용자 생성 시도 중 (provider_id): {}", user.getUsername());

        if (userExists(user.getUsername())) {
            log.warn("이미 존재하는 사용자 (provider_id): {}", user.getUsername());
            throw new GeneralException(ErrorCode.ALREADY_USED_NICKNAME);
        }

        try {
            User newUser = ((CustomUserDetails) user).toEntity();
            userRepository.save(newUser);
            log.info("사용자 생성 완료 (provider_id): {}", user.getUsername());
        } catch (ClassCastException e) {
            log.error("UserDetails → CustomUserDetails 변환 실패 (provider_id): {}", user.getUsername(), e);
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean userExists(String providerId) {
        log.info("사용자 존재 여부 확인 (provider_id): {}", providerId);
        return userRepository.existsByProviderId(providerId);
    }

    @Override
    public void updateUser(UserDetails user) {
        log.error("사용자 정보 업데이트는 지원되지 않음 (provider_id): {}", user.getUsername());
        throw new UnsupportedOperationException("사용자 업데이트 기능은 아직 지원되지 않습니다.");
    }

    @Override
    public void deleteUser(String providerId) {
        log.error("사용자 삭제는 지원되지 않음 (provider_id): {}", providerId);
        throw new UnsupportedOperationException("사용자 삭제 기능은 아직 지원되지 않습니다.");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        log.error("비밀번호 변경은 지원되지 않음.");
        throw new UnsupportedOperationException("비밀번호 변경 기능은 아직 지원되지 않습니다.");
    }
}
/*
1) 왜 필요한가?
Spring Security의 UserDetailsManager 구현으로, providerId 기반 사용자 조회/생성 책임을 분리.
OAuth2/JWT 흐름에서 `UserDetails` ↔ 도메인 `User` 간 변환 지점을 표준화하여 인증 이후의 계정 생성·조회 로직을 일관되게 관리.

2) 없으면/틀리면?
기본 InMemory/Jdbc 기반 매니저로는 `providerId`, `address`, `usernickname` 등 프로젝트 도메인 필드를 다루기 어려워 비즈니스 로직 분기가 복잡해짐.
`loadUserByUsername`이 잘못 구현되면 인증 성공 후 SecurityContext에 잘못된 주체가 들어가 인가 실패(403)나 NPE가 발생.
`createUser`에서 `UserDetails` 캐스팅/매핑이 틀리면 DB에 잘못된 유저가 저장되거나 런타임 ClassCastException으로 가입 플로우가 중단.
