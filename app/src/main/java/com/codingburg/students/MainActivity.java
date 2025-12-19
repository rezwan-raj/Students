package com.codingburg.students;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.content.SharedPreferences;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare UI variables
    EditText etName, etRoll, etBatch, etSearchRoll;
    Button btnSubmit, btnSearch;
    TextView tvResult;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect this activity with activity_main.xml
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("StudentDB", MODE_PRIVATE);


        // Initialize UI components
        etName = findViewById(R.id.etName);
        etRoll = findViewById(R.id.etRoll);
        etBatch = findViewById(R.id.etBatch);
        etSearchRoll = findViewById(R.id.etSearchRoll);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSearch = findViewById(R.id.btnSearch);

        tvResult = findViewById(R.id.tvResult);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString().trim();
                String roll = etRoll.getText().toString().trim();
                String batch = etBatch.getText().toString().trim();

                if (name.equals("") || roll.equals("") || batch.equals("")) {
                    tvResult.setText("Please fill all fields");
                    return;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();

                String key = "student_" + roll;
                String value = name + "," + batch;

                editor.putString(key, value);
                editor.apply();

                tvResult.setText("Student Saved Successfully");

                etName.setText("");
                etRoll.setText("");
                etBatch.setText("");
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchRoll = etSearchRoll.getText().toString().trim();

                if (searchRoll.equals("")) {
                    tvResult.setText("Please enter roll to search");
                    return;
                }

                String key = "student_" + searchRoll;
                String savedData = sharedPreferences.getString(key, null);

                if (savedData == null) {
                    tvResult.setText("Student not found");
                } else {
                    String[] parts = savedData.split(",");

                    String name = parts[0];
                    String batch = parts[1];

                    tvResult.setText(
                            "Student Found\n" +
                                    "Name: " + name + "\n" +
                                    "Roll: " + searchRoll + "\n" +
                                    "Batch: " + batch
                    );
                }
            }
        });


    }


}
