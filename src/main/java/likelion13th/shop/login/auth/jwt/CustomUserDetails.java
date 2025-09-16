package likelion13th.shop.login.auth.jwt;

import likelion13th.shop.domain.Address;
import likelion13th.shop.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String providerId;
    private String usernickname;
    private Address address;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.providerId = user.getProviderId();
        this.usernickname = user.getUsernickname();
        this.address = user.getAddress();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public CustomUserDetails(String providerId, String password, Collection<? extends GrantedAuthority> authorities) {
        this.providerId = providerId;
        this.userId = null;
        this.usernickname = null;
        this.authorities = authorities;
        this.address = null;
    }

    public static CustomUserDetails fromEntity(User entity) {
        return CustomUserDetails.builder()
                .userId(entity.getId())
                .providerId(entity.getProviderId())
                .usernickname(entity.getUsernickname())
                .address(entity.getAddress())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .id(this.userId)
                .providerId(this.providerId)
                .usernickname(this.usernickname)
                .address(this.address)
                .build();
    }

    @Override
    public String getUsername() {
        return this.providerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.authorities != null && !this.authorities.isEmpty()) {
            return this.authorities;
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}

/*
1) 왜 필요한가?
 Security의 `UserDetails`를 구현.
DB `User` 엔티티를 보안 컨텍스트에서 사용할 수 있도록 매핑.
JWT 인증 과정에서 `Authentication` 객체에 담겨 사용자 정보와 권한을 제공.
2) 없으면/틀리면?
기본 `UserDetails`로는 providerId, usernickname, address 같은 도메인 속성을 담을 수 없어 서비스 로직과 분리됨.
잘못 구현 시 인증 후 사용자 식별이 불가능하거나 ROLE이 누락되어 인가(authorization)가 제대로 동작하지 않음.
- `getPassword()` 미구현/오류 시 로그인 흐름에서 인증 실패 발생.

3) 핵심 설계 포인트(선택)
- **엔티티 ↔ DTO 변환 지원**
  public static CustomUserDetails fromEntity(User entity)
  public User toEntity()
 */
