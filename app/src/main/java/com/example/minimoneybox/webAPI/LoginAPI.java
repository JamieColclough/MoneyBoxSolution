package com.example.minimoneybox.webAPI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.minimoneybox.AccountsActivity;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;


/**
 * AsyncTask class that accesses the moneybox API to sign in
 */
public class LoginAPI extends AsyncTask<String, Void, String> {

    private static final String LOGIN_URL =
            "https://api-test01.moneyboxapp.com/users/login"; //URL for the api


    private Context context; //The application context

    /**
     * Constructor for the API
     * @param context the application context
     */
    public LoginAPI(Context context) {
        this.context = context.getApplicationContext();
    }


    /**
     * Method to login and obtain the bearer token to use the MoneyBox API
     * @param email The email address
     * @param password The password
     * @return The bearer token as a string if successful, otherwise null
     */
    public String loginConnect(String email, String password){
        HttpsURLConnection connection;
        try {
            URL connectionUrl = new URL(LOGIN_URL);
            connection =
                    (HttpsURLConnection) connectionUrl.openConnection();//Tries to open a connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("AppId", "3a97b932a9d449c981b595");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("appVersion", "5.10.0");
            connection.setRequestProperty("apiVersion", "3.0.0");

            String jsonObj ="{\n" +
                    "  \"Email\": \""+email+"\",\n" +
                    "  \"Password\": \""+password+"\",\n" +
                    "  \"Idfa\": \"ANYTHING\"\n" +
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

            JSONObject data = new JSONObject(json.toString());//Converts the string to a JSON Object
            return data.getJSONObject("Session").getString("BearerToken");

        }
        catch(IOException e ){return null;}
        catch(JSONException e){return null;}

    }




    @Override
    protected String doInBackground(String... params) {

        try {
            return loginConnect(params[0], params[1]);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    protected void onPostExecute(String token) {
        super.onPostExecute(token);
        try {
            if(token != null){
                //Case that json has been returned- can iterate over the details
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("BearerToken",token);
                editor.apply();
                Intent intent = new Intent(context, AccountsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);



            }
            //In the following 2 cases some error has occurred
            else{
                Toast.makeText(context, "Error signing in: try again later", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e){}
    }
}
