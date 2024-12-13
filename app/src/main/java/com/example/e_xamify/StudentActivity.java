package com.example.e_xamify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button dashboardButton;
    private Button enrollInstitutionButton;
    private Button enrollModuleButton;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        welcomeText = findViewById(R.id.welcomeText);
        dashboardButton = findViewById(R.id.dashboardButton);
        enrollInstitutionButton = findViewById(R.id.enrollInstitutionButton);
        enrollModuleButton = findViewById(R.id.enrollModuleButton);

        user_id = getIntent().getIntExtra("user_id", -1);
        if (user_id == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        welcomeText.setText("Welcome, Student!");

        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, StudentDashboardActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });

        enrollInstitutionButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, StudentEnrollmentActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });

        enrollModuleButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, StudentEnrollModuleActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
