package likelion13th.shop.repository;

import likelion13th.shop.domain.Order;
import likelion13th.shop.global.constant.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime dateTime);
}
//public interface로 구현 없이 메서드의 형태만 정의하는 틀로, Spring에서는 자동 구현을 통해 데이터 접근 로직을 간결하게함