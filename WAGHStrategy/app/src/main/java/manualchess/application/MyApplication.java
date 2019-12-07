package manualchess.application;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

public class MyApplication extends Application {
    BigBlackBox bigBlackBox = null;
    public AppCompatActivity currentActivity = null;
    @Override
    public void onCreate() {
        super.onCreate();
        bigBlackBox = BigBlackBox.getBigBlackBox(this,"0.tcp.ngrok.io",14504);
    }
}