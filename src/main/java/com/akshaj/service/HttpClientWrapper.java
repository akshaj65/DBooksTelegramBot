package com.akshaj.service;

import com.akshaj.exception.GeneralException;
import com.google.gson.Gson;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientWrapper {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofMinutes(2))
            .build();
    private final Gson gson;
    public HttpClientWrapper(){
        gson= new Gson();
    }
    public <T> T getFromUrl(String url,Class<T> tClass,String errMessage) throws GeneralException {

        try {
            HttpResponse<String> response = getResponseFromUrl(url, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!= HttpStatus.SC_OK){
                throw  new HttpException(errMessage);
            }
            return gson.fromJson(response.body(),tClass);
        } catch (Exception e) {
            throw new GeneralException("Exception in HttpClient"+e.getMessage());
        }


    }

    private HttpResponse<String> getResponseFromUrl(String url, HttpResponse.BodyHandler<String> bodyHandler) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();
        return httpClient.send(request,bodyHandler);

    }

}
