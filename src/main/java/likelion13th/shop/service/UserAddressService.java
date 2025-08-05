package likelion13th.shop.service;

import jakarta.transaction.Transactional;
import likelion13th.shop.domain.Address;
import likelion13th.shop.domain.User;
import likelion13th.shop.repository.UserRepository;
import likelion13th.shop.DTO.request.AddressRequest;
import likelion13th.shop.DTO.response.AddressResponse;
import likelion13th.shop.DTO.response.UserMileageResponse;
import likelion13th.shop.DTO.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAddressService {
    final UserRepository userRepository;
    @Transactional
    public UserInfoResponse getUserInfo(User user){return UserInfoResponse.from(user);}
    @Transactional
    public AddressResponse getAddress(Address address){return AddressResponse.from(address);}
    @Transactional
    public UserMileageResponse getUserMileage(User user){return UserMileageResponse.from(user);}
}
//response from를 일관적으로 사용해 잘 읽히도록 하였다