package com.example.codebrains;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_profile_client extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private EditText firstName, lastName, email, contactNo, password, dob;
    private Spinner countrySpinner;
    private CircleImageView profileImage;
    private Button btnUploadPhoto, btnSave;
    ArrayList<String> countryList;
    ProgressBar progress;
    ScrollView scroll;

    private static final int CAMERA_REQ = 100, GALLERY_REQ = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile_client, container, false);
        initialize(view);

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }

        userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);
        scroll.setVisibility(View.GONE);

        fetchClientData();

        btnUploadPhoto.setOnClickListener(v -> selectPic());
        btnSave.setOnClickListener(v -> saveProfile());

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
        profileImage = view.findViewById(R.id.profile_image);
        btnUploadPhoto = view.findViewById(R.id.btn_upload_photo);
        btnSave = view.findViewById(R.id.btn_save);
        progress=view.findViewById(R.id.progress_bar);
scroll= view.findViewById(R.id.scroll);
        countryList = new ArrayList<>(Arrays.asList(
                "Afghanistan", "Albania", "Algeria", "Andorra", "Angola",
                "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria",
                "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
                "Belarus", "Belgium", "Belize", "Benin", "Bhutan",
                "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei",
                "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia",
                "Cameroon", "Canada", "Central African Republic", "Chad", "Chile",
                "China", "Colombia", "Comoros", "Congo (Congo-Brazzaville)", "Costa Rica",
                "Croatia", "Cuba", "Cyprus", "Czechia (Czech Republic)", "Denmark",
                "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt",
                "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini (fmr. \"Swaziland\")",
                "Ethiopia", "Fiji", "Finland", "France", "Gabon",
                "Gambia", "Georgia", "Germany", "Ghana", "Greece",
                "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",
                "Haiti", "Holy See", "Honduras", "Hungary", "Iceland",
                "India", "Indonesia", "Iran", "Iraq", "Ireland",
                "Israel", "Italy", "Jamaica", "Japan", "Jordan",
                "Kazakhstan", "Kenya", "Kiribati", "Korea (North)", "Korea (South)",
                "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon",
                "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania",
                "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives",
                "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius",
                "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
                "Montenegro", "Morocco", "Mozambique", "Myanmar (formerly Burma)", "Namibia",
                "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua",
                "Niger", "Nigeria", "North Macedonia (formerly Macedonia)", "Norway", "Oman",
                "Pakistan", "Palau", "Palestine State", "Panama", "Papua New Guinea",
                "Paraguay", "Peru", "Philippines", "Poland", "Portugal",
                "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis",
                "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe",
                "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone",
                "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia",
                "South Africa", "South Sudan", "Spain", "Sri Lanka", "Sudan",
                "Suriname", "Sweden", "Switzerland", "Syria", "Tajikistan",
                "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga",
                "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu",
                "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America",
                "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam",
                "Yemen", "Zambia", "Zimbabwe"
        ));

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, countryList);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);
    }

    private void fetchClientData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("FirebaseData", snapshot.toString());

                // Map data to Client object
                client client = snapshot.getValue(client.class);

                if (client != null && getActivity() != null) {
                    firstName.setText(getValidString(client.getFirstName()));
                    lastName.setText(getValidString(client.getLastName()));
                    email.setText(getValidString(client.getEmail()));
                    contactNo.setText(getValidString(client.getContactNo()));
                    dob.setText(getValidString(client.getDob()));
                    password.setText(getValidString(client.getPassword()));



                    int countryIndex = countryList.indexOf(client.getCountry());
                    if (countryIndex >= 0) {
                        countrySpinner.setSelection(countryIndex);
                    }
                    scroll.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Profile data loaded successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getValidString(String value) {
        return value != null ? value : "N/A";
    }

    private void saveProfile() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            HashMap<String, Object> updatedData = new HashMap<>();
            updatedData.put("firstName", firstName.getText().toString());
            updatedData.put("lastName", lastName.getText().toString());
            updatedData.put("email", email.getText().toString());
            updatedData.put("contactNo", contactNo.getText().toString());
            updatedData.put("dob", dob.getText().toString());
            updatedData.put("password", password.getText().toString());
            updatedData.put("country", countrySpinner.getSelectedItem().toString());

            userRef.updateChildren(updatedData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("FirebaseUpdate", "Profile updated successfully");
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Exception e = task.getException();
                    Log.e("FirebaseUpdate", "Update failed", e);
                    Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void selectPic() {
        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
        ad.setIcon(R.drawable.baseline_account_circle_24);
        ad.setTitle("Profile Picture ");
        ad.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQ);
            }
        });
        ad.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, CAMERA_REQ);
            }
        });
        ad.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQ) {
                // Get the image from the camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(photo);  // Convert Bitmap to Uri
            } else if (requestCode == GALLERY_REQ) {
                // Get the image from the gallery
                Uri selectedImage = data.getData();
                profileImage.setImageURI(selectedImage);
            }
        }
    }
}