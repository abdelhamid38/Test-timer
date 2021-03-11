package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class get_all extends AppCompatActivity {

    ArrayList<Exam> exams=new ArrayList<>();
    String result="it doesn't work";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_all);
        new async().execute("http://192.168.43.42/all.php");

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(R.string.get_All);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.get_all_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.gotomain){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public class async extends AsyncTask<String,String,String> {

        BufferedReader reader;

        @Override
        protected String doInBackground(String... strings) {

            try {
                String URL = strings[0];
                java.net.URL url = null;
                url = new URL(URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();

                Log.d("TAG", "doInBackground:Json " + result);

            } catch (IOException e) {

                Log.e("TAG", "doInBackground:" + e.getLocalizedMessage());

            }
            return null;
        }

        protected void onPostExecute(String s) {

            try {

                JSONArray array = new JSONArray(result);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);

                    exams.add(new Exam(
                            object.getInt("id"),
                            object.getString("name"),
                            object.getInt("h"),
                            object.getInt("m"),
                            object.getString("time"),
                            object.getString("date"),
                            object.getString("teacher")));

                }

                if (exams.isEmpty()) {


                } else {
                    get_all.Myadapter myadapter = new Myadapter(exams);
                    ListView listView = findViewById(R.id.listview);
                    listView.setAdapter(myadapter);
                }
            } catch (JSONException e) {
                Log.e("TAG", "onPostExecute :" + e.getLocalizedMessage());
            }
        }
    }

    public class Myadapter extends BaseAdapter {


        ArrayList<Exam> exams=new ArrayList<Exam>();
        public Myadapter(ArrayList<Exam> exams){
            this.exams=exams;
        }

        @Override
        public int getCount() {
            return exams.size();
        }

        @Override
        public Object getItem(int position) {
            return exams.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View view=inflater.inflate(R.layout.row_items,null);
            TextView Module=view.findViewById(R.id.Module);
            TextView Time=view.findViewById(R.id.Time);
            TextView teacher=view.findViewById(R.id.Teacher);

            Module.setText(exams.get(position).name);
            Time.setText(exams.get(position).time);
            teacher.setText(exams.get(position).teacher);

            return view;
        }
    }

}