package com.mycompany.sameer.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.Math.floor;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    TextView tempTextView;
    TextView windTextView;
    TextView pressureTextView;
    TextView weatherTextView;


    public void weatherInfo(View view){

        try {
            String cityName = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=1de6c87e01f8c711ed600a9a18bdc015");

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);
        }catch(Exception e){
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Oops something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(inputStream);

                int data = streamReader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = streamReader.read();
                }

                return result;

            }catch (Exception e){
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Oops something went wrong", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString( "weather");
                String temp = jsonObject.getString("main");
                String wind = jsonObject.getString("wind");

                Log.i("Weather Info", weatherInfo);
                Log.i("Temperature", temp);
                Log.i("Wind", wind);

                JSONArray weatherArr = new JSONArray(weatherInfo);

                String weatherMessage = "";
                String tempMessage = "";
                String windMessage = "";
                String pressAndHumidMessage = "";

                for(int i=0; i < weatherArr.length(); i++){
                    JSONObject jsonPartWeather = weatherArr.getJSONObject(i);

                    String main =  jsonPartWeather.getString( "main");
                    String description = jsonPartWeather.getString( "description");

                    if(main != "" && description != "") {
                        weatherMessage = main + " : " + description + "\r\n";
                    }

                }

                JSONObject jsonPartTemp = new JSONObject(temp);

                String currentTemp = jsonPartTemp.getString( "temp");
                String feelsLike = jsonPartTemp.getString( "feels_like");
                String tempMin = jsonPartTemp.getString("temp_min");
                String tempMax = jsonPartTemp.getString("temp_max");
                String pressure = jsonPartTemp.getString("pressure");
                String humidity = jsonPartTemp.getString("humidity");
                double pressureInHg = round(Integer.parseInt(pressure) * 0.02953);

                if(currentTemp != "" || feelsLike != "" || tempMin != "" || tempMax != "" || pressure != "" || humidity != "") {
                    tempMessage = "Current Temp : " + currentTemp + " °C" + "\r\n" + "Feels Like : " + feelsLike + " °C" + "\r\n" + "Min Temp : " + tempMin + " °C" + "\r\n" + "Max Temp : " + tempMax + " °C" + "\r\n";
                    pressAndHumidMessage = "Pressure : " + pressureInHg + " inHg" + "\r\n" + "Humidity : " + humidity + '%' + "\r\n";
                }

                JSONObject jsonPartWind = new JSONObject(wind);

                String speed =  jsonPartWind.getString( "speed");

                if(speed != "" ) {
                    windMessage = "Wind : " + speed + " m/s" + "\r\n";
                }

                if(weatherMessage != "" || tempMessage != "" || pressAndHumidMessage != "" || windMessage != ""){

                    weatherTextView.setText(weatherMessage);
                    tempTextView.setText(tempMessage);
                    pressureTextView.setText(pressAndHumidMessage);
                    windTextView.setText(windMessage);

                }else{
                    Toast.makeText(getApplicationContext(), "Oops something went wrong", Toast.LENGTH_SHORT).show();
                    tempTextView.setText("null");
                    pressureTextView.setText("null");
                    windTextView.setText("null");
                    weatherTextView.setText("null");
                }
            } catch (Exception e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Oops something went wrong", Toast.LENGTH_SHORT).show();
                tempTextView.setText("null");
                pressureTextView.setText("null");
                windTextView.setText("null");
                weatherTextView.setText("null");
            }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText =findViewById(R.id.cityEditText);
        tempTextView = findViewById(R.id.tempTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        windTextView = findViewById(R.id.windTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
    }
}
