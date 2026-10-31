package com.prasad.fooddelivery.application;

import com.prasad.fooddelivery.common.db.DatabaseConfig;
import com.prasad.fooddelivery.customer.repository.CustomerRepository;
import com.prasad.fooddelivery.customer.repository.JdbcCustomerRepository;
import com.prasad.fooddelivery.order.service.OrderService;

import javax.sql.DataSource;

// Composition root: the only place that knows which concrete classes are used.
// Everything else depends on interfaces. Spring's container will take over this job later.
public class FoodDeliveryApplication {

    public static void main(String[] args) {
        DataSource dataSource = DatabaseConfig.createDataSource();
        DatabaseConfig.migrate(dataSource);

        CustomerRepository customerRepository = new JdbcCustomerRepository(dataSource);
        OrderService orderService = new OrderService(customerRepository);

        System.out.println("Food delivery application started");
    }
}
