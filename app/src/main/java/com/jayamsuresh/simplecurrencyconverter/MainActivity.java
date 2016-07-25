package com.jayamsuresh.simplecurrencyconverter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
private String strJSONBuffer;
private ArrayAdapter<String> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FetchCurrencyTask fctask = new FetchCurrencyTask();
        fctask.execute();
        //Get the spinner ID
        Spinner spinner = (Spinner) findViewById(R.id.fromCurrency);
        // Update the ArrayAdapter with new data from the server

        spinner.setAdapter(spinnerArrayAdapter);
        populateSpinner(strJSONBuffer);
    }

    public void populateSpinner(String jsonString){
        // convert JSON string into JSON data
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            // build an array so we can populate the spinner values
            ArrayList<String> spinnerArray = new ArrayList<String>();

            // Loop through all the currency code

            // Use iterator to iterate through the keys
            Iterator<String> iter = jsonObject.keys();
            while(iter.hasNext()){
                String key = iter.next();
                try{
                    Object value = jsonObject.get(key);
                    String valueStr = value.toString();

                    //Populate the spinner array
                    spinnerArray.add(""+key+":"+valueStr);
                    Log.i("key:",""+ key);
                    Log.i("value:",""+ valueStr);
                }
                catch(JSONException e){
                    Log.e("Error","Could not iterate through the JSON keys");
                }
            } // end While block
            // The spinner would now have values added


            //Apply the array to the spinner
            spinnerArrayAdapter = new ArrayAdapter<String>(
                    MainActivity.this, android.R.layout.simple_spinner_item, spinnerArray);

            spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                  Spinner spinner = (Spinner) findViewById(R.id.fromCurrency);
                  spinner.setAdapter(spinnerArrayAdapter);


        } // End Try block
        catch(Throwable t){
            Log.e("Error",t.toString());
        } // End catch block



    }

    public class FetchCurrencyTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchCurrencyTask.class.getSimpleName();
        // declaring some global variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // This stores the JSON string retrieved from the currency
        String JSONStr = null;

        @Override
        protected Void doInBackground(Void... params){


            try {
                // get the URL and connect to Open Exchange Rates.org
                URL url = new URL("https://openexchangerates.org/api/currencies.json?app_id=12211d32f67d46e783861be4be3efd84");

                // Create the request to Open Exchange Rate and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.i("Info","No Input Stream to retrieve");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.i("Info","Stream was empty");
                    return null;
                }
                JSONStr = buffer.toString();
                strJSONBuffer = JSONStr;
                Log.i("Retrieved Buffer is",""+JSONStr);



            }catch (IOException e){

                Log.e(LOG_TAG, "Error ", e );
                return null;
            }
            finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }


    }
}
