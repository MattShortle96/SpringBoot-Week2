package com.besd.InventoryManagement.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.besd.InventoryManagement.entity.Customer;
import com.besd.InventoryManagement.entity.Order;
import com.besd.InventoryManagement.entity.Product;
import com.besd.InventoryManagement.repository.CustomerRepository;
import com.besd.InventoryManagement.repository.OrderRepository;
import com.besd.InventoryManagement.repository.ProductRepository;
import com.besd.InventoryManagement.util.MembershipLevel;
import com.besd.InventoryManagement.util.OrderStatus;

@Service
public class OrderService {
	
	private static final Logger logger = LogManager.getLogger(OrderService.class);
	private final int DELIVERY_DAYS = 7;
	
	@Autowired
	private OrderRepository repo;
	
	@Autowired
	private CustomerRepository custRepo;
	
	@Autowired
	private ProductRepository prodRepo;
	
	public Order submitNewOrder(Set<Long> prodIds, Long customerId) throws Exception{
		try {
			Customer customer = custRepo.findOne(customerId);
			Order order = initializeNewOrder(prodIds, customer);
			return repo.save(order);
		}catch(Exception e) {
			logger.error("Exception occured while trying to create new folder for customer: " + customerId, e);
			throw e;
		}
	}
	
	public Order cancelOrder(Long orderId) throws Exception{
		try {
			Order order = repo.findOne(orderId);
			order.setStatus(order.CANCELED);
			return repo.save(order);
		}catch(Exception e) {
			logger.error("Exception occured while trying to cancel order: " + orderId, e);
			throw new Exception("Unable to update order.");
		}
	}
	
	public Order completeOrder(Long orderId) throws Exception{
		try {
			Order order = repo.findOne(orderId);
			order.setStatus(order.DELIVERED);
			return repo.save(order);
		}catch(Exception e) {
			logger.error("Exception occured while trying to complete order: " + orderId, e);
			throw new Exception("Unable to complete order.");
		}
	}
	
	private Order initializeNewOrder(Set<Long> prodIds, Customer customer) {
		Order order = new Order();
		order.setProducts(convertToProductSet(prodRepo.findAll(prodIds)));
		order.setOrdered(LocalDate.now());
		order.setEstimatedDelivery(LocalDate.now().plusDays(DELIVERY_DAYS));
		order.setCustomer(customer);
		order.setInvoiceAmount(calculateOrderTotal(order.getProducts(), customer.getLevel()));
		order.setStatus(OrderStatus.ORDERED);
		addOrderToProducts(order);
		return order;
	}

	private void addOrderToProducts(Order order) {
		Set<Product> products = order.getProducts();
		for(Product product : products) {
			product.getOrders().add(order);
		}
		
	}
	
	private Set<Product> convertToProductSet(Iterable<Product> iterable){
		Set<Product> set = new HashSet<Product>();
		for(Product product : iterable) {
			set.add(product);
		}
		return set;
	}

	private double calculateOrderTotal(Set<Product> products, MembershipLevel level) {
		double total = 0;
		for(Product product : products) {
			total += product.getPrice();
		}
		return total - total * level.getDiscount();
	}
	
	

}
