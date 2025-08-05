package likelion13th.shop.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import likelion13th.shop.domain.User;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private String usernickname;
    private String phoneNumber;
    private boolean deletable;
    private int recentTotal;

    public static UserInfoResponse from(User user){
        return new UserInfoResponse(
                user.getUsernickname(),
                user.getPhoneNumber(),
                user.isDeletable(),
                user.getRecentTotal()
        );
    }
}
//여러가지 변수를 생성해서 static을 통해 반환