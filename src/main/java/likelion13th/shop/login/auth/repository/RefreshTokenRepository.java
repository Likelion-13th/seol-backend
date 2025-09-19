package likelion13th.shop.login.auth.repository;

import likelion13th.shop.domain.User;
import likelion13th.shop.login.auth.jwt.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
}
/*
1) 왜 필요한가?
RefreshToken 엔티티를 사용자 단위로 관리하기 위한 저장소 레이어.
JPA 기본 CRUD + 사용자별 토큰 조회/삭제 기능 제공.
로그인/재발급/로그아웃 로직이 이 레포지토리를 통해 DB와 상호작용.

2) 없으면/틀리면?
findByUser 없으면 사용자별 RefreshToken 관리 불가 → 재발급 시 토큰 검증 불가.
deleteByUser 구현 누락 시 로그아웃 시점에 토큰 무효화 불가 → 탈취 위험 지속.
@Modifying 빠지면 DELETE JPQL이 실행되지 않아 실제 DB 반영 안 됨.

 */
