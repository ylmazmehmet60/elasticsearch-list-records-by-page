/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.create.index.automatically;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Query {

    private static Query Instance = null;

    public static Query getInstance() {
        if (Instance == null) {
            Instance = new Query();
        }
        return Instance;
    }

    public String GetGeneralQuery(String searchText, String pages) {
        try {

            org.json.simple.JSONObject queryParsed = readAndParseJsonFile("myquery.json");
            JSONObject queryParent = new JSONObject(queryParsed.toString());
            queryParent.getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(0).getJSONObject("multi_match").put("query", searchText);
            queryParent.put("from", pages);
            return queryParent.toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Query class GetGeneralQuery error");
        }
        return null;
    }
    
    private org.json.simple.JSONObject readAndParseJsonFile(String fileName) {
        try {
            StringBuilder sb = new StringBuilder();
            Scanner readfile = new Scanner(new FileReader(getClass().getClassLoader().getResource("queriesJson/" + fileName).getFile()));
            while (readfile.hasNext()) {
                sb.append(readfile.next());
            }
            JSONParser queryParser = new JSONParser();
            return (org.json.simple.JSONObject) queryParser.parse(sb.toString());
        } catch (IOException e) {
            System.err.println("Error while reading file " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Error while parsing json " + e.getMessage());
        }
        return null;
    }

}