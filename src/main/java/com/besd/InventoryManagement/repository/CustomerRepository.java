package com.besd.InventoryManagement.repository;

import org.springframework.data.repository.CrudRepository;

import com.besd.InventoryManagement.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long>{

}
