package com.example.findweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetDataFromInternet.AsyncResponse{

    private static final String TAG = "MainActivity";

    private Button searchButton;
    private EditText searchField;
    private TextView cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchField = findViewById(R.id.searchField);
        cityName = findViewById(R.id.cityName);

        searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

            //URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Gatchina&appid=0ae569f0470202aadc5233bbc707df50");

        URL url = buildURL(searchField.getText().toString());

        cityName.setText(searchField.getText().toString());

        new GetDataFromInternet(this).execute(url);

    }

    private URL buildURL(String city) {
        String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
        String PARAM_CITY = "q";
        String PARAM_APPID = "appid";
        String appid_value = "0ae569f0470202aadc5233bbc707df50";

        Uri biltUri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(PARAM_CITY, city).appendQueryParameter(PARAM_APPID, appid_value).build();

        URL url = null;
        try {
            url = new URL(biltUri.toString());
        //} catch (MalformedURLException e) {
            //e.printStackTrace();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.d(TAG, "buildURL: "+url);
        return url;
    }

    public void processFinish(String output){
        Log.d(TAG, "processFinish:" +output);
        try {
            JSONObject resultJSON = new JSONObject(output);
            JSONObject weather = resultJSON.getJSONObject("main");
            JSONObject sys = resultJSON.getJSONObject("sys");

            TextView temp = findViewById(R.id.tempValue);
            String temp_K = weather.getString("temp");
            float temp_C = Float.parseFloat(temp_K);
            temp_C -= (float) 273.15;
            String a[] = String.valueOf(temp_C).split("[.]");
            //String temp_C_String = Float.toString((int)temp_C);
            temp.setText(a[0] + " °C");

            TextView pressure = findViewById(R.id.pressureValue);
            pressure.setText(weather.getString("pressure") + " гПа");

            TextView sunrise = findViewById(R.id.dawnTime);
            String timeSunrise = sys.getString("sunrise");
            Locale myLocale = new Locale("ru","RU");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", myLocale);

            String dateString = formatter.format(new Date(Long.parseLong(timeSunrise)*1000 + (60*60*1000)*3));
            sunrise.setText(dateString);

            TextView sunset = findViewById(R.id.duskTime);
            String timeSunset = sys.getString("sunset");
            //Locale myLocale = new Locale("ru","RU");
            //SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", myLocale);

            dateString = formatter.format(new Date(Long.parseLong(timeSunset)*1000 + (60*60*1000)*3));
            sunset.setText(dateString);

        }catch (JSONException|NullPointerException e){
            e.printStackTrace();
        }
    }
}