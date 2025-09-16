package likelion13th.shop.login.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class JwtDto {
    private String accessToken;
    private String refreshToken;

    public JwtDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

/*
1) 왜 필요한가?
로그인 성공 시 서버가 발급하는 accessToken/refreshToken를
 클라이언트와 주고받기 위한 객체
2) 없으면/틀리면?
- 토큰을 문자열 두 개로 임의 전달해야 해서 코드 가독성과 유지보수가 떨어짐.
