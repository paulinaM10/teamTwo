package com.floor.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.io.File;

import com.floor.dto.Order;
import com.floor.dto.Product;
import com.floor.dto.Tax;
import com.floor.persistence.FloorDataAccess;
import com.floor.persistence.FloorDataAccessImpl;

public class FloorBusinessLogicImpl implements FloorBusinessLogic {
    private static final String ORDER_FOLDER_PATH = "Orders/";
    private static final String FILE_EXT = ".txt";
    
    private LinkedList<Order> ordersList = new LinkedList<>();
    private FloorDataAccessImpl dataAccess = new FloorDataAccessImpl();

    public FloorBusinessLogicImpl() {
        this.dataAccess = new FloorDataAccessImpl();
    }

    @Override
    public boolean addOrder(Order order) {
        // Generate order number based on the next available order #
        int nextOrderNumber = generateUniqueOrderNumber();
        order.setOrderNumber(nextOrderNumber);

        // Perform calculations
        calculateOrder(order);

        // Add order to the list
        return ordersList.add(order);
    }
    

    @Override
    public int generateUniqueOrderNumber() {
        int maxOrderNumber = ordersList.stream()
                .mapToInt(Order::getOrderNumber)
                .max()
                .orElse(0);
        return maxOrderNumber + 1;
    }

