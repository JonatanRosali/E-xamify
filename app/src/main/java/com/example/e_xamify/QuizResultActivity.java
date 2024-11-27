package com.example.e_xamify;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuizResultActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private LinearLayout resultLayout;
    private Button toggleCorrectAnswersButton;
    private boolean showCorrectAnswers = false;
    private int assignmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        dbHelper = new DatabaseHelper(this); // Initialize dbHelper
        assignmentId = getIntent().getIntExtra("assignmentId", -1);

        if (assignmentId == -1) {
            Log.e("QuizResultActivity", "Assignment ID not received or invalid");
            Toast.makeText(this, "Error loading quiz result.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadQuizResults();

        toggleCorrectAnswersButton.setOnClickListener(v -> {
            showCorrectAnswers = !showCorrectAnswers;
            toggleCorrectAnswersButton.setText(showCorrectAnswers ? "Hide Correct Answers" : "Show Correct Answers");
            loadQuizResults(); // Reload results to update view
        });
    }

    private void initializeViews() {
        resultLayout = findViewById(R.id.resultLayout);
        toggleCorrectAnswersButton = findViewById(R.id.toggleCorrectAnswersButton);

        // Ensure the button is visible
        toggleCorrectAnswersButton.setVisibility(View.VISIBLE);
    }

    private void loadQuizResults() {
        resultLayout.removeAllViews();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT q.question_text, m.optionA, m.optionB, m.optionC, m.optionD, m.correctOption, " +
                        "s.selected_option_id, s.is_correct " +
                        "FROM question q " +
                        "JOIN mcq m ON q.question_id = m.question_id " +
                        "JOIN quiz_submission s ON q.question_id = s.question_id " +
                        "WHERE s.assignment_id = ?",
                new String[]{String.valueOf(assignmentId)}
        );

        if (cursor.moveToFirst()) {
            do {
                String questionText = cursor.getString(0);
                String optionA = cursor.getString(1);
                String optionB = cursor.getString(2);
                String optionC = cursor.getString(3);
                String optionD = cursor.getString(4);
                int correctOption = cursor.getInt(5);
                int selectedOption = cursor.getInt(6);
                boolean isCorrect = cursor.getInt(7) == 1;

                addQuestionToLayout(questionText, optionA, optionB, optionC, optionD, correctOption, selectedOption, isCorrect);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No quiz results found.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void addQuestionToLayout(String questionText, String optionA, String optionB, String optionC,
                                     String optionD, int correctOption, int selectedOption, boolean isCorrect) {
        LinearLayout questionContainer = new LinearLayout(this);
        questionContainer.setOrientation(LinearLayout.VERTICAL);
        questionContainer.setPadding(16, 16, 16, 16);
        questionContainer.setBackgroundResource(android.R.color.background_light);

        // Display the question
        TextView questionView = new TextView(this);
        questionView.setText(questionText);
        questionView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        questionView.setPadding(0, 0, 0, 8);
        questionContainer.addView(questionView);

        // Adjust correctOption to align with the logic in saveSelectedOption
        int adjustedCorrectOption = correctOption - 1; // Assuming correctOption starts from 1

        // Display the options
        String[] options = {optionA, optionB, optionC, optionD};
        for (int i = 0; i < options.length; i++) {
            TextView optionView = new TextView(this);
            optionView.setText((i + 1) + ". " + options[i]);
            optionView.setTextAppearance(this, android.R.style.TextAppearance_Small);

            // Highlight the selected answer
            if (i == selectedOption) {
                optionView.setTextColor(isCorrect ? getResources().getColor(android.R.color.holo_green_dark)
                        : getResources().getColor(android.R.color.holo_red_dark));
                optionView.append(isCorrect ? " (Correct)" : " (Incorrect)");
            }

            // Highlight the correct answer if it hasn't already been marked
            if (showCorrectAnswers && i == adjustedCorrectOption && i != selectedOption) {
                optionView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                optionView.append(" (Correct Answer)");
            }

            optionView.setPadding(0, 4, 0, 4);
            questionContainer.addView(optionView);
        }

        // Add a divider for better separation
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        ));
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        questionContainer.addView(divider);

        resultLayout.addView(questionContainer);
    }

}
