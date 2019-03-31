package com.company;

import java.io.Serializable;

public class Auction implements Serializable {

    private int timeRemaining;
    private double currentBid;
    private String auctionID;
    private String sellerName;
    private String buyerName;
    private String itemInfo;

    public Auction(){

    }

    public Auction(int time, double bid, String id, String sName, String bName, String info){
        timeRemaining = time;
        currentBid = bid;
        auctionID = id;
        sellerName = sName;
        buyerName = bName;
        itemInfo = info;
    }

    public void decrementTimeRemaining(int time){
        timeRemaining -= time;
        if(timeRemaining < 0)
            timeRemaining = 0;
    }

    public void newBid(String bidderName, double bidAmt) throws ClosedAuctionException{
        if(timeRemaining > 0){
            if(bidAmt > currentBid){
                currentBid = bidAmt;
                buyerName = bidderName;
            }else{
                System.out.println("The amount bid must be larger than the current bid on the Auction!");
            }
        }else
            throw new ClosedAuctionException("That auction is closed!");
    }

    @Override
    public String toString() {
        String bName;
        String s = "Auction "+this.getAuctionID()+":"+ "\n";


        if(this.getBuyerName() == null){
             bName = "";
        }else{
            bName = this.getBuyerName();
        }
        s += String.format("%-5s%-10s","", "Seller: " + this.getSellerName())+ "\n";
        s += String.format("%-5s%-10s", "","Buyer: " + bName) + "\n";
        s += String.format("%-5s%-10s", "","Time: " + this.getTimeRemaining())+ "\n";
        s += String.format("%-5s%-10s", "","Info: " + this.getItemInfo())+ "\n";
        return s;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public String getAuctionID() {
        return auctionID;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getItemInfo() {
        return itemInfo;
    }

    public String getSellerName() {
        return sellerName;
    }
}
