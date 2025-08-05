package likelion13th.shop.controller;

import io.swagger.v3.oas.annotations.Operation;

import likelion13th.shop.DTO.response.ItemResponseDto;
import likelion13th.shop.domain.Category;
import likelion13th.shop.domain.Item;
import likelion13th.shop.global.api.ApiResponse;
import likelion13th.shop.global.api.ErrorCode;
import likelion13th.shop.global.api.SuccessCode;
import likelion13th.shop.global.exception.GeneralException;
import likelion13th.shop.login.auth.jwt.CustomUserDetails;
import likelion13th.shop.login.service.UserService;
import likelion13th.shop.service.CategoryService;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}/items")
    @Operation(summary="카테고리별 상품조회", description="해당 카테코리에 있는 모든 아이템을 조회합니다.")
    public ApiResponse<?> getItemsByCategories(@PathVariable Long categoryId){
        log.info("[STEP 1] 카테고리 상품조회 요청 수신");

        try{
            List<ItemResponseDto>categoryItems=categoryService.getItemsByCategoryId(categoryId);
            if (categoryItems.isEmpty()){return ApiResponse.onSuccess(SuccessCode.CATEGORY_ITEMS_EMPTY,Collections.emptyList());}
            return ApiResponse.onSuccess(SuccessCode.CATEGORY_ITEMS_GET_SUCCESS,categoryItems);
        } catch (GeneralException e) {
            log.error("❌ [ERROR] 카테고리 상품 조회중 예외 발생: {}", e.getReason().getMessage());
            throw e;
        }  catch (Exception e){
            log.error("❌ [ERROR] 알 수 없는 예외 발생: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}//request와 get mapping을 활용해서 api호출하는 것을 짜봤다