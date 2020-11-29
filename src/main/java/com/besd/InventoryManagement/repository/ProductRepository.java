package com.besd.InventoryManagement.repository;

import org.springframework.data.repository.CrudRepository;

import com.besd.InventoryManagement.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long>{

	Product findOne(Long id);

}
