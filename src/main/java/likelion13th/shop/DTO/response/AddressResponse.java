package likelion13th.shop.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import likelion13th.shop.domain.Address;

@Getter
@AllArgsConstructor
public class AddressResponse {
    private String zipcode;
    private String address;
    private String addressDetail;

    public static AddressResponse from(Address address) {
        return new AddressResponse(
                address.getZipcode(),
                address.getAddress(),
                address.getAddressDetail()
        );
    }
}
//string zip코드등을 생성해서 static을 통해 반환
