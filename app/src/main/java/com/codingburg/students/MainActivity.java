package com.codingburg.students;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
  /*      etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("saikat");
            }
        });*/

        listViewStudents = findViewById(R.id.listViewStudents);

        // ---------------- SUBMIT (SAVE / UPDATE) ----------------
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

                Toast.makeText(MainActivity.this, "Student Saved / Updated", Toast.LENGTH_SHORT).show();

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

                ArrayList<String> singleResult = new ArrayList<>();
                singleResult.add(
                        "Roll: " + searchRoll + "\n" +
                                "Name: " + parts[0] + "\n" +
                                "Batch: " + parts[1]
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

                for (String key : sharedPreferences.getAll().keySet()) {
                    if (key.startsWith("student_")) {
                        studentKeys.add(key);
                    }
                }

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

        // ---------------- CLICK STUDENT (UPDATE / DELETE) ----------------
        listViewStudents.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String item = parent.getItemAtPosition(position).toString();
                        String rollLine = item.split("\n")[0]; // Roll: X
                        String roll = rollLine.replace("Roll: ", "").trim();

                        showUpdateDeleteDialog(roll);
                    }
                }
        );
    }

    // ---------------- DIALOG ----------------
    private void showUpdateDeleteDialog(final String roll) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Choose Action");

        builder.setItems(
                new String[]{"Update", "Delete", "Cancel"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            loadStudentForUpdate(roll);
                        }
                        else if (which == 1) {
                            deleteStudent(roll);
                        }
                        else {
                            dialog.dismiss();
                        }
                    }
                }
        );

        builder.show();
    }

    // ---------------- LOAD FOR UPDATE ----------------
    private void loadStudentForUpdate(String roll) {

        String key = "student_" + roll;
        String value = sharedPreferences.getString(key, null);

        if (value == null) {
            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] parts = value.split(",");

        etName.setText(parts[0]);
        etRoll.setText(roll);
        etBatch.setText(parts[1]);

        Toast.makeText(
                this,
                "Edit and press Submit to update",
                Toast.LENGTH_LONG
        ).show();
    }

    // ---------------- DELETE ----------------
    private void deleteStudent(String roll) {

        sharedPreferences.edit()
                .remove("student_" + roll)
                .apply();

        Toast.makeText(
                this,
                "Student deleted",
                Toast.LENGTH_SHORT
        ).show();

        listViewStudents.setAdapter(null);
    }
}
