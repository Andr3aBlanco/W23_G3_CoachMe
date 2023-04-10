package com.bawp.coachme.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaceApi {
    public ArrayList<String> autoComplete(String input) {


        ArrayList<String> arrayList=new ArrayList();
        HttpURLConnection connection=null;
        StringBuilder jsonResult=new StringBuilder();
        try {
            StringBuilder sb=new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input="+input);
            sb.append("&key=AIzaSyAxYlO9nqHFqxAbgtBtbBcZHPX3JcyJ4c4");
            URL url=new URL(sb.toString());

            connection=(HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader=new InputStreamReader(connection.getInputStream());
            int read;
            char[] buff=new char[1024];
            while ((read=inputStreamReader.read(buff)) !=-1){
                jsonResult.append(buff,  0, read);
            }}
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
            if(connection!=null){
                connection.disconnect();
            }
           }
              try {
                  JSONObject jsonobject=new JSONObject(jsonResult.toString());

                  JSONArray prediction = jsonobject.getJSONArray("prediction");
                  for (int i=0;i<prediction.length();i++){
                      arrayList.add(prediction.getJSONObject(i).getString("discription"));
                  }

             }catch (JSONException e) {
                  e.printStackTrace();
              }
              return arrayList;
    }
}
