package com.floor.test;

import static org.junit.jupiter.api.Assertions.*;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

import com.floor.dto.Order;
import com.floor.dto.Product;
import com.floor.dto.Tax;
import com.floor.persistence.FloorDataAccess;
import com.floor.persistence.FloorDataAccessImpl;

class PersistenceTest {
	
	private FloorDataAccess floorDataAccess;
    private LinkedList<Order> testOrders;
    private LinkedList<Product> testProducts;
    private LinkedList<Tax> testTaxes;
    

	
	@BeforeEach
	public void setUp() {
		 floorDataAccess = new FloorDataAccessImpl();
	    //    testOrders = new LinkedList<>();
	    }

		

	
	@Test
	public void testWriteOrderFile() {
	    LinkedList<Order> testOrders = new LinkedList<>();
	    Order testOrder = new Order(LocalDate.now(), 111, "Alice", "California", new BigDecimal("99"), "Tile", new BigDecimal("234"),
	            new BigDecimal("3.20"), new BigDecimal("4.22"), new BigDecimal("345.55"), new BigDecimal("234.44"),
	            new BigDecimal("23.33"), new BigDecimal("2344.44"));
	   
	    testOrders.add(testOrder);

	    boolean result = floorDataAccess.writeOrderFiles(testOrders);
	    assertTrue(result);
	}

	
	@Test void testReadOrderFile() {
	LinkedList<Order> result=floorDataAccess.readOrderFile();
	assertEquals(testOrders, result);
	
	
	}
	@Test
	public void testWriteProductFile() {
		LinkedList<Product> testProducts = new LinkedList<>();
		Product testProduct = new Product("Carpet", new BigDecimal("2.3"), new BigDecimal(5.77));
		
		testProducts.add(testProduct);
		
		boolean result= floorDataAccess.writeProductFiles(testProducts);
		assertTrue(result);
		
	}
	@Test void testReadProductFile() {
		LinkedList<Product> result=floorDataAccess.readProductFile();
		assertEquals(testOrders, result);
		
		
	}
	@Test
	public void testWriteTaxFile() {
		LinkedList<Tax> testTaxes = new LinkedList<>();
		Tax testTax = new Tax("CA", "California", new BigDecimal("3.4"));
		
		testTaxes.add(testTax);
		
		boolean result= floorDataAccess.writeTaxFiles(testTaxes);
		assertTrue(result);
		
	}
	@Test void testReadTaxFile() {
		LinkedList<Tax> result=floorDataAccess.readTaxFile();
		assertEquals(testOrders, result);
		
		
	}
	
	
}