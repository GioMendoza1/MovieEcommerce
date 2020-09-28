package edu.uci.ics.gamendo1.service.billing.resources;



import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.gamendo1.service.billing.BillingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayPalClient {
    private String clientId = "Aas5uSd4Tm99LG17RHcQBnJ-BitNHadP4fJ7QQGuBmQMFTwTp1inzEbAq-r7xnofEazjLmyygZhjLN4O";
    private String clientSecret = "EKZku1G4RkQ_gAf8JwLc9c0gaJo_Nc6HlyWNZslejNunZHyy7wi-g86t0mMMKIF12lM9HgeXlId9wp8-";

    public PayPalClient() {}

    public Map<String, Object> createPayment(String sum){
        Map<String, Object> response = new HashMap<String, Object>();
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:4200/cancel");
        //redirectUrls.setReturnUrl("http://localhost:6543/api/billing/order/complete");
        redirectUrls.setReturnUrl("http://" + BillingService.getConfigs().getHostName() + ":" + Integer.toString(BillingService.getConfigs().getPort()) + "/api/billing/order/complete");
        payment.setRedirectUrls(redirectUrls);
        Payment createdPayment;
        try {
            String redirectUrl = "";
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            createdPayment = payment.create(context);
            if(createdPayment!=null){
                List<Links> links = createdPayment.getLinks();
                for (Links link:links) {
                    if(link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
                response.put("boolean", "true");
            }
        } catch (PayPalRESTException e) {
            System.out.println("Error happened during payment creation!");
            response.put("boolean", "false");
        }
        return response;
    }

    public Map<String, Object> completePayment(String paymentId, String payerId){
        Map<String, Object> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment!=null){
                response.put("status", "success");
                response.put("payment", createdPayment);
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
            response.put("status", "failure");
        }
        return response;
    }
}
