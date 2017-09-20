# Verigator Java SDK
[![Build Status](https://travis-ci.org/messente/verigator-java.svg?branch=master)](https://travis-ci.org/messente/verigator-java)

An easy to use wrapper for Messente's Verigator two-factor authentication API.

## Documentation

Javadoc for the library  can be found [here](https://messente.com/documentation/verification-api)

## Installation

Verigator SDK can be easily integrated with your existing Gradle or Maven build system.

### Maven
Add the following Bintray Verigator repository definition to your `pom.xml` within the root node:
```xml
<repositories>
    <repository>
        <id>bintray</id>
        <url>http://dl.bintray.com/messente/verigator</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

and insert the following dependency to your `pox.xml` file in the `dependencies` section:

```xml
<dependency>
    <groupId>com.messente.verigator</groupId>
    <artifactId>verigator-java</artifactId>
    <version>1.0</version>
    <type>pom</type>
</dependency>
```

### Gradle


Add the Verigator Bintray repository to your dependencies:
```
maven { url "https://dl.bintray.com/messente/verigator" }
```

and add the following dependency to your `build.gradle` file in the `dependencies` block:
```gradle
compile 'com.messente.verigator:verigator-java:1.0'
```



## Example

Here's some example code to get you started:

```java
package com.messente.verigator.examples.gradle;

import com.messente.verigator.Service;
import com.messente.verigator.User;
import com.messente.verigator.Verigator;
import com.messente.verigator.exceptions.VerigatorException;
import com.messente.verigator.serializers.AuthenticationResponse;
import com.messente.verigator.serializers.VerificationResponse;

import java.util.Scanner;

public class VerificationFlowExample {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";
    public static boolean USE_SMS = true;

    public static void main(String[] args) throws VerigatorException {
        // Initialize the Verigator API with your Messente API credentials
        Verigator verigator = new Verigator(API_USERNAME, API_PASSWORD);

        // Create a Service resource for your service, you do this only once!
        Service service = verigator.createService("Some example service", "www.example.com");

        // Store the returned Service id in your server's configuration files
        String serviceId = service.getServiceId();

        // After creating the service, for all future requests you get Service instance like this:
        // (using the serviceId you saved in the previous step)
        service = Service.get(verigator, serviceId);

        // Now you can start syncing your service's users to Verigator
        User user = service.registerUser("youremail@example.com", "+3725555555");

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
```

Sample projects for [Gradle](https://github.com/messente/verigator-java/tree/master/examples/gradle) and [Maven](https://github.com/messente/verigator-java/tree/master/examples/maven) build systems are also included.
