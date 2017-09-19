package com.messente.verigator.examples.gradle;

import com.messente.verigator.Service;
import com.messente.verigator.User;
import com.messente.verigator.Verigator;
import com.messente.verigator.VerigatorResponse;
import com.messente.verigator.exceptions.VerigatorException;
import com.messente.verigator.serializers.AuthenticationResponse;
import com.messente.verigator.serializers.VerificationResponse;

import java.util.Scanner;

public class VerificationFlowExample {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";
    public static boolean USE_SMS = false;

    public static void main(String[] args) throws VerigatorException {
        // Initialize the Verigator API with your Messente API credentials
        Verigator verigator = new Verigator(API_USERNAME, API_PASSWORD);

        // Create a Service resource for your service, you do this only once!
        Service service = verigator.createService("Uku service 35", "www.example.com");

        // Store the returned Service id in your server's configuration files
        String serviceId = service.getServiceId();

        // After creating the service, for all future requests you get Service instance like this:
        // (using the serviceId you saved in the previous step)
        service = Service.get(verigator, serviceId);

        // Now you can start syncing your service's users to Verigator
        User user = service.registerUser("ukuloskit@gmail.com", "+37253448365");

        // You can use SMS or TOTP (verification via Verigator app)
        if (USE_SMS) {
            user.authenticateUsingSMS();
        } else {
            user.authenticateUsingTotp();
        }

        Scanner reader = new Scanner(System.in);
        VerificationResponse verificationResponse = null;

        while (verificationResponse == null || !verificationResponse.isVerified()){
            System.out.println("Enter the PIN: ");
            String pin = reader.nextLine();
            verificationResponse = user.verifyPin(pin);
        }
        System.out.println("Verification successful!");

    }


}

