package com.example.minimoneybox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class AccountsActivity extends AppCompatActivity {

    TextView nameView;
    LinearLayout products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        nameView = findViewById(R.id.name);
        products = findViewById(R.id.products);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String name = sp.getString("name", "");

        if (!name.isEmpty()){
            String helloString = "Hello " + name + "!";
            nameView.setText(helloString);
        }
        else{
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        InvestorProductsAPI api = new InvestorProductsAPI();
        api.execute();
    }

    /**
     * AsyncTask inner class that accesses the moneybox to obtain the investor products
     */
    public class InvestorProductsAPI extends AsyncTask<String, Void, JSONArray> {

        private static final String Invest_URL =
                "https://api-test01.moneyboxapp.com/investorproducts"; //URL for the api



        /**
         * Constructor for the API
         */
        public InvestorProductsAPI() {
        }


        /**
         * Static method to obtain a JSON object containing the relevant products from the MoneyBox API
         * @return JSON Object containing all of the data
         */
        public JSONArray obtainProducts(){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String bearerToken = sp.getString("BearerToken", "");
            String authString = "";
            if (!bearerToken.isEmpty()){
                authString = "Bearer " + bearerToken;
            }
            else{}
            HttpsURLConnection connection;
            try {
                URL connectionUrl = new URL(Invest_URL);
                connection =
                        (HttpsURLConnection) connectionUrl.openConnection();//Tries to open a connection
                connection.setRequestProperty("AppId", "3a97b932a9d449c981b595");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("appVersion", "5.10.0");
                connection.setRequestProperty("apiVersion", "3.0.0");
                connection.setRequestProperty("Authorization", authString);



                InputStream read = connection.getInputStream();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(read));//Gets input stream from the connection

                StringBuffer json = new StringBuffer(1024);
                String tmp;
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");//Adds up the string file
                reader.close();

                JSONArray data = new JSONObject(json.toString()).getJSONArray("ProductResponses");//Converts the string to a JSON Object
                return data;
            }
            catch(IOException e ){return null;}
            catch(JSONException e){return null;}

        }




        @Override
        protected JSONArray doInBackground(String... params) {

            try {
                return obtainProducts();
            } catch (Exception e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONArray data) {
            super.onPostExecute(data);
            try {
                if(data != null){
                    LinearLayout product;
                    JSONObject accountInfo;
                    String accountName;
                    int planVal;
                    int moneyBox;
                    int productId;
                    //Case that json has been returned- can iterate over the details
                    for(int i = 0; i < products.getChildCount(); i++) {
                        product = ((LinearLayout) products.getChildAt(i));
                        accountInfo = data.getJSONObject(i);
                        accountName = accountInfo.getJSONObject("Product").getString("FriendlyName");
                        planVal = accountInfo.getInt("PlanValue");
                        moneyBox = accountInfo.getInt("Moneybox");
                        productId = accountInfo.getInt("Id");


                        ((TextView)product.getChildAt(0)).setText(accountName);
                        ((TextView)product.getChildAt(1)).setText("Plan Value: £" + String.valueOf(planVal));
                        ((TextView)product.getChildAt(2)).setText("Moneybox: £" + String.valueOf(moneyBox));

                        product.setOnClickListener(new IndividualAccountClickListener(accountName, planVal, moneyBox, productId));

                    }




                }
                //In the following 2 cases some error has occurred
                else{
                    Toast.makeText(getApplicationContext(), "Please log in again", Toast.LENGTH_LONG).show();
                    finish(); //finishes activity and goes back to login screen
                }
            }
            catch(Exception e){}
        }
    }

    /**
     * OnClick listener designed to facilitate the onclick event that opens up a particular account in the next screen
     */
    public class IndividualAccountClickListener implements View.OnClickListener
    {

        String accountName;
        int planVal;
        int moneyBox;
        int productId;


        /**
         * Constructor for the listener
         * @param accountName
         * @param planVal
         * @param moneyBox
         * @param productId
         */
        IndividualAccountClickListener(String accountName, int planVal, int moneyBox, int productId) {
            this.accountName = accountName;
            this.planVal = planVal;
            this.moneyBox = moneyBox;
            this.productId = productId;
        }

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(getApplicationContext(), IndividualAccountActivity.class);
            intent.putExtra("accountName",accountName);
            intent.putExtra("planVal",planVal);
            intent.putExtra("moneyBox",moneyBox);
            intent.putExtra("productId",productId);
            startActivity(intent);
        }

    }

}
