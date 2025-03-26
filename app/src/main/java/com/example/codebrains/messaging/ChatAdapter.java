package com.example.codebrains.messaging;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.messaging.MessageModel;
import com.example.codebrains.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<MessageModel> messageModels;
    Context ctx;
    String receiverId;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVE_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModel, Context ctx, String receiverId) {
        this.messageModels = messageModel;
        this.ctx = ctx;
        this.receiverId = receiverId;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context ctx) {
        this.messageModels = messageModels;
        this.ctx = ctx;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(ctx).inflate(R.layout.sample_sender,parent,false);
            return  new SenderViewHolder(view);
        }
        View view = LayoutInflater.from(ctx).inflate(R.layout.sample_receiver,parent,false);
        return  new RecieverViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        String userUid = FirebaseAuth.getInstance().getUid();
        if (Objects.requireNonNull(messageModels.get(position)).getUId().equals(userUid)){
            return SENDER_VIEW_TYPE;
        }
        return RECEIVE_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ctx)
                        .setTitle("Delete")
                        .setMessage("Are you sure want to delete message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });
        if (holder.getClass() == SenderViewHolder.class){
            assert messageModel != null;
            ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
            Date date = new Date(messageModel.getTimeStamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
            String strDate = simpleDateFormat.format(date);
            ((SenderViewHolder)holder).senderTimeStamp.setText(strDate);
        }
        else {
            assert messageModel != null;
            ((RecieverViewHolder)holder).receiverMsg.setText(messageModel.getMessage());
            Date date = new Date(messageModel.getTimeStamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
            String strDate = simpleDateFormat.format(date);
            ((RecieverViewHolder)holder).receiverTime.setText(strDate);
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg,receiverTime;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.appReceiverText);
            receiverTime = itemView.findViewById(R.id.appReceiverTime);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTimeStamp;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.appSenderText);
            senderTimeStamp = itemView.findViewById(R.id.appSenderTime);
        }
    }
}
