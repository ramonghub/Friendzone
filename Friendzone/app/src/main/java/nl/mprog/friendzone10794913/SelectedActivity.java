/**
 * Ramon Geessink
 * ramongeessink@gmail.com
 * 10794913
 *
 * The information of the selected activity is shown in this activity.
 * The user can vote by pressing on an option.
 */
package nl.mprog.friendzone10794913;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectedActivity extends ActionBarActivity {

    private Object selectedItem;
    private boolean switch_on;
    private Integer voted = 0;
    private Integer addedAttToList = 0;
    private String date;
    private String dateTitle;
    private String bestTitle;
    private String nameTitle;
    private String best;
    private String newOption;
    private String selectedObjectId2;
    private String selectedObjectId;
    private String optionsTitle;
    private String objectId;
    private String attendants;
    private String attendantsTitle;
    private String attCheck;
    private TextView txtDate;
    private TextView txtDateTitle;
    private TextView txtBestTitle;
    private TextView txtNameTitle;
    private TextView txtBest;
    private TextView txtOptions;
    private TextView txtAttendantsTitle;
    private TextView txtAttendants;
    private ListView listView;
    private List<ParseObject> optionsObjectList;
    private List<ParseObject> currentUserObjectList;
    private List<ParseObject> attendantsObjectList;
    private ArrayList<String> optionsList;
    private ArrayList<String> attendantList;
    private ArrayAdapter<String> adapter;
    private Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        new RemoteDataTask().execute();

        final Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);

//        Get intent variable values
        objectId = getIntent().getStringExtra("objectId");
        nameTitle = getIntent().getStringExtra("activity_name");
        date = getIntent().getStringExtra("date");

//        Set text of headers
        attendantsTitle = "Attendants:";
        dateTitle = "Date:";
        bestTitle = "Best scoring:";
        optionsTitle = "Options:";

//        Link xml textviews with code textviews
        txtNameTitle = (TextView) findViewById(R.id.nameTitle);
        txtAttendantsTitle = (TextView) findViewById(R.id.attendantsTitle);
        txtDateTitle = (TextView) findViewById(R.id.dateTitle);
        txtBestTitle = (TextView) findViewById(R.id.bestTitle);
        txtDate = (TextView) findViewById(R.id.date);
        txtOptions = (TextView) findViewById(R.id.options);
        listView = (ListView) findViewById(R.id.listview);

//        Set text as textview values
        txtNameTitle.setText(nameTitle);
        txtAttendantsTitle.setText(attendantsTitle);
        txtDateTitle.setText(dateTitle);
        txtBestTitle.setText(bestTitle);
        txtDate.setText(date);
        txtOptions.setText(optionsTitle);

//        Get reference to switch1
        mySwitch = (Switch) findViewById(R.id.switch1);

//        Declare lists
        optionsList = new ArrayList<String>();
        attendantList = new ArrayList<String>();

//        Set adapter as listview adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,
                optionsList);
        listView.setAdapter(adapter);

//        Handles click on the listview with options to vote
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Get selected option from ListView
                selectedItem = listView.getItemAtPosition(position);
