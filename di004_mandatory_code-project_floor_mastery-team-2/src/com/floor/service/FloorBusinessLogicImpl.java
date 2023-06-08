package com.floor.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.floor.dto.Order;
import com.floor.dto.Product;
import com.floor.dto.Tax;
import com.floor.persistence.FloorDataAccess;
import com.floor.persistence.FloorDataAccessImpl;

public class FloorBusinessLogicImpl implements FloorBusinessLogic {
    private LinkedList<Order> ordersList = new LinkedList<>();
    private FloorDataAccess dataAccess;

    public FloorBusinessLogicImpl() {
        this.dataAccess = new FloorDataAccessImpl();
    }

    @Override
    public boolean addOrder(Order order) {
        // Generate order number based on the next available order #
        int nextOrderNumber = getNextOrderNumber();
        order.setOrderNumber(nextOrderNumber);

        // Perform calculations
        calculateOrder(order);

        // Add order to the list
        return ordersList.add(order);
    }

    @Override
    public boolean editOrder(LocalDate orderDate, int orderNumber, Order editedOrder) {
        Order existingOrder = getOrder(orderDate, orderNumber);

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
            if (order.getDate().equals(orderDate) && order.getOrderNumber() == orderNumber) {
                iterator.remove();
                break;
            }
        }
        return ordersList.add(editedOrder);
    }

    @Override
    public boolean removeOrder(LocalDate orderDate, int orderNumber) {
        Iterator<Order> iterator = ordersList.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getDate().equals(orderDate) && order.getOrderNumber() == orderNumber) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public LinkedList<Order> getAllOrders() {
        return ordersList;
    }

    @Override
    public Order getOrder(LocalDate orderDate, int orderNumber) {
        for (Order order : ordersList) {
            if (order.getDate().equals(orderDate) && order.getOrderNumber() == orderNumber) {
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
            BigDecimal area = order.getArea();
            BigDecimal materialCost = costPerSquareFoot.multiply(area);
            order.setMaterialCost(materialCost);
        } else {
            order.setMaterialCost(BigDecimal.ZERO); // Set to zero if product not found
        }

        // Calculate labour cost
        if (product != null) {
            BigDecimal labourCostPerSquareFoot = product.getLabourCostPerSquareFoot();
            BigDecimal area = order.getArea();
            BigDecimal labourCost = labourCostPerSquareFoot.multiply(area);
            order.setLabourCost(labourCost);
        } else {
            order.setLabourCost(BigDecimal.ZERO); // Set to zero if product not found
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
        LinkedList<Order> orders = getAllOrders();
        dataAccess.writeOrderFiles(orders);
    }

    private int getNextOrderNumber() {
        int maxOrderNumber = ordersList.stream().mapToInt(Order::getOrderNumber).max().orElse(0);
        return maxOrderNumber + 1;
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
}
