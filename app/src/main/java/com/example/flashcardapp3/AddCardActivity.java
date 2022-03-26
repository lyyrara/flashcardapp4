package com.example.flashcardapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        String s1 = getIntent().getStringExtra("stringKey1");
        String s2 = getIntent().getStringExtra("stringKey2");
        ((EditText) findViewById(R.id.flashcard_question_edittext)).setText(s1);
        ((EditText) findViewById(R.id.flashcard_answer_edittext)).setText(s2);



        findViewById(R.id.flashcard_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.flashcard_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                String inputQuestion = ((EditText) findViewById(R.id.flashcard_question_edittext)).getText().toString();
                String inputAnswer = ((EditText) findViewById(R.id.flashcard_answer_edittext)).getText().toString();
                data.putExtra("question_key", inputQuestion);
                data.putExtra("answer_key", inputAnswer); // puts another string into the Intent, with the key as 'string2
                if (inputQuestion.equalsIgnoreCase("") || inputAnswer.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Must Enter Both Question and Answer", Toast.LENGTH_LONG).show();
                }else {
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish();
                }
            }
        });
    }
}