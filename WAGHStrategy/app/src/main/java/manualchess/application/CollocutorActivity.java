package manualchess.application;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waghstrategy.R;

import manualchess.application.Adapters.CollocutorAdapter;

public class CollocutorActivity extends AppCompatActivity {
    BigBlackBox bbb;
    MyApplication myApp;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Collocutor","activitystarted");
        setContentView(R.layout.activity_collocutor);
        myApp = (MyApplication) getApplication();
        myApp.currentActivity=this;
        Log.d("Collocutor","content view set");
        bbb = ((MyApplication) getApplication()).bigBlackBox;
        Log.d("Collocutor","big black box gotten");
        final RecyclerView recyclerView = findViewById(R.id.collocationRecyclerView);
        Log.d("Collocutor","recycler view inited");
        recyclerView.setAdapter(new CollocutorAdapter(bbb));
        Log.d("Collocutor","adapter set");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("Collocutor","layout manager set");
        bbb.startCollocationSystem();
        Log.d("Collocutor","collocation system started");
        final EditText editText = findViewById(R.id.enterMessageEditText);
        Button button = findViewById(R.id.sendMessageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button","clicked");
                bbb.sendMessage(editText.getText().toString(), new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CollocutorActivity.this,"Connection lost",Toast.LENGTH_LONG).show();
                    }
                });
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
        bbb.clearCollocationResources();
        Intent intent = new Intent(CollocutorActivity.this,DialogActivity.class);
        startActivity(intent);
    }
}
