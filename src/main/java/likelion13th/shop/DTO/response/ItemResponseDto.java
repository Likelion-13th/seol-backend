package likelion13th.shop.DTO.response;

import likelion13th.shop.domain.Category;
import likelion13th.shop.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class ItemResponseDto {
    private Long itemId;
    private String itemName;
    private int price;
    private String imagePath;
    private String brand;
    private boolean isNew;
    private List<Category> categories;

    public static ItemResponseDto from(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getItemName(),
                item.getPrice(),
                item.getImagePath(),
                item.getBrand(),
                item.isNew(),
                item.getCategories()
        );

    }
}
//여러가지 변수를 생성해서 static을 통해 반환