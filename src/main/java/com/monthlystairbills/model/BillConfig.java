package com.monthlystairbills.model;

import java.util.List;

public class BillConfig {

    private int billNumber;
    private final int year;
    private final int month;
    private final int day;
    private final String communityName;
    private final String communityCity;
    private final List<Recipient> recipients;

    public BillConfig(int billNumber, int year, int month, int day,
                      String communityName, String communityCity,
                      List<Recipient> recipients) {
        this.billNumber = billNumber;
        this.year = year;
        this.month = month;
        this.day = day;
        this.communityName = communityName;
        this.communityCity = communityCity;
        this.recipients = recipients;
    }

    public int getBillNumber() { return billNumber; }
    public void setBillNumber(int billNumber) { this.billNumber = billNumber; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public String getCommunityName() { return communityName; }
    public String getCommunityCity() { return communityCity; }
    public List<Recipient> getRecipients() { return recipients; }
}
