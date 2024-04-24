package com.example.burgernezz.Models;

import java.util.HashMap;
import java.util.Map;

public class Invoice {
    private String InvoiceID;
    private String email;
    private String phone;
    private String address;
    private Integer PaidPrice;
    private String DateTime;
    private String PaymentType;
    private String PaymentID;
    private String Status;

    public Invoice() {

    }

    public Invoice (String InvoiceID, String email, String phone, String address, Integer PaidPrice, String DateTime, String PaymentType, String PaymentID, String Status){
        this.InvoiceID = InvoiceID;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.PaidPrice = PaidPrice;
        this.DateTime = DateTime;
        this.PaymentType = PaymentType;
        this.PaymentID = PaymentID;
        this.Status = Status;
    }

    public String getInvoiceID() {
        return InvoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        InvoiceID = invoiceID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPaidPrice() {
        return PaidPrice;
    }

    public void setPaidPrice(Integer paidPrice) {
        PaidPrice = paidPrice;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getPaymentType() {return PaymentType;}

    public void setPaymentType(String paymentType) {PaymentType = paymentType;}

    public String getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(String paymentID) {PaymentID = paymentID;}

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
