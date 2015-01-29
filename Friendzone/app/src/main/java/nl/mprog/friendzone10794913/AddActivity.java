/**
 * Ramon Geessink
 * ramongeessink@gmail.com
 * 10794913
 *
 * This activity lets the user create a new activity in a selected group.
 * The user can set time and date and giva a name to the activity.
 */

package nl.mprog.friendzone10794913;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddActivity extends ActionBarActivity {

    private Object selectedItem ;
    private ArrayAdapter<String> adapter;
    private List<ParseObject> objectList;
    private ArrayList<String> groupList;
    private ListView listView;
    private TextView txtDate;
    private TextView txtTime;
    private TextView txtGroup;
    private TextView txtName;
    private String dateTitle;
    private String timeTitle;
    private String groupTitle;
    private String nameTitle;
    private String ObjectId;
    private String tDate;
    public static String time;
    public static String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        new MakeGroupList().execute();

//        Set values of the textviews
        groupTitle = "Select group:";
        txtGroup = (TextView) findViewById(R.id.groupTitle);
        txtGroup.setText(groupTitle);

        nameTitle = "Activity name:";
        txtName = (TextView) findViewById(R.id.nameTitle);
        txtName.setText(nameTitle);

        timeTitle = "Pick a time:";
        txtTime = (TextView) findViewById(R.id.timeTitle);
        txtTime.setText(timeTitle);

        dateTitle = "Pick a date:";
        txtDate = (TextView) findViewById(R.id.dateTitle);
        txtDate.setText(dateTitle);

//        Set ArrayList list as the listview that is shown
        listView = (ListView) findViewById(R.id.listview);
        groupList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,
                groupList);
        listView.setAdapter(adapter);

//        When the user clicks on item get ObjectId of the Group that activity is for.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            selectedItem = listView.getItemAtPosition(position);
            ParseQuery<ParseObject> selectedQuery = ParseQuery.getQuery("Group");
            selectedQuery.whereEqualTo("group_name", selectedItem.toString());
            selectedQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    System.out.println("Failed getting the objectId of selected item.");
                } else {
                    ObjectId = String.valueOf(object.getObjectId());
                }
                }
            });
            }
        });
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

//            Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

//        Add the current time to String time to set as the time of the activity
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            StringBuilder timeBuilder = new StringBuilder().append(hourOfDay).append(":").append(minute);
            time = timeBuilder.toString();
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            StringBuilder dateBuilder = new StringBuilder().append(year).append("-").append(month).append("-").append(day);
            date = dateBuilder.toString();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private class MakeGroupList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
//            Select all groups that the current user is a member of.
            ParseQuery<ParseObject> groupQuery = new ParseQuery<ParseObject>("Group");
            groupQuery.include("members");
            groupQuery.whereEqualTo("members", ParseUser.getCurrentUser().getUsername());

//            Find all objects that match query
            try {
                objectList = groupQuery.find();
            } catch (ParseException error) {
                Log.e("Error", error.getMessage());
                error.printStackTrace();
            }
            return null;
        }

//        Loop through objectList and add objects to groupList as a string to show in the listview.
        @Override
        protected void onPostExecute(Void result) {
            for (ParseObject groupQuery : objectList) {
                groupList.add((String) groupQuery.get("group_name"));
                adapter.notifyDataSetChanged();
            }
        }
    }

    //  When add button is clicked
    public void addActivity(View view) {
        Intent intent = new Intent(this, GroupActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String activityName = editText.getText().toString();

//        Prepare date and time to be set as in parse
        StringBuilder tDateBuilder = new StringBuilder().append(date).append("T").append(time).append(":00.000Z");
        tDate = tDateBuilder.toString();
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS'Z'");
        Date date = dateFormat.parse(tDate, pos);

//        Create new activity object in parse with given attributes
        ParseObject newActivityQuery = new ParseObject("Activity");
        newActivityQuery.put("groups", ParseObject.createWithoutData("Group", ObjectId));
        newActivityQuery.put("activity_name", activityName);
        newActivityQuery.addAllUnique("attending", Arrays.asList(ParseUser.getCurrentUser().getUsername()));
        newActivityQuery.put("date", date);
        newActivityQuery.saveInBackground();

        startActivity(intent);
        finish();
    }
}
