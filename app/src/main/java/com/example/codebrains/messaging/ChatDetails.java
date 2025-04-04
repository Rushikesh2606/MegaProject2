package com.example.codebrains.messaging;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.codebrains.R;
import com.example.codebrains.databinding.ActivityChatDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetails extends AppCompatActivity {
    private ActivityChatDetailsBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private ArrayList<MessageModel> messageModels;
    private ChatAdapter chatAdapter;
    private String receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        messageModels = new ArrayList<>();

        String senderId = auth.getUid();
        receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");

        if (senderId == null || receiverId == null || userName == null) {
            Toast.makeText(this, "Error: Missing user information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.appUserName.setText(userName);
        binding.appProfilePic.setImageResource(R.drawable.man);

        chatAdapter = new ChatAdapter(messageModels, this, receiverId);
        binding.appChatRecyclerView.setAdapter(chatAdapter);
        binding.appChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupChatListeners(senderId, receiverId);
        setupSendButton(senderId, receiverId);
    }

    private void setupChatListeners(String senderId, String receiverId) {
        String senderRoom = senderId + receiverId;
        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MessageModel model = dataSnapshot.getValue(MessageModel.class);
                            if (model != null) messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                        if (!messageModels.isEmpty()) {
                            binding.appChatRecyclerView.smoothScrollToPosition(messageModels.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatDetails.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSendButton(String senderId, String receiverId) {
        binding.appSendButton.setOnClickListener(v -> {
            String message = binding.appMessage.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            MessageModel model = new MessageModel(senderId, message, new Date().getTime());
            binding.appMessage.setText("");

            String senderRoom = senderId + receiverId;
            String receiverRoom = receiverId + senderId;

            DatabaseReference chatRef = database.getReference().child("chats");
            chatRef.child(senderRoom).push().setValue(model);
            chatRef.child(receiverRoom).push().setValue(model);
        });
    }
}