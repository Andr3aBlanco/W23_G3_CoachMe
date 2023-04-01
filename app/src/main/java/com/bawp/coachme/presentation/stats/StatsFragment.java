package com.bawp.coachme.presentation.stats;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bawp.coachme.R;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;


public class StatsFragment extends Fragment {

    WebView pieChartYear;
    WebView barChartTotal;
    ArrayList<String[]> pieChartYearData = new ArrayList<>();
    ArrayList<String[]> barChartYearData = new ArrayList<>();
    DBHelper dbHelper;
    JSONArray jsonArray = new JSONArray();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_stats, container, false);
        pieChartYear = view.findViewById(R.id.chartWebView);
        barChartTotal = view.findViewById(R.id.barChart);

        WebSettings webSettings = pieChartYear.getSettings();
        WebSettings barSettings = barChartTotal.getSettings();


        dbHelper = new DBHelper(getContext());
        System.out.println("This is current ID " + UserSingleton.getInstance().getUserId());
        // fill the array list
        pieChartYearData = dbHelper.getServiceTypePercentagesLastYear(UserSingleton.getInstance().getUserId());
        barChartYearData = dbHelper.getAppointmentsCountByMonth(UserSingleton.getInstance().getUserId());
        
        // loop trhough the data 
        for (String[] data : pieChartYearData) {
            String serviceType = '"' + data[0] + '"';
            String percentage = data[1];
            System.out.println("Service Type: " + serviceType + ", Percentage: " + percentage);
            try {
                jsonArray.put(new JSONObject().put("serviceType", serviceType).put("percentage", percentage));
            } catch (JSONException e) {
                Toast.makeText(getContext(), "There is no data for the previous year", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);

            }
        }

        // loop trhough the data
        for (String[] data : barChartYearData) {
            String serviceType = '"' + data[0] + '"';
            String percentage = data[1];
            System.out.println("Service Type: " + serviceType + ", Percentage: " + percentage);
        }

        ArrayList<ArrayList<Object>> chartData = new ArrayList<>();

        for (String[] data : pieChartYearData) {
            ArrayList<Object> row = new ArrayList<>();
            row.add('"'+data[0] + '"'); // add the service type (a string)
            row.add(Double.parseDouble(data[1])); // add the percentage (a number)
            chartData.add(row); // add the row to the chart data
        }


        ArrayList<ArrayList<Object>> barChartData = new ArrayList<>();

        for (String[] data : barChartYearData) {
            ArrayList<Object> row = new ArrayList<>();
            row.add('"'+data[0] + '"'); // add the service type (a string)
            row.add(Double.parseDouble(data[1])); // add the percentage (a number)
            barChartData.add(row); // add the row to the chart data
        }


        // Options for pieChart
      String colorsPie = "['#5E60CE', '#48BFE3', '#64DFDF', '#7400B8', '#80FFDB', '#0099C6', '#6930C3', '#72EFDD', '#5390D9', '#64DFDF']";

        pieChartYear.getSettings().setJavaScriptEnabled(true);
        pieChartYear.loadDataWithBaseURL("file:///android_asset/", "<html><head><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">google.charts.load('current', {'packages':['corechart']});google.charts.setOnLoadCallback(drawChart);function drawChart() {var data = new google.visualization.DataTable();data.addColumn('string', 'Service Type');data.addColumn('number', 'Percentage');data.addRows(" + chartData + ");var options = {'title':'Distribution By Service Type', 'width':400, 'height':300, 'colors' :"+ colorsPie +"};var chart = new google.visualization.PieChart(document.getElementById('chart_div'));chart.draw(data, options);}</script></head><body><div id=\"chart_div\"></div></body></html>", "text/html", "UTF-8", null);


        barSettings.setJavaScriptEnabled(true);
        barChartTotal.loadDataWithBaseURL("file:///android_asset/", "<html><head><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">google.charts.load('current', {'packages':['corechart']});google.charts.setOnLoadCallback(drawChart);function drawChart() {var data = new google.visualization.DataTable();data.addColumn('string', 'Service Type');data.addColumn('number', 'Percentage');data.addRows(" + barChartData + ");var options = {'title':'Pie Chart of Service Types by Percentage', 'width':400, 'height':300};var chart = new google.visualization.LineChart(document.getElementById('chart_div'));chart.draw(data, options);}</script></head><body><div id=\"chart_div\"></div></body></html>", "text/html", "UTF-8", null);






        return view;
    }



}
