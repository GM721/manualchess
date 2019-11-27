package manualchess.application.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waghstrategy.R;

import manualchess.application.BigBlackBox;
import manualchess.application.DialogActivity;

public class AuthorisedAdapter extends RecyclerView.Adapter {

    String[] authorisedUsers;
    BigBlackBox bigBlackBox;
    Context context;
    public AuthorisedAdapter(Context context, BigBlackBox bigBlackBox)
    {
        this.bigBlackBox = bigBlackBox;
        this.authorisedUsers = bigBlackBox.getCachedUsers();
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(@NonNull TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_view, parent, false);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = ((ViewHolder) holder);
        viewHolder.textView.setText(authorisedUsers[position]);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigBlackBox.prepareDialogSystem(((TextView)v).getText().toString());
                Intent intent = new Intent(context,DialogActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorisedUsers.length;
    }

    public void updateAdapter(){
        authorisedUsers = bigBlackBox.getCachedUsers();
        notifyDataSetChanged();
    }


}
