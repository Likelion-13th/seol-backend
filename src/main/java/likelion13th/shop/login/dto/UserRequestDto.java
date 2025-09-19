package likelion13th.shop.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@Builder
public class UserRequestDto {
    @Schema(description = "UserReqDto")
    @Getter
    @AllArgsConstructor
    public static class UserReqDto{
        private String userId;
        private String usernickname;
        private String providerId;
    }
}
