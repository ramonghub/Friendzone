//SelectedActivity.java
//Shows selected activity with options
package nl.mprog.friendzone10794913;

//TODO: Alternatief: Knopje +1/-1 attending

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
import android.widget.EditText;
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
    private TextView txtNameTitle;
    private String nameTitle;
    private TextView txtName;
    private String name;
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
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        new RemoteDataTask().execute();

        final Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);

        objectId = getIntent().getStringExtra("objectId");
        System.out.println(objectId);

        // Set textview
        nameTitle = getIntent().getStringExtra("activity_name");
        txtNameTitle = (TextView) findViewById(R.id.nameTitle);
        txtNameTitle.setText(nameTitle);

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
            //get all groups from user
            ParseQuery<ParseObject> groupQuery = new ParseQuery<ParseObject>("Group");
            groupQuery.include("members");
            groupQuery.whereEqualTo("members", ParseUser.getCurrentUser().getUsername());

            //get all activities from groups
            ParseQuery<ParseObject> queryActivity = new ParseQuery<ParseObject>("Activity");
            queryActivity.whereMatchesQuery("groups", groupQuery);
            queryActivity.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));

            //get all options from activity
            ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("Option");
            query3.whereMatchesQuery("activities", queryActivity);

            //display best option
            ParseQuery<ParseObject> bestQuery = ParseQuery.getQuery("Option");
            bestQuery.orderByDescending("votes");
            bestQuery.whereMatchesQuery("activities", queryActivity);
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

            //get all option names for listview
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

    public void onAddButtonClicked(View view) {
        Intent intent = new Intent(this, SelectedActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String new_option = editText.getText().toString();
        intent.putExtra("date", date);
        intent.putExtra("objectId", objectId);

            System.out.println(objectId);
            ParseObject newOption = new ParseObject("Option");
            newOption.put("activities", ParseObject.createWithoutData("Activity", objectId));
            newOption.put("option_name", new_option);
            newOption.put("votes", 0);
            newOption.saveInBackground();

            ParseObject newActivity = new ParseObject("Activity");
            newActivity.put("option_name", new_option);

        startActivity(intent);
        finish();
        }
    }