    @Override
    public boolean editOrder(String filename, int orderNumber, Order editedOrder) {
        List<Order> ordersList = readOrderFile(filename); 

        Order existingOrder = null;
        for (Order order : ordersList) {
            if (order.getOrderNumber() == orderNumber) {
                existingOrder = order;
                break;
            }
        }

        if (existingOrder == null) {
            return false;
        }

        // Preserve existing data if user hits Enter without entering new data
        if (editedOrder.getCustomerName().isEmpty()) {
            editedOrder.setCustomerName(existingOrder.getCustomerName());
        }
        if (editedOrder.getState().isEmpty()) {
            editedOrder.setState(existingOrder.getState());
        }
        if (editedOrder.getProductType().isEmpty()) {
            editedOrder.setProductType(existingOrder.getProductType());
        }
        if (editedOrder.getArea().equals(BigDecimal.ZERO)) {
            editedOrder.setArea(existingOrder.getArea());
        }

        // Perform calculations
        calculateOrder(editedOrder);

        // Replace the order in the list
        Iterator<Order> iterator = ordersList.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getOrderNumber() == orderNumber) {
                iterator.remove();
                break;
            }
        }
        ordersList.add(editedOrder);

        // Write updated orders back to the file
        return dataAccess.writeOrderFile(filename, ordersList); // This method should return a boolean indicating success or failure
    }

    @Override
    public boolean removeOrder(String filename, int orderNumber) {
    	 List<Order> ordersList = readOrderFile(filename); // Adjust this line based on how you read the file

    	    // Find the order to remove
    	    Order orderToRemove = null;
    	    for (Order order : ordersList) {
    	        if (order.getOrderNumber() == orderNumber) {
    	            orderToRemove = order;
    	            break;
    	        }
    	    }

    	    // If order is not found, return false
    	    if (orderToRemove == null) {
    	        return false;
    	    }
    	    // If order is found, remove it from the list
    	    ordersList.remove(orderToRemove);

    	    // Write updated orders back to the file
    	    return dataAccess.writeOrderFile(filename, ordersList); // This method should return a boolean indicating success or failure
    	}
    	    

    @Override
    public LinkedList<Order> readOrderFile(String filename) {
        return dataAccess.readOrderFile(filename);
    }

    @Override
    public Order getOrder(String filename, int orderNumber) {
        List<Order> ordersList = readOrderFile(filename); // Adjust this line based on how you read the file
        
        for (Order order : ordersList) {
            if (order.getOrderNumber() == orderNumber) {
                return order;
            }
        }
        return null;
    }


    @Override
    public void calculateOrder(Order order) {
        // Fetch product and tax data
        LinkedList<Product> products = getAllProducts();
        LinkedList<Tax> taxes = getAllTaxes();

        // Calculate material cost
        Product product = getProductByType(products, order.getProductType());
        if (product != null) {
            BigDecimal costPerSquareFoot = product.getCostPerSquareFoot();
            BigDecimal labourCostPerSquareFoot = product.getLabourCostPerSquareFoot();
            order.setCostPerSquareFoot(costPerSquareFoot);
            order.setLabourCostPerSquareFoot(labourCostPerSquareFoot);
            
            
            
            BigDecimal area = order.getArea();
            BigDecimal materialCost = costPerSquareFoot.multiply(area);
            BigDecimal labourCost = labourCostPerSquareFoot.multiply(area);
            order.setMaterialCost(materialCost);
            order.setLabourCost(labourCost);
        } else {
        	 order.setCostPerSquareFoot(BigDecimal.ZERO);
             order.setLabourCostPerSquareFoot(BigDecimal.ZERO);
             order.setMaterialCost(BigDecimal.ZERO);
             order.setLabourCost(BigDecimal.ZERO);
        }

       

        // Calculate tax
        String state = order.getState();
        Tax tax = getTaxByState(taxes, state);
        if (tax != null) {
            BigDecimal taxRate = tax.getTaxRate();
            BigDecimal subtotal = order.getMaterialCost().add(order.getLabourCost());
            BigDecimal taxAmount = subtotal.multiply(taxRate.divide(BigDecimal.valueOf(100)));
            order.setTax(taxAmount);
        } else {
            order.setTax(BigDecimal.ZERO); // Set to zero if tax not found
        }

        // Calculate total
        BigDecimal total = order.getMaterialCost().add(order.getLabourCost()).add(order.getTax());
        order.setTotal(total);
    }

    @Override
    public LinkedList<Product> getAllProducts() {
        return dataAccess.readProductFile();
    }

    @Override
    public LinkedList<Tax> getAllTaxes() {
        return dataAccess.readTaxFile();
    }

    @Override
    public List<String> getProductTypes() {
        LinkedList<Product> products = getAllProducts();
        return products.stream().map(Product::getProductType).collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTaxRate(String stateAbbreviation) {
        LinkedList<Tax> taxes = getAllTaxes();
        Tax tax = getTaxByStateAbbreviation(taxes, stateAbbreviation);
        if (tax != null) {
            return tax.getTaxRate();
        }
        // Default return, might not be accurate
        return BigDecimal.ZERO;
    }

    @Override
    public void exportAllData() {
//        LinkedList<Order> orders = getAllOrders();
//        dataAccess.writeOrderFiles(orders);
    }

   

    private Product getProductByType(LinkedList<Product> products, String productType) {
        for (Product product : products) {
            if (product.getProductType().equalsIgnoreCase(productType)) {
                return product;
            }
        }
        return null;
    }

    private Tax getTaxByState(LinkedList<Tax> taxes, String state) {
        for (Tax tax : taxes) {
            if (tax.getStateName().equalsIgnoreCase(state)) {
                return tax;
            }
        }
        return null;
    }

    private Tax getTaxByStateAbbreviation(LinkedList<Tax> taxes, String stateAbbreviation) {
        for (Tax tax : taxes) {
            if (tax.getStateAbbreviation().equalsIgnoreCase(stateAbbreviation)) {
                return tax;
            }
        }
        return null;
    }
    
    @Override
    public LinkedList<Order> getAllOrdersByDate(LocalDate date) {
        LinkedList<Order> ordersByDate = new LinkedList<>();
        for (Order order : ordersList) {
            if (order.getDate().equals(date)) {
                ordersByDate.add(order);
            }
        }
        return ordersByDate;
    }

   @Override
	public List<String> getAllOrderFiles() {
	   final String ORDER_FOLDER_PATH = "Orders/";
	   List<String> orderFiles = new ArrayList<>();

	   File directory = new File(ORDER_FOLDER_PATH);
	    File[] files = directory.listFiles();

	    if (files != null) {
	        for (File file : files) {
	            if (file.isFile()) {
	                orderFiles.add(file.getName());
	            }
	        }
	    }
	    return orderFiles;
	}

   @Override

   public void saveQuantity() {

	   dataAccess.writeOrderFile("Orders_" , ordersList);

   }


   
   
   
}