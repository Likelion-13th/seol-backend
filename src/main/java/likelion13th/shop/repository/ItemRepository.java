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
//findById로 주어진 ID로 카테고리를 조회하고, 없을 경우를 대비해 Optional로 감싸 반환