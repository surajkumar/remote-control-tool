package io.github.surajkumar.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressInfo.class);
    private static final String API_URL = "https://api64.ipify.org";
    public static String getLocalAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static String getPublicAddress() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();
        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new Exception(e);
        }
    }

    public static String getAddress() {
        String address;
        try {
            address = getPublicAddress();
        } catch (Exception ex) {
            LOGGER.error("You are not connected to the internet: " + ex.getMessage());
            try {
                address = getLocalAddress();
            } catch (UnknownHostException e) {
                LOGGER.error("You are not connected to a network interface");
                address = "localhost";
            }
        }
        return address;
    }

}
