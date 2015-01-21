package nl.mprog.friendzone10794913;

import java.security.acl.Group;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.activity_main_listview);
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();

//        Sign up button click handler
        ((Button) findViewById(R.id.button_group)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(MainActivity.this, GroupActivity.class));
            }
        });
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Logging in.");
            // Set progressdialog message
            mProgressDialog.setMessage("Please wait.");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
//            ParseObject test = new ParseObject("group");
//            String ObjectId = test.getObjectId();
//
//            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Activity");
//            query2.whereEqualTo("group_name", ObjectId);
//
            ParseObject activity = new ParseObject("Activity");
            String act_id = activity.getObjectId();

//            ParseRelation query2 = activity.getRelation("groups");
            ParseQuery<ParseObject> test = ParseQuery.getQuery("group");
            test.whereEqualTo("objectId", act_id);

//            ParseObject current = ParseUser.getCurrentUser();
//            ParseRelation relation = current.getRelation("groups");
//            ParseQuery query = relation.getQuery();

            ParseQuery<ParseObject> gameQuery = ParseQuery.getQuery("group");
            gameQuery.whereEqualTo("objectId",);

            try {
                ob = test.find();
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
            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_main_listview_item);
            // Retrieve object "name" from Parse.com database
            for (ParseObject query : ob) {
                adapter.add((String) query.get("activity_name"));
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
                    Intent i = new Intent(MainActivity.this, SelectedActivity.class);
                    // Pass data "name" followed by the position
                    i.putExtra("group_name", ob.get(position).getString("group_name").toString());
                    // Open SingleItemView.java Activity
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(MainActivity.this, DispatchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}