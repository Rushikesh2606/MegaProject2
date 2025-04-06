package com.example.codebrains;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_profile_client extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private EditText firstName, lastName, email, contactNo, password, dob;
    private Spinner countrySpinner;
    private CircleImageView profileImage;
    private Button btnUploadPhoto, btnSave;
    private ProgressBar progress;
    private ScrollView scroll;

    private static final int CAMERA_REQ = 100, GALLERY_REQ = 200;
    private Uri selectedImageUri;
    private Bitmap selectedBitmap;
    private boolean imageChanged = false;

    private ArrayList<String> countryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile_client, container, false);
        initialize(view);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }

        String currentUserId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);

        scroll.setVisibility(View.GONE);
        fetchClientData();

        btnUploadPhoto.setOnClickListener(v -> selectPic());
        btnSave.setOnClickListener(v -> {
            if (imageChanged) {
                uploadProfileImageAndSave();
            } else {
                saveProfile(null);
            }
        });

        return view;
    }

    private void initialize(View view) {
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        contactNo = view.findViewById(R.id.contact_no);
        password = view.findViewById(R.id.password);
        dob = view.findViewById(R.id.dob);
        countrySpinner = view.findViewById(R.id.country_spinner);
        profileImage = view.findViewById(R.id.client_profile_image);
        btnUploadPhoto = view.findViewById(R.id.btn_upload_photo);
        btnSave = view.findViewById(R.id.btn_save);
        progress = view.findViewById(R.id.progress_bar);
        scroll = view.findViewById(R.id.scroll);

        countryList = new ArrayList<>(Arrays.asList(Locale.getISOCountries()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, countryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
    }

    private void fetchClientData() {
        progress.setVisibility(View.VISIBLE);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                client client = snapshot.getValue(client.class);

                if (client != null && getActivity() != null) {
                    firstName.setText(getValidString(client.getFirstName()));
                    lastName.setText(getValidString(client.getLastName()));
                    email.setText(getValidString(client.getEmail()));
                    contactNo.setText(getValidString(client.getContactNo()));
                    dob.setText(getValidString(client.getDob()));
                    password.setText(getValidString(client.getPassword()));

                    int index = countryList.indexOf(client.getCountry());
                    if (index >= 0) countrySpinner.setSelection(index);

                    // Load base64 image if exists
                    if (client.getProfileImage() != null && !client.getProfileImage().isEmpty()) {
                        try {
                            byte[] decodedBytes = android.util.Base64.decode(client.getProfileImage(), android.util.Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            profileImage.setImageBitmap(decodedBitmap);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    scroll.setVisibility(View.VISIBLE);
                }

                progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectPic() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Profile Picture");
        dialog.setIcon(R.drawable.baseline_account_circle_24);
        dialog.setNegativeButton("Gallery", (d, i) -> {
            Intent pick = new Intent(Intent.ACTION_PICK);
            pick.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, GALLERY_REQ);
        });
        dialog.setPositiveButton("Camera", (d, i) -> {
            Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(capture, CAMERA_REQ);
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageChanged = true;
            if (requestCode == GALLERY_REQ) {
                selectedImageUri = data.getData();
                profileImage.setImageURI(selectedImageUri);
            } else if (requestCode == CAMERA_REQ) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(selectedBitmap);
            }
        }
    }

    private void uploadProfileImageAndSave() {
        progress.setVisibility(View.VISIBLE);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (selectedBitmap != null) {
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            } else if (selectedImageUri != null) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            byte[] imageBytes = baos.toByteArray();
            String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            saveProfile(base64Image);

        } catch (Exception e) {
            progress.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Image processing failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveProfile(@Nullable String imageBase64) {
        String uid = currentUser.getUid();
        HashMap<String, Object> map = new HashMap<>();
        map.put("firstName", firstName.getText().toString());
        map.put("lastName", lastName.getText().toString());
        map.put("email", email.getText().toString());
        map.put("contactNo", contactNo.getText().toString());
        map.put("dob", dob.getText().toString());
        map.put("password", password.getText().toString());
        map.put("country", countrySpinner.getSelectedItem().toString());

        if (imageBase64 != null) {
            map.put("profileImage", imageBase64);
        }

        userRef.updateChildren(map).addOnCompleteListener(task -> {
            progress.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getValidString(String value) {
        return value != null ? value : "";
    }
}
