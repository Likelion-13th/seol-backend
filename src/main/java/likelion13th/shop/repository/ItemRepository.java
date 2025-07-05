package likelion13th.shop.repository;

import likelion13th.shop.domain.Item;
import likelion13th.shop.global.constant.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface ItemRepository  extends JpaRepository<Item, Long>{
    Optional<Item>  findById(Long itemId );
}