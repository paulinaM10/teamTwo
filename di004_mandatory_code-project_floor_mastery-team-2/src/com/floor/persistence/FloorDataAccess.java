package com.floor.persistence;


import java.util.LinkedList;
import java.util.List;

import com.floor.dto.Order;
import com.floor.dto.Product;
import com.floor.dto.Tax;

public interface FloorDataAccess {

	public boolean writeOrderFiles(LinkedList<Order> orders);
	
	public boolean writeProductFiles(LinkedList<Product> products);
	
	public boolean writeTaxFiles(LinkedList<Tax> taxes);
	LinkedList<Order> readOrderFile();
    LinkedList<Product> readProductFile();
    LinkedList<Tax> readTaxFile();
	
	
	
}