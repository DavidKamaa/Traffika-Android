package com.example.traffika.db;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traffika.R;
import com.example.traffika.search.Violation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Worker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    TableLayout tableLayout;
    TableRow row;


    public Worker(Context ctx, TableLayout mtableLayout, TableRow mrow){
        context=ctx;
        tableLayout=mtableLayout;
        row=mrow;

    }

    public String doInBackground(String... params){
        String type=params[0];
        String search_url= "http://traffika.atwebpages.com/search.php";

        if(type.equals("search")){
            try {
                String keyword= params[1];

                URL url = new URL(search_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("keyword", "UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            }catch (MalformedURLException e){
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Status");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result!=null && !result.isEmpty()){
            alertDialog.setMessage(result);
            alertDialog.show();
            try {

                JSONArray jsonArray = new JSONArray(result);
                ArrayList<Violation> violations = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Violation violation = new Violation();
                    violation.setId(jsonObject.getString("violation_id"));
                    violation.setVehicle_plate(jsonObject.getString("vehicle_plate"));
                    violation.setDateTime(jsonObject.getString("date_time"));
                    violation.setViolationType(jsonObject.getString("violation_type"));
                    violations.add(violation);
                }
                updateUI(violations);
            } catch (JSONException e){
                e.printStackTrace();
                alertDialog.setMessage("Error parsing data");
                alertDialog.show();
            }

        }else{
            alertDialog.setMessage("No Result received");
        }
    }
    public void updateUI(ArrayList<Violation> violations) {
        for (Violation violation: violations) {
            TableRow newRow = new TableRow(context);

            // Inflate the violation_item layout XML file to create a View object
            View rowView = LayoutInflater.from(context).inflate(R.layout.violation_item, null, false);

            // Find views within the row and set data
            TextView idTextView = rowView.findViewById(R.id.tvId);
            TextView vehiclePlateTextView = rowView.findViewById(R.id.tvVehiclePlate);
            TextView dateTimeTextView = rowView.findViewById(R.id.tvTime);
            TextView violationTypeTextView = rowView.findViewById(R.id.tvViolationType);

            idTextView.setText(String.valueOf(violation.getId()));
            vehiclePlateTextView.setText(violation.getVehiclePlate());
            dateTimeTextView.setText(violation.getDateTime());
            violationTypeTextView.setText(violation.getViolationType());

            newRow.addView(rowView);

            // Add the row to the table layout
            tableLayout.addView(newRow);
        }


    }
}
