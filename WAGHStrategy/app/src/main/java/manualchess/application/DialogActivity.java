package manualchess.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waghstrategy.R;

import UtilClasses.RunnableWithResource;
import manualchess.application.Adapters.DialogueAdapter;

public class DialogActivity extends AppCompatActivity {
    BigBlackBox bbb;
    MyApplication myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        myApp = ((MyApplication) getApplication());
        bbb = myApp.bigBlackBox;
        myApp.currentActivity=this;
        bbb.setDiceRequestRunnable(new RunnableWithResource<String>() {
            @Override
            public void run(final String... resources) {
                Log.d("Execution","of battle begun");
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setTitle("Challenge!");
                builder.setMessage(resources[0] + "challenged you to test your luck! Would you agree?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bbb.startBattle(resources[0],false, new RunnableWithResource<Integer>() {
                            @Override
                            public void run(Integer... resources) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setTitle("Result!");
                                if (resources[0] > resources[1]) {
                                    builder.setMessage("You won!" + "\n Your roll:" + resources[0] + "\n" + "Opponents roll:" + resources[1]);
                                }
                                if (resources[0] == resources[1]) {
                                    builder.setMessage("Draw!" + "\n Your roll:" + resources[0] + "\n" + "Opponents roll:" + resources[1]);
                                }
                                if (resources[0] < resources[1]) {
                                    builder.setMessage("You lost!" + "\n Your roll:" + resources[0] + "\n" + "Opponents roll:" + resources[1]);
                                }
                                builder.create().show();
                            }
                        }, new RunnableWithResource<String>() {
                            @Override
                            public void run(String... resources) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bbb.declineBattle(resources[0]);
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.d("Dialog","On cancel called");
                        bbb.declineBattle(resources[0]);
                    }
                });
                builder.create().show();
            }
        });
//_____________________________________________________MENU_________________________________________________________


        final PopupMenu popupMenu = new PopupMenu(this,findViewById(R.id.settings));
        popupMenu.inflate(R.menu.settings_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals("Login screen")){
                    Intent intent = new Intent(DialogActivity.this,MainActivity.class);
                    bbb.leaveDialogSystem();
                    startActivity(intent);
                }else if (item.getTitle().toString().equals("Log out")){
                    Log.d("Logout","SuccesfullCalled");
                    bbb.requestToUnauthorisation(bbb.currentUser.nickname, new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DialogActivity.this, MainActivity.class);
                            bbb.leaveDialogSystem();
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
        final RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setAdapter(new DialogueAdapter(this,bbb));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("DialogAct","acting");
        bbb.prepareDialogSystem(bbb.currentUser.nickname);
        bbb.startDialogueSystem();
//______________________________________________EDITTEXT____________________________________________________________
        Button button = findViewById(R.id.searchButton);
        final EditText editText = findViewById(R.id.findUserTextView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bbb.searchCollocutor(editText.getText().toString(), new Runnable() {
                    @Override
                    public void run() {
                        bbb.prepareCollocationSystem();
                        Intent intent = new Intent(DialogActivity.this, CollocutorActivity.class);
                        DialogActivity.this.startActivity(intent);
                        editText.setText("");
                    }
                }, new Runnable() {
                    @Override
                    public void run() {

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
        bbb.leaveDialogSystem();
        Intent intent = new Intent(DialogActivity.this,MainActivity.class);
        startActivity(intent);
    }

}
