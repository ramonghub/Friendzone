// MainActivity.java
// Shows all the activities that the user takes part in.
package nl.mprog.friendzone10794913;

//TODO: Laat groep zien (actitivy_main_listview_item) (optioneel)

import java.util.ArrayList;
import java.util.List;
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
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;

public class MainActivity extends ActionBarActivity {

    // Declare Variables
    private ListView listview;
    private List<ParseObject> ob;
    private ProgressDialog mProgressDialog;
    private ArrayAdapter<String> adapter;
    private List<ParseObject> testList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.activity_main_listview);
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
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

        //Selects the correct database items to show
        @Override
        protected Void doInBackground(Void... params) {
            // set up our query for the Book object
            ParseQuery bookQuery = ParseQuery.getQuery("_User");
            bookQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername().toString());

            bookQuery.include("array");

            //select relation of current user
            ParseObject current = ParseUser.getCurrentUser();
            ParseRelation relation = current.getRelation("groups");
            ParseQuery query = relation.getQuery();

            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Activity");
            query2.whereMatchesQuery("groups", query);
            query2.orderByAscending("date");

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
            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_main_listview_item);
            // Retrieve object "name" from Parse.com database
            for (ParseObject query2 : ob) {
                adapter.add((String) String.valueOf(query2.get("date")));
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
                    i.putExtra("date", ob.get(position).getDate("date").toString());
                    i.putExtra("objectId", ob.get(position).getObjectId().toString());
                    // Open SingleItemView.java Activity
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(MainActivity.this, DispatchActivity.class));
        }
        if (id == R.id.add_activity) {
            Intent intentAddActivity = new Intent(this, AddActivity.class);
            startActivity(intentAddActivity);
        }
        if (id == R.id.add_group) {
            Intent intentAddGroup = new Intent(this, AddGroupActivity.class);
            startActivity(intentAddGroup);
        }
        return super.onOptionsItemSelected(item);
    }
}