//                Get selected object from parse
                ParseQuery<ParseObject> selectedQuery = ParseQuery.getQuery("Option");
                selectedQuery.whereEqualTo("option_name", selectedItem.toString());
                selectedQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                            System.out.println("The selectedItem request failed.");
                        } else {
                            selectedObjectId = String.valueOf(object.getObjectId());
//                            Only count vote when user selected other option then last time
                            if (!selectedObjectId.equals(selectedObjectId2)) {
                                object.increment("votes", 1);
                                object.saveInBackground();
//                                If user selects another option then remove old vote
                                if (voted == 1) {
                                    ParseQuery<ParseObject> queryMin = ParseQuery.getQuery("Option");
                                    queryMin.getInBackground(selectedObjectId2, new GetCallback<ParseObject>() {
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
//                                Remember selected option
                                selectedObjectId2 = selectedObjectId;
//                                The user has voted, set voted to 1
                                voted = 1;
//                                Set attending switch to true
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
//            Get all groups that the user is a member of
            ParseQuery<ParseObject> groupQuery = new ParseQuery<ParseObject>("Group");
            groupQuery.include("members");
            groupQuery.whereEqualTo("members", ParseUser.getCurrentUser().getUsername());

//            Get the selected activity
            ParseQuery<ParseObject> selectedActivityQuery = new ParseQuery<ParseObject>("Activity");
            selectedActivityQuery.whereMatchesQuery("groups", groupQuery);
            selectedActivityQuery.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));

//            Get all options from the selected activity
            ParseQuery<ParseObject> optionsQuery = new ParseQuery<ParseObject>("Option");
            optionsQuery.whereMatchesQuery("activities", selectedActivityQuery);

//            Get the best scoring option and display in txtBest
            ParseQuery<ParseObject> bestQuery = ParseQuery.getQuery("Option");
            bestQuery.orderByDescending("votes");
            bestQuery.whereMatchesQuery("activities", selectedActivityQuery);
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

//            Get the current user in attending table from selected activity
            ParseQuery<ParseObject> switchQuery = new ParseQuery<ParseObject>("Activity");
            switchQuery.whereEqualTo("objectId", objectId);
            switchQuery.include("attending");
            switchQuery.whereEqualTo("attending", ParseUser.getCurrentUser().getUsername());

//            Get all attendants
            ParseQuery<ParseObject> attQuery = new ParseQuery<ParseObject>("Activity");
            attQuery.whereEqualTo("objectId", objectId);

//            Put all found objects from query in different lists
            try {
                optionsObjectList = optionsQuery.find();
                currentUserObjectList = switchQuery.find();
                attendantsObjectList = attQuery.find();
            } catch (ParseException error) {
                Log.e("Error", error.getMessage());
                error.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            Put all the objects from optionsObjectList into optionsList to display in TextView
            for (ParseObject optionsQuery : optionsObjectList) {
                optionsList.add((String) optionsQuery.get("option_name"));
                adapter.notifyDataSetChanged();
            }
//            Cast the attendees to attCheck and see if currentUser is in that string
            for (ParseObject switchQuery : currentUserObjectList) {
                attCheck = switchQuery.get("attending").toString();
//                If user is in attCheck set switch to on, else set it to off
                if (attCheck.contains(ParseUser.getCurrentUser().getUsername().toString())) {
                    mySwitch.setChecked(true);
                } else {
                    mySwitch.setChecked(false);
                }
            }
//            Get all objects from the objectlist and cast it to string to display
            for (ParseObject attQuery : attendantsObjectList) {
                attendants = attQuery.get("attending").toString();
                txtAttendants = (TextView) findViewById(R.id.attendants);
                txtAttendants.setText(attendants);
            }
        }
    }

//    Initiated when the switch status is changed
    public void onSwitchClicked(View view) {
        switch_on = ((Switch) view).isChecked();
//        When the switch is set to on
        if (switch_on) {
            if (voted == 1) {
//                If the user voted and the switch is set to on the vote counts.
                ParseQuery<ParseObject> queryMin = ParseQuery.getQuery("Option");
                queryMin.getInBackground(selectedObjectId, new GetCallback<ParseObject>() {
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
//            The user get deleted from the attending list
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
//            When the switch is set to off
            if (voted == 1) {
//                If the user voted and the switch is set to off the vote doesn't count
                ParseQuery<ParseObject> queryMin = ParseQuery.getQuery("Option");
                queryMin.getInBackground(selectedObjectId, new GetCallback<ParseObject>() {
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
//            The user gets deleted from the attending list
            ParseQuery<ParseObject> addAttendantQuery = ParseQuery.getQuery("Activity");
            addAttendantQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
                public void done(ParseObject object3, ParseException e) {
                    if (e == null) {
                        if (addedAttToList == 0){
                            attendantList.add(ParseUser.getCurrentUser().getUsername().toString());
                            addedAttToList = 1;
                        }
                    object3.removeAll("attending", attendantList);
                    object3.saveInBackground();
                    } else {
                        //something went wrong.
                    }
                }
            });
        }
    }

//    Handles the add option button click
    public void AddOption(View view) {
//        Gets the entered option name
        EditText editText = (EditText) findViewById(R.id.edit_message);
        newOption = editText.getText().toString();

//        Creates a new option in Option Class
        ParseObject newOptionQuery = new ParseObject("Option");
        newOptionQuery.put("activities", ParseObject.createWithoutData("Activity", objectId));
        newOptionQuery.put("option_name", newOption);
        newOptionQuery.put("votes", 0);
        newOptionQuery.saveInBackground();

//        Relaunches the activity to show the new option
        Intent intent = new Intent(this, SelectedActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("objectId", objectId);
        intent.putExtra("activity_name", nameTitle);
        startActivity(intent);
        finish();
        }

    //    Restore saved data
    @Override
    public void onResume() {
        Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        int pauseCheck = settings.getInt("pauseCheck", 0);

        if (pauseCheck == 1) {
            listView.setItemChecked(settings.getInt("selectedObject", 0), true);
            if (settings.getInt("resume_attending", 0) == 1) {
                mySwitch = (Switch) findViewById(R.id.switch1);
                mySwitch.setChecked(true);
            }
            selectedObjectId = settings.getString("selectedObjectId", "selectedObjectId");
            selectedObjectId2 = settings.getString("selectedObjectId2", "selectedObjectId2");
            voted = settings.getInt("voted", 0);
        }
        super.onResume();
    }

    //    Save data
    @Override
    public void onPause() {
        Context mContext = getApplicationContext();
        SharedPreferences settings = mContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("selectedObject", listView.getCheckedItemPosition());
        editor.putInt("pauseCheck", 1);
        editor.putString("selectedObjectId", selectedObjectId);
        editor.putString("selectedObjectId2", selectedObjectId2);
        editor.putInt("voted", voted);

        editor.commit();
        super.onPause();
    }
}

