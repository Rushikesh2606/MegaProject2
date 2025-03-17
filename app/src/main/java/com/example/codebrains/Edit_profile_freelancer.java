package com.example.codebrains;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_profile_freelancer extends Fragment {


    public Edit_profile_freelancer() {
        // Required empty public constructor
    }

    private EditText first_name,last_name,password, email, mobile,experience, gender, dob, skills,tools, tagline, desc;
    private CircleImageView profileImage;
    Button edit_profile;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    Button btn_upload_photo,btn_save;
    FirebaseUser currentUser;

    AutoCompleteTextView country,gender_spinner;
    ArrayList<String> countryList = new ArrayList<>(Arrays.asList(
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
    private static final int CAMERA_REQ = 100, GALLERY_REQ = 200;

ArrayList<String> gender_array=new ArrayList<>(Arrays.asList("Male","Female"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_edit_profile_freelancer, container, false);
        intitialize(view);
        ArrayAdapter<String> aa=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,countryList);
        country.setAdapter(aa);

        ArrayAdapter<String> gaa=new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, gender_array);
        gender_spinner.setAdapter(gaa);

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        };
        userRef = FirebaseDatabase.getInstance().getReference("freelancer").child(currentUserId);

        fetchDeveloperData();

        btn_upload_photo.setOnClickListener(View -> selectPic());
        btn_save.setOnClickListener(View->saveProfile());

        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_homepage_developer,new developer_profile());
            }
        });
    }

    private void saveProfile() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            HashMap<String, Object> updatedData = new HashMap<>();
            updatedData.put("firstName", first_name.getText().toString());
            updatedData.put("email", email.getText().toString());
            updatedData.put("phone", mobile.getText().toString());

            // Check if gender is valid
            String selectedGender = gender_spinner.getText().toString();
            if (gender_array.contains(selectedGender)) {
                updatedData.put("gender", selectedGender);
            } else {
                Toast.makeText(getContext(), "Please select a valid gender", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if country is valid
            String selectedCountry = country.getText().toString();
            if (countryList.contains(selectedCountry)) {
                updatedData.put("country", selectedCountry);
            } else {
                Toast.makeText(getContext(), "Please select a valid country", Toast.LENGTH_SHORT).show();
                return;
            }

            updatedData.put("dob", dob.getText().toString());
            updatedData.put("desc", desc.getText().toString());
            updatedData.put("tagLine", tagline.getText().toString());
            updatedData.put("lastName", last_name.getText().toString());
            updatedData.put("tools", tools.getText().toString());
            updatedData.put("skills", skills.getText().toString());
            updatedData.put("yearsOfExperience", experience.getText().toString());
            updatedData.put("password", password.getText().toString());

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

            }
        }
    }
    private Uri getImageUri(Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void intitialize(View view) {

        first_name = view.findViewById(R.id.first_name);
        last_name = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        mobile = view.findViewById(R.id.contact_no);
        gender_spinner = view.findViewById(R.id.spinner);
        dob = view.findViewById(R.id.dob);
        skills = view.findViewById(R.id.skills);
        tagline = view.findViewById(R.id.tagline);
        profileImage = view.findViewById(R.id.profile_image);
        password = view.findViewById(R.id.password);
        desc=view.findViewById(R.id.description);
        tools=view.findViewById(R.id.tools_expertise);
        experience=view.findViewById(R.id.experience);
        country=view.findViewById(R.id.country);
        btn_upload_photo=view.findViewById(R.id.btn_upload_photo);
        profileImage=view.findViewById(R.id.profileImage);
        btn_save=view.findViewById(R.id.btn_save);
    }
    private void fetchDeveloperData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
                    return;
                }

                Log.d("FirebaseData", snapshot.toString());

                // Map data to Freelancer object
                freelancer freelancer = snapshot.getValue(freelancer.class);

                if (freelancer != null && getActivity() != null) {
                    first_name.setText(getValidString(freelancer.getFirstName()));
                    last_name.setText(getValidString(freelancer.getLastName()));
                    email.setText(getValidString(freelancer.getEmail()));
                    mobile.setText(getValidString(freelancer.getContactNo()));
                    skills.setText( getValidString(freelancer.getSkills()));
                    tools.setText(getValidString(freelancer.getTools()));
                    tagline.setText( getValidString(freelancer.getTagLine()));
                    desc.setText(getValidString(freelancer.getDesc()));
                    dob.setText(getValidString(freelancer.getDob()));
                    experience.setText(getValidString(freelancer.getYearsOfExperience()));
//                    gender_spinner.setText(getValidString(freelancer.getGender()));
//                    country.setText(getValidString(freelancer.getCountry()));

                    Toast.makeText(getContext(), "Profile data loaded successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getValidString(String value) {
        return value != null ? value : "N/A";
    }


}