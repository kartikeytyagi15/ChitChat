package info.androidhive.chitchat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import info.androidhive.chitchat.Models.MessageModel;
import info.androidhive.chitchat.R;

public class ChatAdapter extends RecyclerView.Adapter{

    ArrayList<MessageModel> messageModels;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    String receiverId;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String receiverId) {
        this.messageModels = messageModels;
        this.context = context;
        this.receiverId = receiverId;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete message")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid()
                                        + receiverId;
                                database.getReference().child("Chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                return false;
            }
        });

        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
        }
        else{
            ((ReceiverViewHolder)holder).receiverMsg.setText(messageModel.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
            return SENDER_VIEW_TYPE;
        else return RECEIVER_VIEW_TYPE;
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg, receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverTv);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderTv);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }

}
