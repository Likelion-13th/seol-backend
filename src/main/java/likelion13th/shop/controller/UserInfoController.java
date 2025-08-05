package likelion13th.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import likelion13th.shop.DTO.request.AddressRequest;
import likelion13th.shop.DTO.response.AddressResponse;
import likelion13th.shop.DTO.response.UserMileageResponse;
import likelion13th.shop.DTO.response.UserInfoResponse;
import likelion13th.shop.domain.User;
import likelion13th.shop.global.api.ApiResponse;
import likelion13th.shop.global.api.ErrorCode;
import likelion13th.shop.global.api.SuccessCode;
import likelion13th.shop.global.exception.GeneralException;
import likelion13th.shop.login.auth.jwt.CustomUserDetails;
import likelion13th.shop.login.service.UserService;
import likelion13th.shop.service.OrderService;
import likelion13th.shop.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserService userService;
    private final UserAddressService userAddressService;

    @GetMapping("/profile")
    @Operation(summary = "내 정보 조회", description = "로그인한 유저의 정보를 조회합니다.")
    public ApiResponse<?> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        log.info("[STEP 1]  내 정보 조회 요청 수신");

        try {
            User user=userService.findByProviderId(customUserDetails.getProviderId())
                    .orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
            UserInfoResponse userInfoResponse=userAddressService.getUserInfo(user);
            log.info("[STEP 2] 내 정보 조회 성공");

            return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, userInfoResponse);
        } catch (GeneralException e) {
            log.error("❌ [ERROR] 내 정보 조회 중 예외 발생: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ [ERROR] 알 수 없는 예외 발생: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/mileage")
    @Operation(summary = "내 마일리지 조회", description = "로그인한 유저의 마일리지를 조회합니다.")
    public ApiResponse<?> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        log.info("[STEP 1]  내 마일리지 조회 요청 수신");

        try {
            User user=userService.findByProviderId(customUserDetails.getProviderId())
                    .orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
            UserMileageResponse userMileageResponse=userAddressService.getUserMileage(user);
            log.info("[STEP 2] 내 마일리지 조회 성공");

            return ApiResponse.onSuccess(SuccessCode.USER_MILEAGE_SUCCESS, userMileageResponse);
        } catch (GeneralException e) {
            log.error("❌ [ERROR] 내 정보 조회 중 예외 발생: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ [ERROR] 알 수 없는 예외 발생: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/address")
    @Operation(summary = "내 주소 조회", description = "로그인한 유저의 주소를 조회합니다.")
    public ApiResponse<?> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        log.info("[STEP 1]  내 주소 조회 요청 수신");

        try {
            User user=userService.findByProviderId(customUserDetails.getProviderId())
                    .orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
            AddressResponse addressResponse=userAddressService.getAddress(user.getAddress());
            log.info("[STEP 2] 내 주소 조회 성공");

            return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, userInfoResponse);
        } catch (GeneralException e) {
            log.error("❌ [ERROR] 내 주소 조회 중 예외 발생: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ [ERROR] 알 수 없는 예외 발생: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }//try catch문을 사용하여서 정보조회를 하는 과정과 오류가 발생하면 일관적으로 반환을 한다
    }
}
