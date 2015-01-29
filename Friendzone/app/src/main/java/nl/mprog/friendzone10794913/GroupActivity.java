/**
 * Ramon Geessink
 * ramongeessink@gmail.com
 * 10794913
 *
 * GroupActivity.java shows the groups that the user is a member of.
 * The user can select an activity by pressing it.
 */

package nl.mprog.friendzone10794913;

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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GroupActivity extends ActionBarActivity {

    private List<ParseObject> objectList;
    private ListView listview;
    private ProgressDialog mProgressDialog;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        new MakeList().execute();
    }
    //    Make list of all groups to show in listview
    private class MakeList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(GroupActivity.this);
            mProgressDialog.setTitle("Loading");
            mProgressDialog.setMessage("Please wait.");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

//        Selects the correct database objects to show
        @Override
        protected Void doInBackground(Void... params) {
//            Select all groups where user is a member of
            ParseQuery<ParseObject> groupQuery = new ParseQuery<ParseObject>("Group");
            groupQuery.include("members");
            groupQuery.whereEqualTo("members", ParseUser.getCurrentUser().getUsername());

            try {
                objectList = groupQuery.find();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listview = (ListView) findViewById(R.id.listview);
            adapter = new ArrayAdapter<String>(GroupActivity.this, R.layout.activity_main_listview_item);
//            Get group_name to show
            for (ParseObject groupQuery : objectList) {
                adapter.add((String) String.valueOf(groupQuery.get("group_name")));
            }
            listview.setAdapter(adapter);
            mProgressDialog.dismiss();

//                Go to details of activity when clicked on
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent i = new Intent(GroupActivity.this, MainActivity.class);
                    // Pass data "name" followed by the position
                    i.putExtra("objectId", objectList.get(position).getObjectId().toString());
                    i.putExtra("group_name", objectList.get(position).getString("group_name"));
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(GroupActivity.this, DispatchActivity.class));
        }
        if (id == R.id.add_group) {
            Intent intentAddGroup = new Intent(this, AddGroupActivity.class);
            startActivity(intentAddGroup);
        }
        return super.onOptionsItemSelected(item);
    }
}