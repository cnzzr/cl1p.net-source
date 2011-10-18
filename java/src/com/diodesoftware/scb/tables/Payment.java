package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Oct 15, 2006
 * Time: 1:04:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Payment implements DatabaseEntry {

    private int number;
    private String itemName = null;
    private String itemNumber = null;
    private String paymentStatus = null;
    private String paymentAmount = null;
    private String paymentCurrency = null;
    private String txnId = null;
    private String receiverEmail = null;
    private String payerEmail = null;
    private String custom = null;
    private String message = null;
    private int resultType = 0;
    private Calendar paymentDate = Calendar.getInstance();

    public static final int USER_UPGRADED = 1;
    public static final int PAYMENT_NOT_COMPLETE = 2;
    public static final int CUSTOM_NOT_A_NUMBER = 3;
    public static final int USER_DOES_NOT_EXIST = 4;
    public static final int USER_ALREADY_PRO = 5;
    public static final int UNEXPECTED_ERROR = 6;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("ItemName", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("ItemNumber", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("PaymentStatus", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("PaymentAmount", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("PaymentCurrency", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("TxnId", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("ReceiverEmail", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("PayerEmail", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("Custom", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("Message", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("PaymentDate", DatabaseColumnType.DATE12),
            new DatabaseColumn("ResultType", DatabaseColumnType.DECIMAL)
    };

    public DatabaseColumn[] columns() {
        return columns;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentCurrency() {
        return paymentCurrency;
    }

    public void setPaymentCurrency(String paymentCurrency) {
        this.paymentCurrency = paymentCurrency;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public Calendar getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Calendar paymentDate) {
        this.paymentDate = paymentDate;
    }
}

