package com.floor.presentation;

import com.floor.service.FloorBusinessLogic;
import com.floor.service.FloorBusinessLogicImpl;
import com.floor.dto.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;

public class FloorUserInterfaceImpl implements FloorUserInterface {
    private FloorBusinessLogic businessLogic = new FloorBusinessLogicImpl();
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void displayMenu() {
        System.out.println("1. Display Orders");
        System.out.println("2. Add an Order");
        System.out.println("3. Edit an Order");
        System.out.println("4. Remove an Order");
        System.out.println("5. Export All Data");
        System.out.println("6. Quit");
    }

    @Override
    public void performMenu(int choice) {
        switch (choice) {
            case 1:
                displayOrders();
                break;
            case 2:
                addOrder();
                break;
            case 3:
                editOrder();
                break;
            case 4:
                removeOrder();
                break;
            case 5:
                exportAllData();
                break;
            case 6:
                System.out.println("Exiting...");
                scanner.close();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid Choice");
        }
    }

    private void displayOrders() {
        System.out.println("Enter a date (MM/dd/yyyy): ");
        String dateInput = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(dateInput, formatter);

        LinkedList<Order> orders = businessLogic.getAllOrdersByDate(date);

        if (!orders.isEmpty()) {
            for (Order order : orders) {
                System.out.println(order);
            }
        } else {
            System.out.println("No orders found for the specified date.");
        }
    }

    private void addOrder() {
        Order order = getInputOrder();

        if (businessLogic.addOrder(order)) {
            System.out.println("Order Added!");
        } else {
            System.out.println("Order Not Added!");
        }
    }

    private Order getInputOrder() {
        Order order = new Order();

        System.out.println("Enter order date (MM/dd/yyyy): ");
        String dateInput = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate orderDate = LocalDate.parse(dateInput, formatter);
        order.setDate(orderDate);

        System.out.println("Enter Customer Name: ");
        String customerName = scanner.nextLine();
        order.setCustomerName(customerName);

        System.out.println("Enter State: ");
        String state = scanner.nextLine();
        order.setState(state);

        System.out.println("Enter Product Type: ");
        String productType = scanner.nextLine();
        order.setProductType(productType);

        System.out.println("Enter Area: ");
        BigDecimal area = scanner.nextBigDecimal();
        order.setArea(area);
        scanner.nextLine(); // Consume the newline character

        businessLogic.calculateOrder(order);

        return order;
    }

    private void editOrder() {
        System.out.println("Enter order date (MM/dd/yyyy): ");
        String dateInput = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate orderDate = LocalDate.parse(dateInput, formatter);

        System.out.println("Enter order number: ");
        int orderNumber = scanner.nextInt();
        scanner.nextLine();

        Order existingOrder = businessLogic.getOrder(orderDate, orderNumber);

        if (existingOrder == null) {
            System.out.println("Order not found!");
            return;
        }

        Order editedOrder = getInputOrder();

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

        businessLogic.calculateOrder(editedOrder);

        System.out.println("Edited Order Summary:");
        System.out.println(editedOrder);

        System.out.println("Save the edited order? (Y/N)");
        String saveChoice = scanner.nextLine();
        if (saveChoice.equalsIgnoreCase("Y")) {
            if (businessLogic.editOrder(orderDate, orderNumber, editedOrder)) {
                System.out.println("Order edited successfully.");
            } else {
                System.out.println("Failed to edit the order.");
            }
        }
    }

    private void removeOrder() {
        System.out.println("Enter order date (MM/dd/yyyy): ");
        String dateInput = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate orderDate = LocalDate.parse(dateInput, formatter);

        System.out.println("Enter order number: ");
        int orderNumber = scanner.nextInt();
        scanner.nextLine();

        Order existingOrder = businessLogic.getOrder(orderDate, orderNumber);

        if (existingOrder == null) {
            System.out.println("Order not found!");
            return;
        }

        System.out.println("Order Details:");
        System.out.println(existingOrder);

        System.out.println("Are you sure you want to remove this order? (Y/N)");
        String removeChoice = scanner.nextLine();
        if (removeChoice.equalsIgnoreCase("Y")) {
            if (businessLogic.removeOrder(orderDate, orderNumber)) {
                System.out.println("Order removed successfully.");
            } else {
                System.out.println("Failed to remove the order.");
            }
        }
    }

    private void exportAllData() {
        businessLogic.exportAllData();
        System.out.println("Data exported successfully.");
    }
}
