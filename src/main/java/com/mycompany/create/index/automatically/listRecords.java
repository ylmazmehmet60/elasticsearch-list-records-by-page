/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.create.index.automatically;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ASOS
 */
public class listRecords {

    static String urlServer = "http://localhost:900";

    public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException {

        String searchText = "tez";
        String page = "10"; 

        listRecords ya = new listRecords();

        String JSON = ya.JSONIsle(urlServer, searchText, page);

        ArrayList<icerikBean> islenmissonuc  = ya.sonuclarıGoster(JSON);
   
        
        //abi burada jsp tarafında bu formatta gosterbilrsin
        for (int i = 0; i < islenmissonuc.size(); i++) {
            String title = islenmissonuc.get(i).getTitle();
            System.out.println(title);
        }
        
        

    }

    private String JSONIsle(String urlServer, String searchText, String page) throws MalformedURLException, IOException {
        String urly = urlServer + "/INDEXNAME/_search";
        URL obj = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        String donenJson = Query.getInstance().GetGeneralQuery(searchText, page);

        wr.writeBytes(donenJson);
        wr.flush();
        wr.close();

        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            System.out.println("Response Code : " + HttpResult);
        } else {
            System.out.println("Response Code Err " + con.getResponseMessage());
        }

        BufferedReader iny = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = iny.readLine()) != null) {
            response.append(output);
        }
        iny.close();

        return response.toString();

    }


    public ArrayList<icerikBean> sonuclarıGoster(String JSON) {
        org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();

        ArrayList<icerikBean> sonuclarListesi = new ArrayList<icerikBean>();

        try {
            Object parsedJsonObject = parser.parse(JSON);
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parsedJsonObject;
            parsedJsonObject = jsonObject.get("hits");
            jsonObject = (org.json.simple.JSONObject) parsedJsonObject;
            org.json.simple.JSONArray it = (org.json.simple.JSONArray) jsonObject.get("hits");
            Iterator<org.json.simple.JSONObject> iterator = it.iterator();

            while (iterator.hasNext()) {
                try {
                    org.json.simple.JSONObject jsonObj = iterator.next();
                    icerikBean icerikbean = new icerikBean();
                    
                    parsedJsonObject = jsonObj.get("_source");

                    jsonObj = (org.json.simple.JSONObject) parsedJsonObject;

                    if (jsonObj.get("title") != null) {
                        String title = jsonObj.get("title").toString();
                        //icerik beane title ekle
                        icerikbean.setTitle(title);
                    }

                    sonuclarListesi.add(icerikbean);

                } catch (Exception e) {
                    System.err.println("elasticislem::Search 172 " + e.getMessage() + "  " + e.getCause());
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            System.err.println("elasticislem::Search 177" + e.getMessage());
        }
        
        return sonuclarListesi;
    }
}
