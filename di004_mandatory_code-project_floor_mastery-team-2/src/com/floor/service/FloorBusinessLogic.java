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

    public boolean editOrder(String filename, int orderNumber, Order editedOrder);
    public boolean removeOrder(String filename, int orderNumber);
    //ADDED
    public int generateUniqueOrderNumber() ;
    public List<String> getAllOrderFiles();
    
    public Order getOrder(String filename, int orderNumber);
    void calculateOrder(Order order);
    public LinkedList<Order> readOrderFile(String filename) ;
    LinkedList<Product> getAllProducts();

    LinkedList<Tax> getAllTaxes();

    List<String> getProductTypes();

    BigDecimal getTaxRate(String stateAbbreviation);

    void exportAllData();

	LinkedList<Order> getAllOrdersByDate(LocalDate date);
	
	void saveQuantity();
}