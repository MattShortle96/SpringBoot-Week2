package com.besd.InventoryManagement.repository;

import org.springframework.data.repository.CrudRepository;

import com.besd.InventoryManagement.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Long>{

}
