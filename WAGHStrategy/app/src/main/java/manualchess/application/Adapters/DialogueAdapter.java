package manualchess.application.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waghstrategy.R;

import org.w3c.dom.Text;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.TreeMap;

import CommonClasses.Message;
import UtilClasses.NotifiableDataArray;

public class DialogueAdapter extends RecyclerView.Adapter {
    NotifiableDataArray<String, Message> notifiableDataArray;
    TreeMap<String,Message> treeMap;
    Context context;

    public DialogueAdapter(NotifiableDataArray<String,Message> notifiableDataArray){
        this.notifiableDataArray = notifiableDataArray;
        notifiableDataArray.setOnTreeMapSetAction(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        notifiableDataArray.setOnTreeMapInsertAction(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();//TODO redo with runnableWithResources
            }
        });
        notifiableDataArray.setOnElementChangedAction(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();//TODO redo with runnableWithResources
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView collocutor;
        TextView lastMessage;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);//itemView - which itemView is inflated
            this.collocutor = (TextView) itemView.findViewById(R.id.collocutorTextView);
            this.lastMessage = (TextView) itemView.findViewById(R.id.lastMessageTextView);
            this.date = (TextView) itemView.findViewById(R.id.dateTextView);
        }

        public TextView getCollocutor() {
            return collocutor;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_dialog,parent,false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = notifiableDataArray.getItem(position);
        ((ViewHolder)holder).collocutor.setText(message.sender);
        ((ViewHolder)holder).lastMessage.setText(message.message);
        ((ViewHolder)holder).date.setText(message.date.toString());
    }

    @Override
    public int getItemCount() {
        return notifiableDataArray.getSize();
    }
}
