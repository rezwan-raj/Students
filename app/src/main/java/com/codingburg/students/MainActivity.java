package com.codingburg.students;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    // UI
    EditText etName, etRoll, etBatch, etSearchRoll;
    Button btnSubmit, btnSearch, btnShowAll;
    ListView listViewStudents;

    // Storage
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences
        sharedPreferences = getSharedPreferences("StudentDB", MODE_PRIVATE);

        // Init UI
        etName = findViewById(R.id.etName);
        etRoll = findViewById(R.id.etRoll);
        etBatch = findViewById(R.id.etBatch);
        etSearchRoll = findViewById(R.id.etSearchRoll);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSearch = findViewById(R.id.btnSearch);
        btnShowAll = findViewById(R.id.btnShowAll);

        listViewStudents = findViewById(R.id.listViewStudents);

        // ---------------- SUBMIT ----------------
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString().trim();
                String roll = etRoll.getText().toString().trim();
                String batch = etBatch.getText().toString().trim();

                if (name.equals("") || roll.equals("") || batch.equals("")) {
                    Toast.makeText(MainActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String key = "student_" + roll;
                String value = name + "," + batch;

                sharedPreferences.edit()
                        .putString(key, value)
                        .apply();

                Toast.makeText(MainActivity.this,
                        "Student Saved Successfully",
                        Toast.LENGTH_SHORT).show();

                etName.setText("");
                etRoll.setText("");
                etBatch.setText("");
            }
        });

        // ---------------- SEARCH ----------------
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchRoll = etSearchRoll.getText().toString().trim();

                if (searchRoll.equals("")) {
                    Toast.makeText(MainActivity.this,
                            "Please enter roll to search",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String key = "student_" + searchRoll;
                String savedData = sharedPreferences.getString(key, null);

                if (savedData == null) {
                    Toast.makeText(MainActivity.this,
                            "Student not found",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] parts = savedData.split(",");
                String name = parts[0];
                String batch = parts[1];

                ArrayList<String> singleResult = new ArrayList<>();
                singleResult.add(
                        "Roll: " + searchRoll + "\n" +
                                "Name: " + name + "\n" +
                                "Batch: " + batch
                );

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                                MainActivity.this,
                                android.R.layout.simple_list_item_1,
                                singleResult
                        );

                listViewStudents.setAdapter(adapter);
            }
        });

        // ---------------- SHOW ALL (ROLL-WISE) ----------------
        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> studentKeys = new ArrayList<>();

                // collect keys
                for (String key : sharedPreferences.getAll().keySet()) {
                    if (key.startsWith("student_")) {
                        studentKeys.add(key);
                    }
                }

                // sort by roll number
                Collections.sort(studentKeys, new Comparator<String>() {
                    @Override
                    public int compare(String k1, String k2) {
                        int r1 = Integer.parseInt(k1.replace("student_", ""));
                        int r2 = Integer.parseInt(k2.replace("student_", ""));
                        return r1 - r2;
                    }
                });

                ArrayList<String> displayList = new ArrayList<>();

                for (int i = 0; i < studentKeys.size(); i++) {
                    String key = studentKeys.get(i);
                    String roll = key.replace("student_", "");

                    String value = sharedPreferences.getString(key, "");
                    String[] parts = value.split(",");

                    displayList.add(
                            "Roll: " + roll + "\n" +
                                    "Name: " + parts[0] + "\n" +
                                    "Batch: " + parts[1]
                    );
                }

                if (displayList.size() == 0) {
                    Toast.makeText(MainActivity.this,
                            "No students found",
                            Toast.LENGTH_SHORT).show();
                    listViewStudents.setAdapter(null);
                } else {
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    MainActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    displayList
                            );
                    listViewStudents.setAdapter(adapter);
                }
            }
        });
    }
}
