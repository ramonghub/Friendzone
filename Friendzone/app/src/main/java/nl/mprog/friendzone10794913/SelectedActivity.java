//SelectedActivity.java
//Shows selected activity with options
package nl.mprog.friendzone10794913;

//BELANGRIJKSTE ACTIVITY!!!
//TODO: zorg dat gebruiker aanwezig afwezig kan zetten
//TODO: onthoudt aanwezig afwezig
//TODO: -1 bij selectie andere optie
//TODO: -1 bij uitzetten aanwezigheid
//TODO: zorg dat nieuw optie kan worden toegevoegd
//TODO: link nieuw optie aan activity

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectedActivity extends ActionBarActivity {

    private TextView txtDate;
    private String date;
    private TextView txtDateTitle;
    private String dateTitle;
    private TextView txtBestTitle;
    private String bestTitle;
    private TextView txtBest;
    private String best;
    private TextView txtOptions;
    private String options;
    private List<ParseObject> objectList;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Integer switch_status;
    private boolean switch_on;
    private Switch mySwitch;
    private String oldObject;
    private String newObject;
    private Integer voted = 0;
    private Object selectedItem ;
    private Integer switch_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        new RemoteDataTask().execute();

        final Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);

        // Set textview
        dateTitle = "Date:";
        txtDateTitle = (TextView) findViewById(R.id.dateTitle);
        txtDateTitle.setText(dateTitle);

        // Set textview
        date = getIntent().getStringExtra("date");
        txtDate = (TextView) findViewById(R.id.date);
        txtDate.setText(date);

        // Set textview
        bestTitle = "Best:";
        txtBestTitle = (TextView) findViewById(R.id.bestTitle);
        txtBestTitle.setText(bestTitle);

        // Set textview
        options = "Options:";
        txtOptions = (TextView) findViewById(R.id.options);
        txtOptions.setText(options);

        // Get reference to switch1
        mySwitch = (Switch) findViewById(R.id.switch1);

        // Getting object reference to listview of main.xml
        listView = (ListView) findViewById(R.id.listview);

        // Instantiating array adapter to populate the listView
        // The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectedItem = listView.getItemAtPosition(position);

                ParseQuery<ParseObject> selectedQuery = ParseQuery.getQuery("Option");
                selectedQuery.whereEqualTo("option_name", selectedItem.toString());
                selectedQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                            System.out.println("The getSelected request failed.");
                        } else {
                            newObject = String.valueOf(object.get("option_name"));
                            if (!newObject.equals(oldObject)) {
                                object.increment("votes", 1);
                                object.saveInBackground();
                                System.out.println(newObject);

                                oldObject = newObject;
                                voted = 1;
                                switch_off = 0;

                                System.out.println(newObject);

                                mySwitch.setChecked(true);
                                switch_status = 1;
                            }
                        }
                    }
                });
            }
        });

    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //select relation of current user
            ParseObject current = ParseUser.getCurrentUser();
            ParseRelation relation = current.getRelation("groups");
            ParseQuery query = relation.getQuery();

            ParseQuery innerQuery = relation.getQuery();
            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Activity");
            query2.whereMatchesQuery("groups", innerQuery);
            query2.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));
            ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("Option");
            query3.whereMatchesQuery("activities", query2);

            ParseQuery<ParseObject> bestQuery = ParseQuery.getQuery("Option");
            bestQuery.orderByDescending("votes");
            bestQuery.whereMatchesQuery("activities", query2);
            bestQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        System.out.println("The getFirst request failed.");
                    } else {
                        System.out.println("Retrieved the object.");
                        best = object.get("option_name").toString();
                        txtBest = (TextView) findViewById(R.id.best);
                        txtBest.setText(best);
                    }
                }
            });

            try {
                objectList = query3.find();
            } catch (ParseException error) {
                Log.e("Error", error.getMessage());
                error.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for (ParseObject query : objectList) {
                list.add((String) query.get("option_name"));
                adapter.notifyDataSetChanged();
            }
        }
    }

    //	  when game is resumed
    @Override
    public void onResume() {
//		  make shared preferences editor
        Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        int on_pause_check = settings.getInt("on_pause_check", 0);

//		  if data is saved restore preferences
        if (on_pause_check == 1) {

            listView.setItemChecked(settings.getInt("resume_selected", 0), true);

            if (settings.getInt("resume_attending", 0) == 1) {
                mySwitch = (Switch) findViewById(R.id.switch1);
                mySwitch.setChecked(true);
            }

        }
        super.onResume();
    }

    //	  when game is paused/stopped
    @Override
    public void onPause() {
//		  make shared preferences editor
        Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

//        remember selected option
        editor.putInt("resume_selected", listView.getCheckedItemPosition());

//        remember switch switch_status
//        editor.putInt("resume_attending", switch_status);

//	      enable onResume
        editor.putInt("on_pause_check", 1);

//	      commit the edits!
        editor.commit();

        super.onPause();
    }


    public void onSwitchClicked(View view) {
        // Is the toggle on?
        switch_on = ((Switch) view).isChecked();

        if (switch_on) {
            // Enable vibrate
            switch_status = 1;
            System.out.println(switch_status);

            if (voted > 0 && switch_off == 1){
                ParseQuery<ParseObject> switchOnQuery = ParseQuery.getQuery("Option");
                switchOnQuery.whereEqualTo("option_name", selectedItem.toString());
                switchOnQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        object.increment("votes", 1);
                        System.out.println(object.get("votes").toString());
                        switch_off = 0;
                    }
                });
            }

        } else {
            switch_status = 0;
            System.out.println(switch_status);

            if (voted > 0 && switch_off != 1){
                ParseQuery<ParseObject> switchQuery = ParseQuery.getQuery("Option");
                switchQuery.whereEqualTo("option_name", selectedItem.toString());
                switchQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        object.increment("votes", -1);
                        System.out.println(object.get("votes").toString());
                        switch_off = 1;
                    }
                });
            }

        }

    }

//    public void onAddButtonClicked(View view) {
//            if (voted > 0 && switch_off != 1){
//                ParseQuery<ParseObject> switchQuery = ParseQuery.getQuery("Option");
//                switchQuery.whereEqualTo("option_name", selectedItem.toString());
//                switchQuery.getFirstInBackground(new GetCallback<ParseObject>() {
//                    public void done(ParseObject object, ParseException e) {
//                        object.increment("votes", -1);
//                        System.out.println(object.get("votes").toString());
//                        switch_off = 1;
//                    }
//                });
//            }
//
//        }

    }


