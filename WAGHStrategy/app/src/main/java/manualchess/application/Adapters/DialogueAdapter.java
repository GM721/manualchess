package manualchess.application.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waghstrategy.R;

import java.util.TreeMap;

import CommonClasses.Message;
import UtilClasses.RunnableWithResource;
import UtilClasses.NotifiableTreeMap;
import manualchess.application.BigBlackBox;
import manualchess.application.CollocutorActivity;
import manualchess.application.MyApplication;

public class DialogueAdapter extends RecyclerView.Adapter {
    NotifiableTreeMap<String, Message> notifiableTreeMap;
    BigBlackBox bigBlackBox;
    TreeMap<String,Message> treeMap;
    Context context;

    public DialogueAdapter(Context context,BigBlackBox bigBlackBox){
        this.bigBlackBox = bigBlackBox;
        this.notifiableTreeMap = bigBlackBox.getNotifiableLastMessages();
        this.context = context;
        notifiableTreeMap.setOnTreeMapSetAction(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        notifiableTreeMap.setOnTreeMapInsertAction(new RunnableWithResource<Integer>() {
            @Override
            public void run(Integer... item) {
                notifyItemChanged(item[0]);

            }
        });
        notifiableTreeMap.setOnElementChangedAction(new RunnableWithResource<Integer>() {
            @Override
            public void run(Integer... item) {
                notifyItemChanged(item[0]);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView collocutor;
        TextView lastMessage;
        TextView date;
        ImageView startBattle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);//itemView - which itemView is inflated
            this.collocutor = itemView.findViewById(R.id.collocutorTextView);
            this.lastMessage = itemView.findViewById(R.id.lastMessageTextView);
            this.date = itemView.findViewById(R.id.dateTextView);
            this.startBattle = itemView.findViewById(R.id.startBattleView);
        }

        public TextView getCollocutor() {
            return collocutor;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout linearLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_dialogview,parent,false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(final @NonNull RecyclerView.ViewHolder holder, int position) {
        final Message message = notifiableTreeMap.getItem(position);
        ((ViewHolder)holder).collocutor.setText(message.sender);
        ((ViewHolder)holder).lastMessage.setText(message.message);
        ((ViewHolder)holder).date.setText(message.getDate());
        ((ViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigBlackBox.setCurrentCollocutor(message.sender);
                bigBlackBox.prepareCollocationSystem();
                Intent intent = new Intent(context, CollocutorActivity.class);
                context.startActivity(intent);
            }
        });
        ((ViewHolder)holder).startBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigBlackBox.startBattle(message.sender,true, new RunnableWithResource<Integer>() {
                    @Override
                    public void run(Integer... resources) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(((MyApplication)context.getApplicationContext()).currentActivity);
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
    }

    @Override
    public int getItemCount() {
        if(notifiableTreeMap!=null) {
            return notifiableTreeMap.getSize();
        }else return 0;
    }
}
