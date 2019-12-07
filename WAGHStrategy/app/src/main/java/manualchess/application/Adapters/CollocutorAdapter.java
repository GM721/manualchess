package manualchess.application.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waghstrategy.R;

import CommonClasses.Message;
import UtilClasses.NotifiableArrayList;
import UtilClasses.RunnableWithResource;
import manualchess.application.BigBlackBox;

public class CollocutorAdapter extends RecyclerView.Adapter {
    private static final int USER=1;
    private static final int COLLOCUTOR=2;
    private BigBlackBox bigBlackBox;
    private NotifiableArrayList<Message> notifiableArrayList;
    public CollocutorAdapter(BigBlackBox bigBlackBox){
        this.notifiableArrayList = bigBlackBox.getNotifiableCollocation();
        this.bigBlackBox = bigBlackBox;
        notifiableArrayList.setOnSetListener(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        notifiableArrayList.setOnAddListener(new RunnableWithResource<Integer>() {
            @Override
            public void run(Integer... item) {
                notifyItemChanged(item[0]);
            }
        });
    }

    class ViewHolderUser extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView dateTextView;
        TextView authorTextView;
        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.userMessageTextView);
            dateTextView = itemView.findViewById(R.id.userDateTextView);
            authorTextView = itemView.findViewById(R.id.userAuthorTextView);
        }
    }

    class ViewHolderCollocutor extends RecyclerView.ViewHolder{
        TextView messageTextView;
        TextView dateTextView;
        TextView authorTextView;
        public ViewHolderCollocutor(@NonNull View itemView){
            super(itemView);
            messageTextView = itemView.findViewById(R.id.collocutorMessageTextView);
            dateTextView = itemView.findViewById(R.id.collocutorDateTextView);
            authorTextView = itemView.findViewById(R.id.collocutorAuthorTextView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        String sender = notifiableArrayList.getItem(position).sender;
        if(sender.equals(bigBlackBox.currentUser.nickname)){
            return USER;
        }else return COLLOCUTOR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==USER){
            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.user_message,parent,false);
            return new ViewHolderUser(constraintLayout);
        }
        else{
            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.collocutor_message,parent,false);
            return new ViewHolderCollocutor(constraintLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType()==USER){
            ViewHolderUser viewHolderUser = (ViewHolderUser)holder;
            viewHolderUser.messageTextView.setText(notifiableArrayList.getItem(position).message);
            viewHolderUser.dateTextView.setText(notifiableArrayList.getItem(position).getDate());
            viewHolderUser.authorTextView.setText("You");
        }
        if(holder.getItemViewType()==COLLOCUTOR){
            ViewHolderCollocutor viewHolderCollocutor = (ViewHolderCollocutor) holder;
            viewHolderCollocutor.messageTextView.setText(notifiableArrayList.getItem(position).message);
            viewHolderCollocutor.dateTextView.setText(notifiableArrayList.getItem(position).getDate());
            viewHolderCollocutor.authorTextView.setText(notifiableArrayList.getItem(position).sender);
        }
    }

    @Override
    public int getItemCount() {
        return notifiableArrayList.getSize();
    }
}
