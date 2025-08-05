package likelion13th.shop.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import likelion13th.shop.domain.User;

@Getter
@AllArgsConstructor
public class UserMileageResponse {
    private int maxMileage;
    private int recentTotal;
    public static UserMileageResponse from(User user){
        return new UserMileageResponse(
                user.getMaxMileage(),
                user.getRecentTotal()
        );
    }
}
//여러가지 변수를 생성해서 static을 통해 반환