package com.floor.persistence;

import java.util.LinkedList;
import java.util.List;

import com.floor.dto.Order;
import com.floor.dto.Product;
import com.floor.dto.Tax;

public interface FloorDataAccess {

    boolean writeOrderFiles(LinkedList<Order> orders);

    boolean writeProductFiles(LinkedList<Product> products);

    boolean writeTaxFiles(LinkedList<Tax> taxes);
	
    public LinkedList<Order> readOrderFile(String fileName);

    LinkedList<Product> readProductFile();

    LinkedList<Tax> readTaxFile();

    
    
    List<String> getOrderFiles();
    LinkedList<Order> readOrderFiles(List<String> filenames);



}
