/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bookstoreapp;

import java.time.YearMonth;

/**
 *
 * @author afoud
 */
public class Payment {

    private double priceToCharge;
    private boolean paymentSuccess = false;
    
    public Payment(double priceToCharge) {
        this.priceToCharge = priceToCharge;
        
    }
    
    public boolean takePayment(Long cardNumber, YearMonth yearMonth, int cardCvv) {
        // No payment serivce for the prototype. Using boolean to simulate taking payment 
        boolean paymentSuccessfull = true;
        return paymentSuccessfull;
    }
}
