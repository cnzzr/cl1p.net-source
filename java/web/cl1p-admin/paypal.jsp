<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="com.diodesoftware.dbmapper.DBMapper" %>
<%@ page import="com.diodesoftware.scb.tables.User" %>
<%@ page import="com.diodesoftware.scb.tables.Payment" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.scb.agents.UrlPurchaseAgent" %>
<%@ page import="com.diodesoftware.scb.licensegen.GenLicense" %>
<%@ page import="com.diodesoftware.scb.ClipUtil" %>
<%!
    private static Logger log = Logger.getLogger("jsp.paypal");
%>
<%
    if (log.isDebugEnabled()) log.debug("Processing Paypal IPN - Start");
    try {
// read post from PayPal system and add 'cmd'
        Enumeration en = request.getParameterNames();
        String str = "cmd=_notify-validate";
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            String paramValue = request.getParameter(paramName);
            str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
        }

// post back to PayPal system to validate
// NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
// using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
// and configured for older versions.
        if (log.isDebugEnabled()) log.debug("PayPal: Starting postback");
        URL u = new URL("http://www.paypal.com/cgi-bin/webscr");
        URLConnection uc = u.openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        PrintWriter pw = new PrintWriter(uc.getOutputStream());
        pw.println(str);
        pw.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(uc.getInputStream()));
        String res = in.readLine();
        in.close();
        if (log.isDebugEnabled()) log.debug("PayPal: Postback complete. Response is [" + res + "]");
// assign posted variables to local variables
        String itemName = request.getParameter("item_name");
        String itemNumber = request.getParameter("item_number");
        String paymentStatus = request.getParameter("payment_status");
        String paymentAmount = request.getParameter("mc_gross");
        String paymentCurrency = request.getParameter("mc_currency");
        String txnId = request.getParameter("txn_id");
        String receiverEmail = request.getParameter("receiver_email");
        String payerEmail = request.getParameter("payer_email");
        String custom = request.getParameter("custom");

//check notification validation
        if (res.equals("VERIFIED")) {
            DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
            Connection con = dbConnectionMgr.getConnection();
            DBMapper mapper = DBMapper.getInstance();
            try {
                Payment payment = new Payment();
                payment.setItemName(itemName);
                payment.setItemNumber(itemNumber);
                payment.setPaymentStatus(paymentStatus);
                payment.setPaymentAmount(paymentAmount);
                payment.setPaymentCurrency(paymentCurrency);
                payment.setTxnId(txnId);
                payment.setReceiverEmail(receiverEmail);
                payment.setPayerEmail(payerEmail);
                payment.setCustom(custom);
                String msg = "No Message";
// check that paymentStatus=Completed
// check that txnId has not been previously processed
// check that receiverEmail is your Primary PayPal email
// check that paymentAmount/paymentCurrency are correct
// process payment
                if ("Completed".equals(paymentStatus)) {
                    try {
                        int userID = Integer.parseInt(custom);
                        if ("CL1P_1YEAR".equals(itemNumber)) {
                            User user = (User) mapper.load(User.class, userID, con);
                            if (user != null) {
                                if (user.isPro()) {
                                    msg = "User [" + user.getNumber() + "] is already Pro";
                                    payment.setResultType(Payment.USER_ALREADY_PRO);
                                } else {
                                    user.setPro(true);
                                    mapper.save(user, con);
                                    msg = "User [" + user.getNumber() + "] upgraded to pro";
                                    payment.setResultType(Payment.USER_UPGRADED);
                                }
                            } else {
                                msg = "UseIDr [" + userID + "] does not exist";
                                payment.setResultType(Payment.USER_DOES_NOT_EXIST);
                            }
                        }
                    } catch (NumberFormatException e) {
                        msg = "Custom Filed value [" + custom + "] is not a number";
                        payment.setResultType(Payment.CUSTOM_NOT_A_NUMBER);
                    }
                } else {
                    msg = "Payment status not 'Completed'";
                    payment.setResultType(Payment.PAYMENT_NOT_COMPLETE);
                }
                payment.setMessage(msg);
                mapper.save(payment, con);
                if (itemNumber.startsWith("URL-")) {
                    UrlPurchaseAgent pa = new UrlPurchaseAgent();
                    pa.buyUrl(payment, con, config.getServletContext());
                }
                if (itemNumber.startsWith("CLIPBOARD-CORE")) {
                    String firstName = request.getParameter("first_name");
                    String lastName = request.getParameter("last_name");
                    String businessName = request.getParameter("payer_business_name");
                    if(ClipUtil.isBlank(firstName)){
                        firstName = "";
                    }
                    if(ClipUtil.isBlank(lastName)){
                        lastName = "";
                    }
                    String name = firstName + " " + lastName;
                    if(ClipUtil.isBlank(name)){
                        name = payerEmail;
                    }
                    if (ClipUtil.isBlank(businessName)){
                        businessName = name;
                    }
                    float amt = Float.parseFloat(paymentAmount);
                    if("CLIPBOARD-CORE".equals(itemNumber))
                        GenLicense.genLicense(payerEmail, name, businessName,txnId, con);
                    else if("CLIPBOARD-CORE-BASIC".equals(itemNumber)){
                        GenLicense.genBasicLicense(payerEmail, name, businessName,txnId, con);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing payment", e);
            } finally {
                dbConnectionMgr.returnConnection(con);
            }
        } else if (res.equals("INVALID")) {
// log for investigation
            log.error("Invalid response code from paypal. IP[" + request.getRemoteHost() + "]");
        } else {
// error
            log.error("Unexpected response type [" + res + "]");
        }
    } catch (Throwable e) {
        log.error("Error processing PayPal IPN [" + e.getMessage() + "]", e);
    }
%>
