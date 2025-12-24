package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByUserId(String userId);

    Page<Order> findByUserId(String userId, Pageable pageable);

    List<Order> findByUserIdOrderByOrderDateDesc(String userId);

    List<Order> findByStatus(Order.Status status);

    List<Order> findByUserIdAndStatus(String userId, Order.Status status);
}
