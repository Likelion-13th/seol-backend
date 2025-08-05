package likelion13th.shop.domain;

import jakarta.persistence.*;
import likelion13th.shop.domain.entity.BaseEntity;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Table(name="categorys")
@NoArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    @Column(name="category_id")
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    @ManyToMany
    private List<Item> items=new ArrayList<>();

    public Category(String categoryName) {this.categoryName=categoryName;}
}
//manytomany를 자동으로 해결하기위해 선언하였다