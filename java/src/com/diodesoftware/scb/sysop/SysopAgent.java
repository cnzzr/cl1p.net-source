package com.diodesoftware.scb.sysop;

import com.diodesoftware.scb.agents.UserAgent;
import com.diodesoftware.scb.agents.FormException;
import com.diodesoftware.scb.agents.UrlPurchaseAgent;
import com.diodesoftware.scb.tables.User;
import com.diodesoftware.scb.tables.UrlCheckout;
import com.diodesoftware.scb.tables.Payment;

import javax.servlet.ServletContext;
import java.sql.Connection;

public class SysopAgent {

    public int queueUrlToBuy(String url, String username, Connection con)
            throws FormException {
        int userId = -1;
        if (username != null) {
            UserAgent ua = UserAgent.getInstance();
            User user = ua.load(username, con);
            if (user == null) throw new FormException("User [" + username + "' Not found");
            userId = user.getNumber();
        }
        UrlPurchaseAgent upa = new UrlPurchaseAgent();
        UrlCheckout checkout = upa.queueUrlToBuy(url, userId, con);
        if (checkout == null)
            throw new FormException("User is already buying that URL");
        return checkout.getNumber();
    }

    public void completeUrlPurchase(int checkoutId, String itemNumber, Connection con, ServletContext context)
            throws FormException {
        UrlPurchaseAgent upa = new UrlPurchaseAgent();
        Payment payment = new Payment();
        payment.setItemNumber(itemNumber);
        payment.setMessage("Interal url purchase from Sysop");
        payment.setCustom(checkoutId + "");
        upa.buyUrl(payment, con, context);
    }

    public void completeUrlTokenPurchase(int checkoutId, String itemNumber, String email, Connection con, ServletContext context) {
        UrlPurchaseAgent upa = new UrlPurchaseAgent();
        Payment payment = new Payment();
        payment.setPayerEmail(email);
        payment.setItemNumber(itemNumber);
        payment.setMessage("Interal url purchase from Sysop");
        payment.setCustom(checkoutId + "");
        upa.buyUrl(payment, con, context);
    }

}
