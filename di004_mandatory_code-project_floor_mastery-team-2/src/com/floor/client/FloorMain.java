package com.floor.client;
import java.util.Scanner;

import com.floor.presentation.FloorUserInterface;
import com.floor.presentation.FloorUserInterfaceImpl;

public class FloorMain {

    public static void main(String[] args) {
        FloorUserInterface floorUserInterface = new FloorUserInterfaceImpl();
        floorUserInterface.displayMenu();
        while (true) { // a loop to keep the application running until the user exits
            System.out.println("Enter your choice:");
            int choice = new Scanner(System.in).nextInt(); // gets the user's menu choice
            floorUserInterface.performMenu(choice);
        }
    }
}
