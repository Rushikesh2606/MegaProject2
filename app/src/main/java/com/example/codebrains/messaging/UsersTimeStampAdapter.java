package com.example.codebrains.messaging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codebrains.R;
import com.example.codebrains.model.Connection;
import com.example.codebrains.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UsersTimeStampAdapter extends RecyclerView.Adapter<UsersTimeStampAdapter.ViewHolder> {

    private static final String TAG = "UsersTimeStampAdapter";
    private ArrayList<User> userList;
    private Context context;
    private FirebaseAuth auth;
    private OnUserClickListener onUserClickListener;
    private String userType;
    private String currentUserId;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UsersTimeStampAdapter(ArrayList<User> users, ArrayList<Connection> connections, Context context, OnUserClickListener listener) {
        this.context = context;
        this.onUserClickListener = listener;
        this.userList = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userType = sharedPreferences.getString("profession", "Unknown");

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Log.e(TAG, "Error: Current user is null.");
            return;
        }

        Log.d(TAG, "Current User Type: " + userType);
        Log.d(TAG, "Current User ID: " + currentUserId);
        Log.d(TAG, "Users List Size: " + users.size());

        filterUsers(users, connections);
    }

    /**
     * Filters users based on their type (Client/Freelancer)
     */
    private void filterUsers(ArrayList<User> users, ArrayList<Connection> connections) {
        if (connections == null) {
            Log.e(TAG, "Error: Connection list is null.");
            return;
        }

        if (userType.equals("Client")) {
            // Show only freelancers connected to this client
            for (User user : users) {
                Log.d("Checking Freelancer", user.getFirstName());
                if (isConnectedToFreelancer(user.getId(), connections)) {
                    userList.add(user);
                }
            }
        } else if (userType.equals("Freelancer")) {
            // Show only clients connected to this freelancer
            for (User user : users) {
                Log.d("Checking Client", user.getFirstName());
                if (isConnectedToClient(user.getId(), connections)) {
                    userList.add(user);
                }
            }
        } else {
            // If userType is unknown, show all users
            userList.addAll(users);
        }
    }

    /**
     * Check if the current client is connected to this freelancer
     */
    private boolean isConnectedToFreelancer(String freelancerId, ArrayList<Connection> connections) {
        for (Connection connection : connections) {
            if (connection.getClient().equals(currentUserId) && connection.getFreelancer().equals(freelancerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the current freelancer is connected to this client
     */
    private boolean isConnectedToClient(String clientId, ArrayList<Connection> connections) {
        for (Connection connection : connections) {
            if (connection.getFreelancer().equals(currentUserId) && connection.getClient().equals(clientId)) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_itme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        Log.d(TAG, "Binding User: " + user.getFirstName() + " " + user.getLastName());

        holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        holder.profilePic.setImageResource(R.drawable.man); // Default profile image

        long timestamp = user.getTimestamp();
        if (timestamp > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy h:mm a", Locale.getDefault());
            holder.userTimeStamp.setText(simpleDateFormat.format(new Date(timestamp)));
        } else {
            holder.userTimeStamp.setText("Hey, I am available!!");
        }

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
            Intent i=new Intent(context,ChatDetails.class);
            i.putExtra("userId",user.getId());
            i.putExtra("userName",user.getFirstName()+" "+user.getLastName());
            context.startActivity(i);
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView userName, userTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.title);
            userTimeStamp = itemView.findViewById(R.id.subtitle);
        }
    }
}
