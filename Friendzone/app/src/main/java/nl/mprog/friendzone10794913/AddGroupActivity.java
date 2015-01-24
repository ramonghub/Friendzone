//AddGroupActivity.java
//Let's the user join or create a group.
package nl.mprog.friendzone10794913;

//TODO: Lid kunnen worden van bestaande groep
//TODO: Maak current user lid van groep

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class AddGroupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
    }

//  When add button is clicked
    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(message, message);

        ParseObject newGroup = new ParseObject("Group");
        newGroup.put("group_name", editText.getText().toString());
        newGroup.saveInBackground();

        startActivity(intent);
        finish();
    }
}
