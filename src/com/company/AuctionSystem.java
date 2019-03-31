package com.company;

import big.data.DataSourceException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AuctionSystem {
    //TODO JavaDocs
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Starting...");
        AuctionTable auctions = null;
        try {
            auctions = AuctionTable.load("auctions.obj");
        }catch (IOException | ClassNotFoundException e){
           // System.out.println("There was an error loading a previous AuctionTable.");
        }
        if(auctions == null){
            auctions = new AuctionTable();
            System.out.println("No previous auction table found, creating a new table...");
        }else{
            System.out.println("Previous auction table found, now opening...");
        }
        System.out.print("Please enter a username: ");
        String username = s.nextLine();

        String operation = "";
        while(!operation.equalsIgnoreCase("q")){
            System.out.println("Menu:\n" +
                    "    (D) - Import Data from URL\n" +
                    "    (A) - Create a New Auction\n" +
                    "    (B) - Bid on an Item\n" +
                    "    (I) - Get Info on Auction\n" +
                    "    (P) - Print All Auctions\n" +
                    "    (R) - Remove Expired Auctions\n" +
                    "    (T) - Let Time Pass\n" +
                    "    (Q) - Quit\n");

            System.out.print("Please select an option: ");
            operation = s.nextLine();
            switch(operation.toLowerCase().trim()){
                case "d":
                    System.out.print("Please enter a URL: ");
                    String URL = s.nextLine();
                    try {
                        auctions = AuctionTable.buildFromURL(URL);
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }catch (DataSourceException e){
                        System.out.println("There was an error in finding the Data Source!");
                    }
                    break;
                case "p":
                    auctions.printTable();
                    break;
                case "q":
                    System.out.println("Writing auction table to file...");
                    auctions.save("auctions.obj");
                    System.out.println("Done!");
                    System.out.println("Goodbye");
                    break;
                case "a":
                    try {
                        System.out.println("Creating new auction as " + username);
                        System.out.print("Please enter an Auction ID: ");
                        int id = s.nextInt();
                        s.nextLine();
                        System.out.print("Please enter an Auction time(hours): ");
                        int time = s.nextInt();
                        s.nextLine();
                        System.out.print("Please enter some Item Info: ");
                        String info = s.nextLine();

                        Auction a = new Auction(time, -1, String.valueOf(id), username, null, info);
                        auctions.putAuction(String.valueOf(id), a);
                    }catch(IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }catch(InputMismatchException e){
                        System.out.println("That input is invalid!");
                    }
                    break;
                case "t":
                    try {
                        System.out.print("How many hours should pass: ");
                        int time = s.nextInt();
                        s.nextLine();
                        System.out.println("Time passing...");
                        auctions.letTimePass(time);
                        System.out.println("Auction times updated!");
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "b":
                    System.out.print("Please enter an auction ID: ");
                    try {
                        int id = s.nextInt();
                        s.nextLine();
                        Auction a = null;
                        if (auctions.getAuction(Integer.toString(id)) != null) {
                            a = auctions.getAuction(Integer.toString(id));
                            System.out.print("Please enter a bid amount: ");
                            double bid = s.nextDouble();
                            s.nextLine();
                            a.newBid(username, bid);
                        } else {
                            System.out.println("That auction could not be found!");
                        }
                    }catch (InputMismatchException e){
                        System.out.println("That input is invalid!");
                    }catch (ClosedAuctionException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "r":
                    auctions.removeExpiredAuctions();
                    System.out.println("All expired auctions removed!");
                    break;
                case "i":
                    try {
                        System.out.print("Please enter an auction ID: ");
                        String p;
                        int id = s.nextInt();
                        s.nextLine();
                        if (auctions.getAuction(Integer.toString(id)) != null) {
                            p = auctions.getAuction(Integer.toString(id)).toString();
                        } else {
                            p = "An auction with that ID could not be found!";
                        }
                        System.out.println(p);
                    }catch (InputMismatchException e){
                        s.nextLine();
                        System.out.println("That input is invalid!");
                    }
                    break;
                default:
                    System.out.println("That operation is invalid!");
                    break;
            }
        }
    }
}
