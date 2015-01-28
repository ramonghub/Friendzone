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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class NewAddGroupActivity extends ActionBarActivity {

    private ArrayList<String> list;
    private ArrayList<String> selectedUsers;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Object selectedItem;
    private String ObjectId;
    private List<ParseObject> objectList;

    private TextView txtGroup;
    private String groupTitle;

    private TextView txtMembers;
    private String membersTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_group);
        new RemoteDataTask().execute();

        groupTitle = "Group name:";
        txtGroup = (TextView) findViewById(R.id.groupTitle);
        txtGroup.setText(groupTitle);

        membersTitle = "Invite:";
        txtMembers = (TextView) findViewById(R.id.membersTitle);
        txtMembers.setText(membersTitle);

        listView = (ListView) findViewById(R.id.listview);
        list = new ArrayList<String>();
        selectedUsers = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectedItem = listView.getItemAtPosition(position);
                System.out.println(selectedItem);
                selectedUsers.add((String) selectedItem);
                System.out.println(selectedUsers);

//                HashSet hs = new HashSet();
//                hs.addAll(selectedUsers);
//                selectedUsers.clear();
//                selectedUsers.addAll(hs);

//                ParseQuery<ParseObject> selectedQuery = ParseQuery.getQuery("_User");
//                selectedQuery.whereEqualTo("objectId", selectedItem.toString());
//                selectedQuery.getFirstInBackground(new GetCallback<ParseObject>() {
//                    public void done(ParseObject object, ParseException e) {
//                        if (object == null) {
//                            System.out.println("The getSelected request failed.");
//                        } else {
//                            ObjectId = String.valueOf(object.get("username"));
//                            System.out.println(ObjectId);
//                        }
//                    }
//                });
            }
        });

    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //select relation of current user
            ParseQuery<ParseObject> groupQuery = new ParseQuery<ParseObject>("_User");
//            groupQuery.orderByDescending("createdAt");
            groupQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            try {
                objectList = groupQuery.find();
            } catch (ParseException error) {
                Log.e("Error", error.getMessage());
                error.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for (ParseObject query : objectList) {
                list.add((String) query.get("username"));
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String newGroupName = editText.getText().toString();

            ParseObject newGroup = new ParseObject("Group");
            newGroup.put("group_name", newGroupName);
            newGroup.addAll("members", selectedUsers);
            newGroup.saveInBackground();

        startActivity(intent);
        finish();
    }
}