//GroupActivity.java
//Lets the user choose group and shows activities in that group.
package nl.mprog.friendzone10794913;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public class GroupActivity extends ActionBarActivity implements OnItemSelectedListener {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter;
    Spinner spinnerOsversions;
    TextView selVersion;
    String selState;

    private String[] state = {
            "huisgenoten",
            "vrienden",
            "klas" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        new RemoteDataTask().execute();

        selVersion = (TextView) findViewById(R.id.selVersion);
        spinnerOsversions = (Spinner) findViewById(R.id.osversions);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, state);
                adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOsversions.setAdapter(adapter_state);
        spinnerOsversions.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        spinnerOsversions.setSelection(position);
        selState = (String) spinnerOsversions.getSelectedItem();
        System.out.println(selState);
        selVersion.setText("Viewing " + selState);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(GroupActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Groups");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        //Selects the correct database items to show
        @Override
        protected Void doInBackground(Void... params) {
            //select relation of current user
            ParseObject current = ParseUser.getCurrentUser();
            ParseRelation relation = current.getRelation("groups");
            ParseQuery query = relation.getQuery();

            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Activity");
            query2.whereMatchesQuery("groups", query);

//            ParseObject test = new ParseObject("group");
//            String ObjectId = test.getObjectId();

//            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("_User");
//            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

//            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Group");
//            query2.whereEqualTo("group_name", query2);

//            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("activity");
//            query2.whereMatchesQuery("groups", query);

            try {
                ob = query2.find();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into an ArrayAdapter
            adapter = new ArrayAdapter<String>(GroupActivity.this, R.layout.activity_main_listview_item);
            // Retrieve object "name" from Parse.com database
            for (ParseObject query2 : ob) {
                adapter.add((String) query2.get("activity_name"));
            }
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
            // Capture button clicks on ListView items
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // Send single item click data to SingleItemView Class
                    Intent i = new Intent(GroupActivity.this, SelectedActivity.class);
                    // Pass data "name" followed by the position
                    i.putExtra("group_name", ob.get(position).getString("activity_name").toString());
                    // Open SingleItemView.java Activity
                    startActivity(i);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
