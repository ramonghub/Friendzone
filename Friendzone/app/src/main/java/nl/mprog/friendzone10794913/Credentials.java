package nl.mprog.friendzone10794913;

import android.app.Application;
import com.parse.Parse;

public class Credentials extends Application {

    public void onCreate() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "swoGi3XwUvWrQQ2sHe8HBXwT8Eli2WxJrN9kx2Ip", "CE1xzDQAZbKUs64wRBtr8dQOeZwhaymJKOnGaU9c");
    }

}