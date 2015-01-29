/**
 * Ramon Geessink
 * ramongeessink@gmail.com
 * 10794913
 *
 * Checks if user has logged in before, and sends user to correct page
 */
package nl.mprog.friendzone10794913;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class DispatchActivity extends Activity {

//    Check if there is a current user, if so.. go to main activity, else go to login activity.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, GroupActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}