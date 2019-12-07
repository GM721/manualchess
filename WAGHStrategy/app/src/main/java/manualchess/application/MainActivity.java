package manualchess.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import CommonClasses.PlayerAuthorise;
import CommonClasses.PlayerRegister;
import manualchess.application.Adapters.AuthorisedAdapter;
import com.example.waghstrategy.R;

public class MainActivity extends AppCompatActivity {

    MyApplication myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = ((MyApplication) getApplication());
        myApp.currentActivity=this;
        final BigBlackBox bigBlackBox = myApp.bigBlackBox;
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = findViewById(R.id.authorisedRecyclerView);
        recyclerView.setAdapter(new AuthorisedAdapter(this,bigBlackBox));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button regButton = findViewById(R.id.regButton);
        Button authButton = findViewById(R.id.authButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.EmailEditText)).getText().toString();
                String password = ((EditText) findViewById(R.id.PasswordEditText)).getText().toString();
                String nickname = ((EditText) findViewById(R.id.NicknameEditText)).getText().toString();
                Log.d("Nic+pas+em",nickname+password+email);
                Toast.makeText(MainActivity.this,"Registration initiated",Toast.LENGTH_SHORT);
                bigBlackBox.requestRegisterUser(new PlayerRegister(nickname, email, password,
                        null, "registration"), new Runnable() {
                    @Override
                    public void run() {
                        ((AuthorisedAdapter)recyclerView.getAdapter()).updateAdapter();
                        Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                        startActivity(intent);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                    }
                });

                //Не переходить,если ответа не поступило
            }
        });
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((EditText) findViewById(R.id.PasswordEditText)).getText().toString();
                String nickname = ((EditText) findViewById(R.id.NicknameEditText)).getText().toString();
                Toast.makeText(MainActivity.this,"Authorisation initiated",Toast.LENGTH_SHORT);
                bigBlackBox.requestAuthoriseUser(new PlayerAuthorise(null,nickname,
                        password,"authorisation"), new Runnable() {
                    @Override
                    public void run() {
                        ((AuthorisedAdapter)recyclerView.getAdapter()).updateAdapter();
                        Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                        startActivity(intent);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                //Не переходить,если ответ
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myApp.currentActivity=this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApp.currentActivity=null;
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}
