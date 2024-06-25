package dk.arasbuilds.jobactions.Utils;

import java.util.Random;

public class OrderIDGenerator {

    // Method to generate a unique order ID
    public static String generateOrderID() {
        long timestamp = System.currentTimeMillis(); // Get the current timestamp
        int randomNum = new Random().nextInt(1000); // Generate a random number between 0 and 999

        // Combine timestamp and random number to create the unique order ID
        String orderID = "ORD" + timestamp + String.format("%03d", randomNum);

        return orderID;
    }

    public static void main(String[] args) {
        // Generate and print a unique order ID
        String uniqueOrderID = generateOrderID();
        System.out.println("Generated Order ID: " + uniqueOrderID);
    }
}
