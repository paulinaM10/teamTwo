package com.floor.test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.floor.dto.Order;
import com.floor.dto.Product;
import com.floor.dto.Tax;
import com.floor.service.FloorBusinessLogic;
import com.floor.service.FloorBusinessLogicImpl;

class ServiceTest {
	
	private FloorBusinessLogic floorBusinessLogic;
	
	//@Test
	//call data needed
	//call method
	//perform assertions
	
	@BeforeEach
	public void setUp() {
		floorBusinessLogic = new FloorBusinessLogicImpl();
		
	}
	
	@Test
	public void testGetAllOrdersByDate() {
	LocalDate date = LocalDate.now();
	    
	LinkedList<Order> orders = new LinkedList<>();


	    Order testOrder = new Order(date, 111, "Alice", "California", new BigDecimal("99"), "Tile", new BigDecimal("234"),
	            new BigDecimal("3.20"), new BigDecimal("4.22"), new BigDecimal("345.55"), new BigDecimal("234.44"),
	            new BigDecimal("23.33"), new BigDecimal("2344.44"));
	   
	 orders.add(testOrder);

	    
	 LinkedList<Order> result = floorBusinessLogic.getAllOrdersByDate(date);

	  
	 assertNotNull(result);
	 assertEquals(orders, result);

	}

	
	@Test
	public void testAddOrder() {
		Order testOrder = new Order(LocalDate.now(), 111, "Alice", "California", new BigDecimal("99"), "Tile", new BigDecimal("234"),
		            new BigDecimal("3.20"), new BigDecimal("4.22"), new BigDecimal("345.55"), new BigDecimal("234.44"),
		            new BigDecimal("23.33"), new BigDecimal("2344.44"));
		 
	boolean result = floorBusinessLogic.addOrder(testOrder);
	   
	assertTrue(result);
	   
	}
	
	@Test
	public void testEditOrder() {
		Order testOrder = new Order(LocalDate.now(), 111, "Alice", "California", new BigDecimal("99"), "Tile", new BigDecimal("234"),
	            new BigDecimal("3.20"), new BigDecimal("4.22"), new BigDecimal("345.55"), new BigDecimal("234.44"),
	            new BigDecimal("23.33"), new BigDecimal("2344.44"));
	 
   boolean result = floorBusinessLogic.editOrder(testOrder);
   
   assertTrue(result);
		
		
	}
	@Test
	public void testRemoveOrder() {
		LocalDate date = LocalDate.now();
        int orderNumber = 111;
       
    floorBusinessLogic.removeOrder(date, orderNumber);
		
	}
	@Test
	public void testExportAllData() {
		Order testOrder = new Order(LocalDate.now(), 111, "Alice", "California", new BigDecimal("99"), "Tile", new BigDecimal("234"),
	            new BigDecimal("3.20"), new BigDecimal("4.22"), new BigDecimal("345.55"), new BigDecimal("234.44"),
	            new BigDecimal("23.33"), new BigDecimal("2344.44"));
	
    floorBusinessLogic.exportAllData();

		
		
	}
	@Test
	public void testGetProductTypes() {
	List<String> productTypes = floorBusinessLogic.getProductTypes();
    assertNotNull(productTypes);	
  
	
		
	}
	@Test
	public void testGetTaxRate() {
	String stateAbbreviation = "CA";
	String stateName = "California";
	BigDecimal expectTaxRate = new BigDecimal("23.45");
	
	
	BigDecimal taxRate= floorBusinessLogic.getTaxRate(stateAbbreviation);
	assertNotNull(taxRate);
	assertEquals(expectTaxRate,taxRate);


}}
