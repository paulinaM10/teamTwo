package com.floor.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import com.floor.dto.Order;
import com.floor.dto.Product;
import com.floor.dto.Tax;

public interface FloorBusinessLogic {
    boolean addOrder(Order order);

    boolean editOrder(LocalDate orderDate, int orderNumber, Order editedOrder);

    boolean removeOrder(LocalDate orderDate, int orderNumber);

    LinkedList<Order> getAllOrders();

    Order getOrder(LocalDate orderDate, int orderNumber);

    void calculateOrder(Order order);

    LinkedList<Product> getAllProducts();

    LinkedList<Tax> getAllTaxes();

    List<String> getProductTypes();

    BigDecimal getTaxRate(String stateAbbreviation);

    void exportAllData();

	LinkedList<Order> getAllOrdersByDate(LocalDate date);
}
