package likelion13th.shop.login.auth.jwt;

import jakarta.persistence.*;
import likelion13th.shop.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", unique = true)
    private User user;

    private String refreshToken;

    private Long ttl;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateTtl(Long ttl) {
        this.ttl = ttl;
    }
}

/*
1) 왜 필요한가?
JWT 기반 인증에서 accessToken은 만료 시간이 짧아 자주 재발급 필요.
refreshToken을 DB에 저장해두면 보안상 더 안전하며, 토큰 탈취/재사용 공격 시 DB에서 강제로 무효화가능.
User와 1:1 매핑을 통해 사용자별 refreshToken 관리가 명확해짐.

2) 없으면/틀리면?
RefreshToken을 DB에 저장하지 않으면 서버가 토큰 무효화를 제어할 수 없어서 유효기간 내 탈취된 토큰을 막을 방법이 없음.
User와 매핑 누락 시 여러 개 토큰이 한 사용자에 연결되어 혼란 발생.
ttl(만료 시각) 누락 시 refreshToken의 재발급 주기를 추적할 수 없어 무제한 세션 위험 발생.

3) 핵심 설계 포인트(선택)
User와 1:1 관계 고정
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "users_id", unique = true)
  private User user;
 */

