/**
 * Ramon Geessink
 * ramongeessink@gmail.com
 * 10794913
 *
 * In this activity the user can select a group he/she wants to join.
 */

package nl.mprog.friendzone10794913;

import android.content.Intent;
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
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddGroupActivity extends ActionBarActivity {

    private ArrayList<String> groupList;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Object selectedItem;
    private String objectId;
    private List<ParseObject> objectList;
    private TextView txtGroup;
    private String groupTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        new MakeList().execute();

        groupTitle = "Select group:";
        txtGroup = (TextView) findViewById(R.id.groupTitle);
        txtGroup.setText(groupTitle);

        listView = (ListView) findViewById(R.id.listview);
        groupList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, groupList);
        listView.setAdapter(adapter);

//        Get objectId of selected group in listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            selectedItem = listView.getItemAtPosition(position);
            ParseQuery<ParseObject> selectedQuery = ParseQuery.getQuery("Group");
            selectedQuery.whereEqualTo("group_name", selectedItem.toString());
            selectedQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    System.out.println("The getSelected request failed.");
                } else {
                    objectId = String.valueOf(object.getObjectId());
                }
                }
            });
            }
        });

    }

//    Make list with groups that user isn't member of to show in listview
    private class MakeList extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //select relation of current user
            ParseQuery<ParseObject> groupQuery = new ParseQuery<ParseObject>("Group");
            groupQuery.include("members");
            groupQuery.whereNotEqualTo("members", ParseUser.getCurrentUser().getUsername());

            try {
                objectList = groupQuery.find();
            } catch (ParseException error) {
                Log.e("Error", error.getMessage());
                error.printStackTrace();
            }
            return null;
        }

//        Add objects to groupList to show in listview
        @Override
        protected void onPostExecute(Void result) {
            for (ParseObject query : objectList) {
                groupList.add((String) query.get("group_name"));
                adapter.notifyDataSetChanged();
            }
        }
    }

//    Join group when join button is pressed.
    public void joinGroup(View view) {
//        Get selected object and add username to members table
        ParseQuery<ParseObject> addGroupQuery = ParseQuery.getQuery("Group");
        addGroupQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
            if (e == null) {
                gameScore.addAllUnique("members", Arrays.asList(ParseUser.getCurrentUser().getUsername()));
                gameScore.saveInBackground();
            }
            }
        });
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_activity) {
            startActivity(new Intent(AddGroupActivity.this, NewAddGroupActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
