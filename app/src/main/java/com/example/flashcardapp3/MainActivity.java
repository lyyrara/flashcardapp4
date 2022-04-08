package com.example.flashcardapp3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView flashcardQuestion;
    TextView flashcardAnswer;
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;
    CountDownTimer countDownTimer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        flashcardQuestion = findViewById(R.id.flashcard_question);
        flashcardAnswer = findViewById(R.id.flashcard_answer);

        View answerSideView = findViewById(R.id.flashcard_answer);
        View questionSideView = findViewById(R.id.flashcard_question);

        countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.timer)).setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
            }
        };


        flashcardAnswer.setVisibility(View.INVISIBLE);

        flashcardQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardQuestion.setVisibility(View.INVISIBLE);
                flashcardAnswer.setVisibility(View.VISIBLE);

// get the center for the clipping circle
                int cx = answerSideView.getWidth() / 2;
                int cy = answerSideView.getHeight() / 2;

// get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

// create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);

// hide the question and show the answer to prepare for playing the animation!
                questionSideView.setVisibility(View.INVISIBLE);
                answerSideView.setVisibility(View.VISIBLE);

                anim.setDuration(3000);
                anim.start();
            }
        });

        flashcardAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardQuestion.setVisibility(View.VISIBLE);
                flashcardAnswer.setVisibility(View.INVISIBLE);
            }
        });


        findViewById(R.id.flashcard_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                //MainActivity.this.startActivity(intent);
                startActivityForResult(intent, 100);
            }
        });

        findViewById(R.id.flashcard_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                intent.putExtra("stringKey1", flashcardQuestion.getText());
                intent.putExtra("stringKey2", flashcardAnswer.getText());
                //MainActivity.this.startActivity(intent);
                startActivityForResult(intent, 100);
            }
        });

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();



        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (allFlashcards != null && allFlashcards.size() > 0) {
                    ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
                }
                if (allFlashcards.size() == 0)
                    return;
                // advance our pointer index so we can show the next card
                currentCardDisplayedIndex++;
                // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                if(currentCardDisplayedIndex >= allFlashcards.size()) {
                    Snackbar.make(flashcardQuestion,
                            "You've reached the end of the cards, going back to start.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    currentCardDisplayedIndex = 0;
                }


                // set the question and answer TextViews with data from the database


                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        findViewById(R.id.flashcard_question).startAnimation(rightInAnim);

                        allFlashcards = flashcardDatabase.getAllCards();
                        Flashcard flashcard = allFlashcards.get(currentCardDisplayedIndex);

                        ((TextView) findViewById(R.id.flashcard_question)).setText(flashcard.getAnswer());
                        ((TextView) findViewById(R.id.flashcard_answer)).setText(flashcard.getQuestion());

                        flashcardQuestion.setVisibility(View.VISIBLE);
                        flashcardAnswer.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });
                findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);
            }


        });

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());
                currentCardDisplayedIndex--;
                allFlashcards = flashcardDatabase.getAllCards();
                Flashcard flashcard = allFlashcards.get(currentCardDisplayedIndex);

                ((TextView) findViewById(R.id.flashcard_question)).setText(flashcard.getAnswer());
                ((TextView) findViewById(R.id.flashcard_answer)).setText(flashcard.getQuestion());

                if(currentCardDisplayedIndex == 0) {
                    ((TextView) findViewById(R.id.flashcard_question)).setText("");
                    ((TextView) findViewById(R.id.flashcard_answer)).setText("");
                }
                if(currentCardDisplayedIndex < 0) {
                    Snackbar.make(flashcardQuestion,
                            "You've reached the end of the cards, going back to start.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    currentCardDisplayedIndex = 0;
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                String questionString = data.getExtras().getString("question_key");
                String answerString = data.getExtras().getString("answer_key");
                flashcardQuestion.setText(questionString);
                flashcardAnswer.setText(answerString);
                Snackbar.make(findViewById(R.id.flashcard_question),
                        "Card successfully created!",
                       Snackbar.LENGTH_SHORT)
                       .show();
                flashcardDatabase.insertCard(new Flashcard(questionString, answerString));
                allFlashcards = flashcardDatabase.getAllCards();
            }
        }

    }
}