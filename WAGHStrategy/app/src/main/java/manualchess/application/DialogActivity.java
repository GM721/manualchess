package manualchess.application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waghstrategy.R;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
//_____________________________________________________MENU_________________________________________________________


        final PopupMenu popupMenu = new PopupMenu(this,findViewById(R.id.settings));
        popupMenu.inflate(R.menu.settings_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                BigBlackBox bbb = ((MyApplication) getApplication()).bigBlackBox;
                if(item.getTitle().toString().equals("Refresh connection")){
                    Log.d("Refreshed","refreshed");
                    //bbb.establishConnection(bbb.ngrokHost,bbb.port);
                }else if (item.getTitle().toString().equals("Login screen")){
                    //cachedUser=null
                    Intent intent = new Intent(DialogActivity.this,MainActivity.class);
                    startActivity(intent);
                }else if (item.getTitle().toString().equals("Log out")){
                    Log.d("Logout","SuccesfullCalled");
                    bbb.requestToUnauthorisation(bbb.currentUser.nickname, new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DialogActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }return false;
            }
        });

        ImageView settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
//______________________________________________ADAPTER_____________________________________________________________

    }
}
