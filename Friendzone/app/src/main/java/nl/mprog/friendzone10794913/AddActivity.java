//AddActivity.java
//Let's the user add a new activity in certain group.
package nl.mprog.friendzone10794913;

//TODO: Link activiteit met groep
//TODO: Zorg dat ze tijd kunnen aangeven

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends ActionBarActivity{

    private Spinner spinnerOsversions;
    private String selState;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter_state;
    private List<ParseObject> objectList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_group);
//            spinnerOsversions = (Spinner) findViewById(R.id.spinner);
//
//            list = new ArrayList<String>();
//            adapter_state = new ArrayAdapter<String>(this,
//                    android.R.layout.simple_spinner_item, list);
//            adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerOsversions.setAdapter(adapter_state);
//            spinnerOsversions.setOnItemSelectedListener(this);
//            selState = (String) spinnerOsversions.getSelectedItem();
//
//            ParseObject current = ParseUser.getCurrentUser();
//            ParseRelation relation = current.getRelation("groups");
//            ParseQuery query = relation.getQuery();
//
//            try {
//                objectList = query.find();
//            } catch (ParseException error) {
//                Log.e("Error", error.getMessage());
//                error.printStackTrace();
//            }
//
//            for (ParseObject groupQuery : objectList) {
//                list.add((String) groupQuery.get("group_name"));
//                adapter_state.notifyDataSetChanged();
//            }
//        }
//
//    public void onItemSelected(AdapterView<?> parent, View view, int position,
//                               long id) {
//        spinnerOsversions.setSelection(position);
////        selState = (String) spinnerOsversions.getSelectedItem();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> arg0) {
//        // TODO Auto-generated method stub
    }

//  When add button is clicked
    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(message, message);

        ParseObject newActivity = new ParseObject("Activity");
        newActivity.put("activity_name", editText.getText().toString());
        newActivity.put("groups", editText.getText().toString());
        newActivity.saveInBackground();

        startActivity(intent);
        finish();
    }
}
