package manualchess.application;

import android.app.Application;

public class MyApplication extends Application {
    BigBlackBox bigBlackBox = null;
    @Override
    public void onCreate() {
        super.onCreate();
        bigBlackBox = BigBlackBox.getBigBlackBox(this,"0.tcp.ngrok.io",10276);
    }
}