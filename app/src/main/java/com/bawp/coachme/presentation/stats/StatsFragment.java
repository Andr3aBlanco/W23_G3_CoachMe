package com.bawp.coachme.presentation.stats;

import android.graphics.Color;
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
import android.widget.TextView;
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
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

/**
 * Class StatsFragment.java
 *
 * This
 * **/

public class StatsFragment extends Fragment {

    WebView pieChartYear;
    WebView barChartTotal;
    TextView nothingHereText;
    TextView txttotalHours;
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
        nothingHereText = view.findViewById(R.id.txtNoHistoryText);
        txttotalHours = view.findViewById(R.id.txtTotalHours);

        // put the color
        pieChartYear.setBackgroundColor(Color.parseColor("#fcfffd"));
        barChartTotal.setBackgroundColor(Color.parseColor("#fcfffd"));

        WebSettings webSettings = pieChartYear.getSettings();
        WebSettings barSettings = barChartTotal.getSettings();


        dbHelper = new DBHelper(getContext());
        System.out.println("This is current ID " + UserSingleton.getInstance().getUserId());
        // fill the array list
        pieChartYearData = dbHelper.getServiceTypePercentagesLastYear(UserSingleton.getInstance().getUserId());
        barChartYearData = dbHelper.getAppointmentsCountByMonth(UserSingleton.getInstance().getUserId());
        int totalHours = dbHelper.getTotalWorkoutHours(UserSingleton.getInstance().getUserId());

        if(totalHours > 0){

            String textToDisplay = "You have trained " + totalHours + " h. Keep Going!";
            txttotalHours.setText(textToDisplay);

        }

        if(pieChartYearData.size() == 0 && barChartYearData.size() == 0){
            nothingHereText.setVisibility(View.VISIBLE);

        }

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
            int monthNumber = Integer.parseInt(data[0]);
            String monthName = new DateFormatSymbols().getShortMonths()[monthNumber-1]; // Get the short month name from the month number
            row.add('"'+monthName + '"'); // month
            row.add(Double.parseDouble(data[1])); // count
            barChartData.add(row); // add the row to the chart data
        }


        // Options for pieChart
        String colorsPie = "['#5E60CE', '#48BFE3', '#64DFDF', '#7400B8', '#80FFDB', '#0099C6', '#6930C3', '#72EFDD', '#5390D9', '#64DFDF']";
        String titlePie = "'Distribution of services'";
        String pieOptions = "{'title':"+ titlePie +", is3D: true, 'width':400, " +
                "'height':300," +
                "titleTextStyle: {color: '#613dc1', fontSize: 16, bold: true}," +
                " 'colors' :"+ colorsPie + "," +
                "backgroundColor: '#fcfffd', " +
                "chartArea:{left:10,top:20 ,width:'100%',height:'90%'}," +
                "legend:{alignment: 'center', position:'right', textStyle: {color: '#613dc1', fontSize: 16}}," +
                "pieSliceTextStyle:{color: 'white', fontSize: 20}" +
                "}";
//        String barOptions = "{'title':'Hours per month', 'width':400, " +
//                "'height':300, 'colors' :"+ colorsPie + "," +
//                "backgroundColor: '#fcfffd', " +
//                "chartArea:{left:10,top:0,width:'100%',height:'100%'}," +
//                "legend:{textStyle: {color: 'white', fontSize: 16}}" +
//                "}";

        String barOptions = "{'title': 'Hours per Month', " +
                "titleTextStyle: {color: '#613dc1', fontSize: 16, bold: true, align: 'center'}, " +
                "axisTitlesPosition: 'in',"+
                "backgroundColor:{ fill: '#fcfffd'}, " +
                "chartArea: { backgroundColor: '#fcfffd', width: '100%', height: '65%', left : 20}," +
                "colors: ['#5E60CE', '#48BFE3', '#64DFDF', '#7400B8', '#80FFDB', '#0099C6', '#6930C3', '#72EFDD', '#5390D9', '#64DFDF'], " +
                "hAxis: {textStyle: {color: '#613dc1', fontSize: 16}, slantedText: true, slantedTextAngle: 90}, " +
                "vAxis:{title: 'Hours', titleTextStyle: {color: '#613dc1', fontSize: 16}, textStyle: {color: '#613dc1', fontSize: 12}, format:'#'} "+
                "}";

        pieChartYear.getSettings().setJavaScriptEnabled(true);
//        pieChartYear.loadDataWithBaseURL("file:///android_asset/", "<html><head><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">google.charts.load('current', {'packages':['corechart']});google.charts.setOnLoadCallback(drawChart);function drawChart() {var data = new google.visualization.DataTable();data.addColumn('string', 'Service Type');data.addColumn('number', 'Percentage');data.addRows(" + chartData + ");var options = {'title':'Distribution By Service Type', is3D: true, 'width':400, 'height':300, 'colors' :"+ colorsPie +"};var chart = new google.visualization.PieChart(document.getElementById('chart_div'));chart.draw(data, options);}</script></head><body><div id=\"chart_div\"></div></body></html>", "text/html", "UTF-8", null);
        pieChartYear.loadDataWithBaseURL("file:///android_asset/", "<html><head><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">google.charts.load('current', {'packages':['corechart']});google.charts.setOnLoadCallback(drawChart);function drawChart() {var data = new google.visualization.DataTable();data.addColumn('string', 'Service Type');data.addColumn('number', 'Percentage');data.addRows(" + chartData + ");var options = "+ pieOptions +";var chart = new google.visualization.PieChart(document.getElementById('chart_div'));chart.draw(data, options);}</script></head><body><div id=\"chart_div\"></div></body></html>", "text/html", "UTF-8", null);


        barSettings.setJavaScriptEnabled(true);
        barChartTotal.loadDataWithBaseURL("file:///android_asset/", "<html><head><script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">google.charts.load('current', {'packages':['corechart']});google.charts.setOnLoadCallback(drawChart);function drawChart() {var data = new google.visualization.DataTable();data.addColumn('string', 'Month');data.addColumn('number', 'Hours');data.addRows(" + barChartData + ");var options = "+ barOptions +";var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));chart.draw(data, options);}</script></head><body><div id=\"chart_div\"></div></body></html>", "text/html", "UTF-8", null);





        return view;
    }



}
