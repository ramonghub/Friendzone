//SelectedActivity.java
//Shows selected activity with options
package nl.mprog.friendzone10794913;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SelectedActivity extends ActionBarActivity {

TextView txtname;
String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);

        // Get the name
        name = getIntent().getStringExtra("name");

        // Locate the TextView in singleitemview.xml
        txtname = (TextView) findViewById(R.id.name);

        // Load the text into the TextView
        txtname.setText(name);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_selected, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
