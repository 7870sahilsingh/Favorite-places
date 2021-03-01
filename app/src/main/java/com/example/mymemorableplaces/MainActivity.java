package com.example.mymemorableplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DefaultDatabaseErrorHandler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static  ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    TextView textView;
    SharedPreferences sharedPreferences;

    public void setLanguage(String language){
        sharedPreferences.edit().putString("language",language).apply();
        textView.setText(language);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.english:
                setLanguage("English");
                return true;
            case R.id.spanish:
                setLanguage("spanish");
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView favPlacesListView = (ListView)findViewById(R.id.favPlacesListView);

        textView=(TextView)findViewById(R.id.textView);
        sharedPreferences = this.getSharedPreferences("com.example.mymemorableplaces", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language","");

        if (language ==""){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Choose a Language")
                    .setMessage("Which language would you like?")
                    .setPositiveButton("English", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this,"English is your preferred language",Toast.LENGTH_SHORT).show();
                            setLanguage("English");
                        }
                    })
                    .setNegativeButton("Spanish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this,"Spanish is your preferred language",Toast.LENGTH_SHORT).show();
                            setLanguage("Spanish");
                        }
                    })
                    .show();
        }
        else
        {textView.setText(language);}

        //SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.mymemorableplaces", Context.MODE_PRIVATE);
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();


        try {

            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes =    (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(places.size()>0 && latitudes.size()>0 && longitudes.size()>0){
            if(places.size()==latitudes.size() && latitudes.size()==longitudes.size()){
                for(int i=0;i<latitudes.size();i++){
                locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));}
            }
        }
        else
        {places.add("Add a new place..");
        locations.add(new LatLng(0,0));}

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);
        favPlacesListView.setAdapter(arrayAdapter);
        favPlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placeNumber",position);

                startActivity(intent);
            }
        });
    }
}
