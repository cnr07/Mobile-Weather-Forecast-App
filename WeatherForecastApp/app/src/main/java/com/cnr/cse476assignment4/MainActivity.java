package com.cnr.cse476assignment4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView cityName,descriptionText,temperatureText;
    ImageView iconImageView;
    EditText lookCityName;
    Button lookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName=findViewById(R.id.cityName);
        descriptionText=findViewById(R.id.descriptionText);
        temperatureText=findViewById(R.id.temperatureText);
        iconImageView=findViewById(R.id.iconImageView);
        lookCityName=findViewById(R.id.lookCityName);
        lookButton=findViewById(R.id.lookButton);

        if(checkNetworkConnection()){

            new HTTPAsyncTask().execute("https://api.openweathermap.org/data/2.5/weather?q=istanbul&appid=df6e2cd9174aa9989dbd136cf9acdf89");
        }

    }

    public void LookCurrentWeather(View view) {
        String cityChoice=lookCityName.getText().toString();
        String req="https://api.openweathermap.org/data/2.5/weather?q="+cityChoice+"&appid=df6e2cd9174aa9989dbd136cf9acdf89";
        if(checkNetworkConnection()&&!cityChoice.equals("")){
            new HTTPAsyncTask().execute(req);
        }

    }

    private class HTTPAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                return HTTPGet(urls[0]);
            }catch (IOException e){
                return "Unable to retrieve web page. URL may be invalid.";

            }

        }

        @Override
        protected void onPostExecute(String result){
            /* result: aldığım xml*/
            System.out.println(result);
            String weatherCodesXML=result;
            //parse etcez weatherı
            try {
                JSONObject jsonObject = new JSONObject(weatherCodesXML);
                String icon=jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconNamee="weather"+icon;
                int iconId=getResources().getIdentifier(iconNamee,"drawable","com.cnr.cse476assignment4");
                iconImageView.setImageResource(iconId);
                //System.out.println(icon);
                String cityNamee=jsonObject.getString("name");
                cityName.setText(cityNamee);
                //System.out.println(cityName);
                String airDescription=jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                descriptionText.setText(airDescription);
                //System.out.println(airDescription);
                String maxTempString=jsonObject.getJSONObject("main").get("temp_max").toString();
                double maxTemp=Double.parseDouble(maxTempString);
                maxTemp-=273.15;
                int maxTmp=Integer.parseInt(String.valueOf(Math.round(maxTemp)));
                maxTempString=maxTmp+"";
                //System.out.println(maxTempString);
                String minTempString=jsonObject.getJSONObject("main").get("temp_min").toString();
                double minTemp=Double.parseDouble(minTempString);
                minTemp-=273.15;
                int minTmp=Integer.parseInt(String.valueOf(Math.round(minTemp)));
                minTempString=minTmp+"";
                //System.out.println(minTempString);
                String tempString=jsonObject.getJSONObject("main").get("temp").toString();
                double temp=Double.parseDouble(tempString);
                temp-=273.15;
                int tmp=Integer.parseInt(String.valueOf(Math.round(temp)));
                tempString=tmp+"";
                //System.out.println(tempString);
                String tempTextt=tempString+"°C   H: "+maxTempString+"°C   L: "+minTempString+"°C";
                temperatureText.setText(tempTextt);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private String HTTPGet(String myUrl) throws IOException{
        InputStream inputStream = null;
        String result="";

        URL url = new URL(myUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.connect();

        inputStream=conn.getInputStream();

        if(inputStream!=null){
            result=convertInputStreamToString(inputStream);
        }
        else{
            result="Did not work!";
        }
        return result;

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line="";
        String result = "";
        while((line=bufferedReader.readLine()) != null){
            result+=line;
        }
        inputStream.close();
        return result;
    }


    public boolean checkNetworkConnection(){
        ConnectivityManager connMgr=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if(networkInfo!=null && (isConnected=networkInfo.isConnected())){
            //connected
        } else{
            //not connected
        }
        return isConnected;
    }



}