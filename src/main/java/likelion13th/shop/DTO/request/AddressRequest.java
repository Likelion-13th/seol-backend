package likelion13th.shop.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequest {
    private String zipcode;
    private String address;
    private String addressDetail;
}
//필드들의 getter 메서드를 자동 생성해 불변 객체처럼 사용하게된다