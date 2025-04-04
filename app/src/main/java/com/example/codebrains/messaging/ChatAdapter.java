package com.example.codebrains.messaging;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codebrains.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {
    private final ArrayList<MessageModel> messageModels;
    private final Context context;
    private final String receiverId;
    private final int SENDER_VIEW_TYPE = 1;
    private final int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String receiverId) {
        this.messageModels = messageModels;
        this.context = context;
        this.receiverId = receiverId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
        return new ReceiverViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return Objects.equals(messageModels.get(position).getUId(), FirebaseAuth.getInstance().getUid())
                ? SENDER_VIEW_TYPE : RECEIVER_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        String time = new SimpleDateFormat("h:mm a").format(new Date(messageModel.getTimeStamp()));

        if (holder.getItemViewType() == SENDER_VIEW_TYPE) {
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
            ((SenderViewHolder) holder).senderTimeStamp.setText(time);
        } else {
            ((ReceiverViewHolder) holder).receiverMsg.setText(messageModel.getMessage());
            ((ReceiverViewHolder) holder).receiverTime.setText(time);
        }

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteMessage(messageModel))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return false;
        });
    }

    private void deleteMessage(MessageModel messageModel) {
        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
        FirebaseDatabase.getInstance().getReference()
                .child("chats").child(senderRoom)
                .child(messageModel.getMessageId())
                .removeValue();
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTimeStamp;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.appSenderText);
            senderTimeStamp = itemView.findViewById(R.id.appSenderTime);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.appReceiverText);
            receiverTime = itemView.findViewById(R.id.appReceiverTime);
        }
    }
}