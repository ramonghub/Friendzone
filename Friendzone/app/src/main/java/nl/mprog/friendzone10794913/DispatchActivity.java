//DispatchActivity.java
//Sends the user to correct page.
package nl.mprog.friendzone10794913;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class DispatchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}