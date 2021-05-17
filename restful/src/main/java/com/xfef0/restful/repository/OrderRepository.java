package com.xfef0.restful.repository;

import com.xfef0.restful.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
