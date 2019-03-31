package com.company;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Set;

import big.data.*;

public class AuctionTable extends Hashtable implements Serializable {

    public AuctionTable(){

    }

    public static AuctionTable buildFromURL(String URL) throws IllegalArgumentException, DataSourceException{
        AuctionTable table = null;
        DataSource ds = DataSource.connect(URL).load();
        if(ds != null){
            table = new AuctionTable();
            String[] sellerNames = ds.fetchStringArray("listing/seller_info/seller_name");
            String[] times = ds.fetchStringArray("listing/auction_info/time_left");
            String[] bids = ds.fetchStringArray("listing/auction_info/current_bid");
            String[] ids = ds.fetchStringArray("listing/auction_info/id_num");
            String[] bidderNames = ds.fetchStringArray("listing/auction_info/high_bidder/bidder_name");
            String[] cpus = ds.fetchStringArray("listing/item_info/cpu");
            String[] hardDrives = ds.fetchStringArray("listing/item_info/hard_drive");
            String[] memory = ds.fetchStringArray("listing/item_info/memory");

            String sellerName = "";
            int time = 0;
            double bid = 0;
            String bidderName = "";
            String id = "";
            String cpu = "";
            String hD = "";
            String mem = "";

            for(int i = 0; i < ids.length; i++) {

                if (sellerNames[i] != null)
                    sellerName = sellerNames[i];
                if (bidderNames[i] != null)
                    bidderName = bidderNames[i];
                if (times[i] != null) {
                    String[] s = times[i].split(" ");
                    int hours = 0;
                    for (int j = 0; j < s.length; j++) {
                        if (s[j].contains("days")) {
                            hours += Integer.parseInt(s[j - 1]) * 24;
                        }
                        if (s[j].contains("hours")) {
                            hours += Integer.parseInt(s[j - 1]);
                        }
                    }
                    time = hours;
                }
                if (bids[i] != null) {
                    String s = bids[i];
                    if (s.contains("$")) {
                        s = s.replace("$", "");

                    }
                    if (s.contains(",")) {
                       s = s.replace(",", "");
                    }
                    if (s.contains(" ")) {
                       s = s.replace(" ", "");
                    }
                    bid = Double.parseDouble(s);


                }
                if (cpus[i] != null)
                    cpu = cpus[i];
                if (hardDrives != null)
                    hD = hardDrives[i];
                if (memory[i] != null)
                    mem = memory[i];
                if (ids[i] != null)
                    id = ids[i];


                Auction auction = new Auction(time, bid, id, sellerName, bidderName, cpu + " | " + hD + " | " + mem);
                table.putAuction(id, auction);

            }
            }else{
             throw new IllegalArgumentException("That URL could not be connected to!");
        }


        return table;
    }

    public void putAuction(String AuctionID, Auction auction) throws IllegalArgumentException{
        if(this.get(AuctionID) == null){
            this.put(AuctionID, auction);
        }else
            throw new IllegalArgumentException("That auction already exists!");
    }

    public Auction getAuction(String AuctionID){
       return (Auction)this.get(AuctionID);
    }

    public void letTimePass(int numHours) throws IllegalArgumentException{
        if(numHours > 0) {
            for (Object key : this.keySet()) {
                if (key instanceof String) {
                    if (this.get(key) != null && this.get(key) instanceof Auction) {
                        ((Auction) this.get(key)).decrementTimeRemaining(numHours);
                    }
                }
            }
        }else
            throw new IllegalArgumentException("That value for the time was invalid!");

    }

    public void removeExpiredAuctions(){
        Set keys = this.keySet();
        Object[] keyArr = keys.toArray();
        for (int i = 0; i < keyArr.length; i++) {
            if (keyArr[i] instanceof String) {
                if (this.get(keyArr[i]) != null && this.get(keyArr[i]) instanceof Auction) {
                    Auction a = (Auction)this.get(keyArr[i]);
                    if(a.getTimeRemaining() == 0){
                        this.remove(keyArr[i]);
                    }
                }
            }
        }
    }

    public void printTable(){
        System.out.println("Auction ID |      Bid   |        Seller         |          Buyer          |    Time   |  Item Info");
        System.out.println("===================================================================================================================================");

        for (Object key : this.keySet()) {
            if (key instanceof String) {
                if (this.get(key) != null && this.get(key) instanceof Auction) {
                    Auction a = (Auction)this.get(key);
                    String bid = String.format("%.2f", a.getCurrentBid()) +" ";
                    String bName = a.getBuyerName();
                    if(a.getCurrentBid() == -1){
                        bid = "";
                    }
                    if(a.getBuyerName() == null)
                        bName = "";
                    String s = String.format("%-11s|$%11s|%-23s|%-25s|%11s|%-11s", a.getAuctionID(), bid,
                            " " + a.getSellerName(), " "+bName, a.getTimeRemaining() + " hours ", " "+ a.getItemInfo());
                    System.out.println(s);
                }
            }
        }
    }

    public void save(String name){
        try {
            FileOutputStream file = new FileOutputStream(name);
            ObjectOutputStream outStream = new ObjectOutputStream(file);
            outStream.writeObject(this);
        }catch(IOException e){
            System.out.println("There was an error saving this AuctionTable!");
        }
    }

    public static AuctionTable load(String name) throws ClassNotFoundException, IOException{
            FileInputStream file = new FileInputStream(name);
            ObjectInputStream inStream = new ObjectInputStream(file);


            return (AuctionTable) inStream.readObject();

    }
}

