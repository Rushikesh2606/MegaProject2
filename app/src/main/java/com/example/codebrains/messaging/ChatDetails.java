package com.example.codebrains.messaging;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.codebrains.R;
import com.example.codebrains.databinding.ActivityChatDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetails extends AppCompatActivity {

    private ActivityChatDetailsBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Context context;
    private static final String DATABASE_CHAT = "chats";
    private static final String TAG = "ChatDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(this, "Authentication failed. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        // String imageBase64 = getIntent().getStringExtra("imageBase64"); // Commented out

        if (receiveId == null || userName == null) {
            Toast.makeText(this, "User information is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.appUserName.setText(userName);

        // Commented out Base64 image handling, setting default image
        /*
        try {
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                binding.appProfilePic.setImageBitmap(decodedBitmap);
            } else {
                binding.appProfilePic.setImageResource(R.drawable.baseline_account_circle_24);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error decoding image", e);
            binding.appProfilePic.setImageResource(R.drawable.baseline_account_circle_24);
        }
        */

        // Set default image from drawable
        binding.appProfilePic.setImageResource(R.drawable.man);

        binding.appBackArrow.setOnClickListener(view -> {
            Intent intent = new Intent(this, Chat.class);
            startActivity(intent);
            finish();
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiveId);
        binding.appChatRecyclerView.setAdapter(chatAdapter);
        binding.appChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final String senderRoom = userId + receiveId;
        final String receiverRoom = receiveId + userId;

        database.getReference().child(DATABASE_CHAT)
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MessageModel model = dataSnapshot.getValue(MessageModel.class);
                            if (model != null) {
                                messageModels.add(model);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();

                        if (!messageModels.isEmpty()) {
                            int position = messageModels.size() - 1;
                            binding.appChatRecyclerView.smoothScrollToPosition(position);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database Error: " + error.getMessage());
                        Toast.makeText(ChatDetails.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
                    }
                });

        binding.appSendButton.setOnClickListener(view -> {
            String sendMsg = binding.appMessage.getText().toString().trim();
            if (sendMsg.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            final MessageModel model = new MessageModel(userId, sendMsg);
            model.setTimeStamp(new Date().getTime());
            binding.appMessage.setText("");

            database.getReference().child(DATABASE_CHAT)
                    .child(senderRoom)
                    .push()
                    .setValue(model)
                    .addOnSuccessListener(unused -> database.getReference().child(DATABASE_CHAT)
                            .child(receiverRoom)
                            .push()
                            .setValue(model)
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to send message to receiver: " + e.getMessage());
                                Toast.makeText(ChatDetails.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to send message: " + e.getMessage());
                        Toast.makeText(ChatDetails.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
