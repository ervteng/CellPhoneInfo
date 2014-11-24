package com.crossmobile.phonetracker;

import android.net.Uri;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by aloknerurkar on 9/3/14.
 */

public class HTTP_payload {

    HttpClient client;
    HttpHost host;
    URI uri;
    URL url;
    int content_length;

    public HTTP_payload(HttpClient client, HttpHost host, URI uri, URL url){
        this.client = client;
        this.host = host;
        this.uri = uri;
        this.url = url;

        URLConnection openConnection = null;
        try {
            openConnection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        content_length = openConnection.getContentLength();

    }



    public HttpResponse send_http_get(){
        HttpGet new_get = new HttpGet(uri);
        HttpResponse response = null;
        try {
            response = client.execute(new_get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void send_http_get(Header header){
        HttpGet new_get = new HttpGet();
        new_get.addHeader(header);

        try {
            client.execute(host,new_get);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send_http_get(HttpParams params){
        HttpGet new_get = new HttpGet(uri);
        new_get.setParams(params);

        try {
            client.execute(host,new_get);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send_http_get(HttpEntity entity, Header header, HttpParams params){
        HttpGet new_get = new HttpGet();
        new_get.addHeader(header);
        new_get.setParams(params);

        try {
            client.execute(host,new_get);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send_http_post(HttpEntity entity){
        HttpPost new_post = new HttpPost();
        new_post.setEntity(entity);
        try {
            client.execute(host,new_post);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void send_http_head(HttpClient client, HttpHost host){
        HttpHead new_head = new HttpHead();
        try {
            client.execute(host,new_head);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
