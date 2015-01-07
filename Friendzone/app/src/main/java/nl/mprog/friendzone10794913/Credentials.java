package nl.mprog.friendzone10794913;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by RamonProBook on 07-01-15.
 */
public class Credentials extends Application{

    public void onCreate(){
    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);
    Parse.initialize(this, "swoGi3XwUvWrQQ2sHe8HBXwT8Eli2WxJrN9kx2Ip", "CE1xzDQAZbKUs64wRBtr8dQOeZwhaymJKOnGaU9c");
    }

}
