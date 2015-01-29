/**
 * Ramon Geessink
 * ramongeessink@gmail.com
 * 10794913
 *
 * In this activity the user can add other members to join a group that the user creates.
 * The user can enter a name for the group.
 */
package nl.mprog.friendzone10794913;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NewAddGroupActivity extends ActionBarActivity {

    private ArrayList<String> userList;
    private ArrayList<String> selectedUsers;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Object selectedItem;
    private List<ParseObject> objectList;
    private TextView txtGroup;
    private TextView txtMembers;

    private String groupTitle;
    private String membersTitle;
    private String ObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_group);
        new MakeList().execute();

//        Set textviews variables
        groupTitle = "Group name:";
        txtGroup = (TextView) findViewById(R.id.groupTitle);
        txtGroup.setText(groupTitle);

        membersTitle = "Invite:";
        txtMembers = (TextView) findViewById(R.id.membersTitle);
        txtMembers.setText(membersTitle);

        listView = (ListView) findViewById(R.id.listview);
        userList = new ArrayList<String>();
        selectedUsers = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, userList);
        listView.setAdapter(adapter);
//            Add selected users to the list
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    selectedItem = listView.getItemAtPosition(position);
                    selectedUsers.add((String) selectedItem);
                }
            });
    }

    private class MakeList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //select relation of current user
            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("_User");
            userQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            try {
                objectList = userQuery.find();
            } catch (ParseException error) {
                Log.e("Error", error.getMessage());
                error.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for (ParseObject query : objectList) {
                userList.add((String) query.get("username"));
                adapter.notifyDataSetChanged();
            }
        }
    }

//    Make group when create group button is clicked
    public void CreateGroup(View view) {
        Intent intent = new Intent(this, GroupActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String newGroupName = editText.getText().toString();

//        Make new object in group class with the given variables
        ParseObject newGroup = new ParseObject("Group");
        newGroup.put("group_name", newGroupName);
        newGroup.addAll("members", selectedUsers);
        newGroup.saveInBackground();

        startActivity(intent);
        finish();
    }
}