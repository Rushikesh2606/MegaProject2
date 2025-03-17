package com.example.codebrains;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class signup extends AppCompatActivity {

    EditText first_name,last_name,email,password;
    AutoCompleteTextView country;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button b=findViewById(R.id.sign_up_button);
       last_name=findViewById(R.id.last_name);
        email=findViewById(R.id.email);
        country=findViewById(R.id.country);

        first_name=findViewById(R.id.first_name);
        ArrayAdapter<String> aa= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,countryList);
        country.setAdapter(aa);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String surname=last_name.getText().toString();
                String name=first_name.getText().toString();
                String Email=email.getText().toString();
                String Country=country.getText().toString();

                if (TextUtils.isEmpty(surname)) {
                    last_name.setError("Surname cannot be empty");
                } else if (TextUtils.isEmpty(name)) {
                    first_name.setError("Name cannot be empty");
                } else if (TextUtils.isEmpty(Email)) {
                    email.setError("Email cannot be empty");
                } else if (!Email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    email.setError("Enter a valid email");
                }  else if (TextUtils.isEmpty(Country)) {
                    Toast.makeText(getApplicationContext(), "Select Country..!!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(signup.this, register.class);
                    intent.putExtra("last_name", surname);
                    intent.putExtra("first_name", name);
                    intent.putExtra("email", Email);
                    intent.putExtra("country", Country);

                    // Start the register activity
                    startActivity(intent);
                }


            }

        });
    }
}