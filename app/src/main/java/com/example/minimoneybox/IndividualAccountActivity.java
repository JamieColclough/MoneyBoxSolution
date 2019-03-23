package com.example.minimoneybox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class IndividualAccountActivity extends AppCompatActivity {
    TextView accountName;
    TextView planVal;
    TextView moneyBox;
    int moneyBoxTotal;
    int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_account);
        accountName = findViewById(R.id.account_name);
        planVal = findViewById(R.id.plan_val);
        moneyBox = findViewById(R.id.moneybox_total);

        Intent data = getIntent();
        productId = data.getIntExtra("productId", 0);
        accountName.setText(data.getStringExtra("accountName"));
        planVal.setText("Plan Value: £" + String.valueOf(data.getIntExtra("planVal", 0)));
        moneyBoxTotal = data.getIntExtra("moneyBox", 0);
        moneyBox.setText("Moneybox: £" + String.valueOf(moneyBoxTotal));
        (findViewById(R.id.add_money)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentAPI api = new PaymentAPI(productId);
                api.execute();

            }
        });
    }


        /**
         * AsyncTask inner class that accesses the moneybox API to increment a moneybox total by £10
         */
        class PaymentAPI extends AsyncTask<String, Void, String> {

            private static final String ADD_URL =
                    "https://api-test01.moneyboxapp.com/oneoffpayments"; //URL for the api

            private final int productId;



            /**
             * Constructor for the API
             */
            public PaymentAPI(int pId) {
                this.productId = pId;
            }


            /**
             * Static method to obtain a string from performing a http call to the openWeatherMap API server
             * @return string that is not null if the call was successful
             */
            String oneOffPayment(){
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                String bearerToken = sp.getString("BearerToken", "");
                String authString = "";
                if (!bearerToken.isEmpty()){
                    authString = "Bearer " + bearerToken;
                }
                else{}
                HttpsURLConnection connection;
                try {
                    URL connectionUrl = new URL(ADD_URL);
                    connection =
                            (HttpsURLConnection) connectionUrl.openConnection();//Tries to open a connection
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("AppId", "3a97b932a9d449c981b595");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("appVersion", "5.10.0");
                    connection.setRequestProperty("apiVersion", "3.0.0");
                    connection.setRequestProperty("Authorization", authString);

                    String jsonObj ="{\n" +
                            "  \"Amount\": 10,\n" +
                            "  \"InvestorProductId\": "+String.valueOf(productId)+"\n" +
                            "}";
                    OutputStream wr = connection.getOutputStream();
                    wr.write(jsonObj.getBytes());
                    wr.close();

                    InputStream read = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(read));//Gets input stream from the connection

                    StringBuffer json = new StringBuffer(1024);
                    String tmp;
                    while((tmp=reader.readLine())!=null)
                        json.append(tmp).append("\n");//Adds up the string file
                    reader.close();

                    return json.toString();
                }
                catch(IOException e ){return null;}

            }




            @Override
            protected String doInBackground(String... params) {

                try {
                    return oneOffPayment();
                } catch (Exception e) {
                    return null;
                }
            }


            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
                try {
                    if(data != null){
                        Toast.makeText(getApplicationContext(),"Payment Successful!", Toast.LENGTH_LONG).show();
                        moneyBoxTotal += 10;
                        moneyBox.setText("Moneybox: £" + String.valueOf(moneyBoxTotal));


                    }
                    //In the following 2 cases some error has occurred
                    else{
                        Toast.makeText(getApplicationContext(), "Payment Error: Try again later.", Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception e){}
            }
        }


    }

