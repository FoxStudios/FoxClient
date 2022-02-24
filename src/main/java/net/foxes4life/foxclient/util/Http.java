package net.foxes4life.foxclient.util;

import net.foxes4life.foxclient.Main;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// this class is not even recoded I just stole it from the old code lmao
public class Http {
    public static HttpResponse get(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        request.setHeader("User-Agent", "FoxClient/"+ Main.VERSION+" Java/"+Main.JAVA_VERSION);
        HttpResponse response;
        try {
            response = client.execute(request);
        } catch (IOException ignored) {
            return null;
        }

        return response;
    }

    public static String getResponseBody(HttpResponse response) {
        if(response == null) return null;
        if(response.getEntity() == null) return null;
        BufferedReader rd;
        StringBuilder result;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        result = new StringBuilder();
        String line;
        try {
            try {
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } catch (NullPointerException ignored) {
            }
        } catch (IOException ignored) {
            return null;
        }
        return result.toString();
    }
}