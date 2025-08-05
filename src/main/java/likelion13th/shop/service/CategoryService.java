package likelion13th.shop.service;

import jakarta.transaction.Transactional;
import likelion13th.shop.DTO.response.ItemResponseDto;
import likelion13th.shop.domain.Category;
import likelion13th.shop.global.api.ErrorCode;
import likelion13th.shop.global.exception.GeneralException;
import likelion13th.shop.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public List<ItemResponseDto> getItemsByCategoryId(Long categoryId){
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));
        return category.getItems().stream().map(ItemResponseDto::from).collect(Collectors.toList());
    }
}
//Transactional은 메서드 내 데이터베이스 작업이 전부 성공할 때만 적용되도록 보장하는 안전장치