package org.example.jpaadvance.repository;

import org.example.jpaadvance.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
