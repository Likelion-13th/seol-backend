package likelion13th.shop.repository;

import likelion13th.shop.domain.Category;
import likelion13th.shop.global.constant.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface CategoryRepository  extends JpaRepository<Category, Long>{
    Optional<Category>  findById(Long itemCategory );
}