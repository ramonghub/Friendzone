//SelectedActivity.java
//Shows selected activity with options
package nl.mprog.friendzone10794913;

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
import com.parse.codec.binary.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private List<ParseObject> objectList2;
    private List<ParseObject> objectList3;
    private ArrayList<String> list;
    private ArrayList<String> list2;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Integer switch_status;
    private boolean switch_on;
    private Switch mySwitch;
    private String oldObject;
    private String newObject;
    private Integer voted = 0;
    private Integer switch_off;
    private String objectId;
    private String ObjectId;
    private Object selectedItem;
    Integer added = 0;
    private String attenders;

    private TextView txtAttendants;
    private String attendants;

    private TextView txtAttendantsTitle;
    private String attendantsTitle;

    private Integer counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        new RemoteDataTask().execute();

        final Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);

        objectId = getIntent().getStringExtra("objectId");

        // Set textview
        nameTitle = getIntent().getStringExtra("activity_name");
        txtNameTitle = (TextView) findViewById(R.id.nameTitle);
        txtNameTitle.setText(nameTitle);

        // Set textview
        attendantsTitle = "Attendants:";
        txtAttendantsTitle = (TextView) findViewById(R.id.attendantsTitle);
        txtAttendantsTitle.setText(attendantsTitle);

        // Set textview
        dateTitle = "Date:";
        txtDateTitle = (TextView) findViewById(R.id.dateTitle);
        txtDateTitle.setText(dateTitle);

        // Set textview
        date = getIntent().getStringExtra("date");
        txtDate = (TextView) findViewById(R.id.date);
        txtDate.setText(date);

        // Set textview
        bestTitle = "Best scoring:";
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
        list2 = new ArrayList<String>();
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
                            newObject = String.valueOf(object.getObjectId());
                            if (!newObject.equals(oldObject)) {
//                                ObjectId = String.valueOf(object.getObjectId());
                                object.increment("votes", 1);
                                object.saveInBackground();

                                if (voted == 1) {
                                    ParseQuery<ParseObject> queryMin = ParseQuery.getQuery("Option");
                                    queryMin.getInBackground(oldObject, new GetCallback<ParseObject>() {
                                        public void done(ParseObject object2, ParseException e) {
                                            if (e == null) {
                                                object2.increment("votes", -1);
                                                object2.saveInBackground();
                                            } else {
                                                // something went wrong
                                            }
                                        }
                                    });
                                }

                                oldObject = newObject;
                                voted = 1;

                                mySwitch.setChecked(true);
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
                        best = object.get("option_name").toString();
                        txtBest = (TextView) findViewById(R.id.best);
                        txtBest.setText(best);
                    }
                }
            });

            //test to see if user is attending
            ParseQuery<ParseObject> switchQuery = new ParseQuery<ParseObject>("Activity");
            switchQuery.whereEqualTo("objectId", objectId);
            switchQuery.include("attending");
            switchQuery.whereEqualTo("attending", ParseUser.getCurrentUser().getUsername());

            //test to see if user is attending
            ParseQuery<ParseObject> attQuery = new ParseQuery<ParseObject>("Activity");
            attQuery.whereEqualTo("objectId", objectId);
//            attQuery.include("attending");

            //get all option names for listview
            try {
                objectList = query3.find();
                objectList2 = switchQuery.find();
                objectList3 = attQuery.find();
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
            for (ParseObject query2 : objectList2) {
                String test = query2.get("attending").toString();

                if (test.contains(ParseUser.getCurrentUser().getUsername().toString())) {
                    mySwitch.setChecked(true);
                } else {
                    mySwitch.setChecked(false);
                }

                // Set textview


            }
            for (ParseObject query3 : objectList3) {
                attendants = query3.get("attending").toString();
                txtAttendants = (TextView) findViewById(R.id.attendants);
                txtAttendants.setText(attendants);
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

            newObject = settings.getString("newObject", "newObject");
            oldObject = settings.getString("oldObject", "oldObject");
            voted = settings.getInt("voted", 0);

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

//	      enable onResume
        editor.putInt("on_pause_check", 1);

        editor.putString("newObject", newObject);
        editor.putString("oldObject", oldObject);
        editor.putInt("voted", voted);

//	      commit the edits!
        editor.commit();

        super.onPause();
    }


    public void onSwitchClicked(View view) {
        // Is the toggle on?
        switch_on = ((Switch) view).isChecked();

        if (switch_on) {
            if (voted == 1) {
                ParseQuery<ParseObject> queryMin = ParseQuery.getQuery("Option");
                queryMin.getInBackground(newObject, new GetCallback<ParseObject>() {
                    public void done(ParseObject object2, ParseException e) {
                        if (e == null) {
                            object2.increment("votes", 1);
                            object2.saveInBackground();
                        } else {
                            // something went wrong
                        }
                    }
                });
            }
                ParseQuery<ParseObject> addAttendantQuery = ParseQuery.getQuery("Activity");
                addAttendantQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
                    public void done(ParseObject object3, ParseException e) {
                        if (e == null) {
                            object3.addAllUnique("attending", Arrays.asList(ParseUser.getCurrentUser().getUsername()));
                            object3.saveInBackground();
                        } else {
                            //something went wrong.
                        }
                    }
                });
        } else {
            if (voted == 1) {
                ParseQuery<ParseObject> queryMin = ParseQuery.getQuery("Option");
                queryMin.getInBackground(newObject, new GetCallback<ParseObject>() {
                    public void done(ParseObject object2, ParseException e) {
                        if (e == null) {
                            object2.increment("votes", -1);
                            object2.saveInBackground();
                        } else {
                            // something went wrong
                        }
                    }
                });
            }
                ParseQuery<ParseObject> addAttendantQuery = ParseQuery.getQuery("Activity");
                addAttendantQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
                    public void done(ParseObject object3, ParseException e) {
                        if (e == null) {
                            if (added == 0){
                                list2.add(ParseUser.getCurrentUser().getUsername().toString());
                                added = 1;
                            }
                            System.out.println(list2);
                            object3.removeAll("attending", list2);
                            object3.saveInBackground();
                        } else {
                            //something went wrong.
                        }
                    }
                });
        }
    }

    public void onAddButtonClicked(View view) {
        Intent intent = new Intent(this, SelectedActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String new_option = editText.getText().toString();
        intent.putExtra("date", date);
        intent.putExtra("objectId", objectId);
        intent.putExtra("activity_name", nameTitle);

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


