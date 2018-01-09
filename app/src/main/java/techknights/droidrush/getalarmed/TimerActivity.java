package techknights.droidrush.getalarmed;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

public class TimerActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {

    public CountDownTimer countDownTimer;
    public boolean timerHasStarted = false;
    public TextView text;
    public final long interval = 1 * 1000;
    EditText hours;
    EditText minutes;
    EditText seconds;
    Button startButton;
    public String hr;
    public String minute;
    public String sec;
    public long timeValHour;
    public long timeValMinute;
    public long timeValSecond;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


        startButton = (Button)findViewById(R.id.start_timer);
        hours = (EditText)findViewById(R.id.TimerHour);
        minutes = (EditText)findViewById(R.id.TimerMinute);
        seconds = (EditText)findViewById(R.id.TimerSecond);
        text = (TextView) this.findViewById(R.id.status);
        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.US);


        startButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                // get the values from editText and storing into long variable
                timeValHour = Long.parseLong(hours.getText().toString());
                timeValMinute = Long.parseLong(minutes.getText().toString());
                timeValSecond = Long.parseLong(seconds.getText().toString());

                if(timeValHour==0 && timeValMinute==0 && timeValSecond==0){
                    text.setText("Set the Timer!!!");
                }
                else {
                    long timeVal = (timeValHour * 60 * 60 * 1000) + (timeValMinute * 60 * 1000) + (timeValSecond * 1000);

                    hours.setEnabled(false);
                    minutes.setEnabled(false);
                    seconds.setEnabled(false);

                    if (!timerHasStarted) {


                        countDownTimer = new CountDownTimer(timeVal, interval) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                long millis = millisUntilFinished;
                                hr = String.format("%02d", TimeUnit.MILLISECONDS.toHours(millis));
                                minute = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
                                sec = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                                text.setText(hr + ":" + minute + ":" + sec);
                            }

                            @Override
                            public void onFinish() {
                                text.setText("Time's up!!!");
                                startButton.setText("START TIMER");
                                hours.setText("00");
                                minutes.setText("00");
                                seconds.setText("00");
                                hours.setEnabled(true);
                                minutes.setEnabled(true);
                                seconds.setEnabled(true);
                                tts.speak("Time's Up", QUEUE_FLUSH, null);
                            }
                        }.start();

                        timerHasStarted = true;
                        startButton.setText("PAUSE");
                    } else {
                        countDownTimer.cancel();
                        timerHasStarted = false;
                        startButton.setText("RESTART");
                        hours.setText(hr);
                        minutes.setText(minute);
                        seconds.setText(sec);
                    }
                }
            }
        });
    }


    public void resetTimer (View view){

        if(timeValHour==0 && timeValMinute==0 && timeValSecond==0){
            text.setText("");
        }
        else {
            hours.setText("00");
            minutes.setText("00");
            seconds.setText("00");
            hours.setEnabled(true);
            minutes.setEnabled(true);
            seconds.setEnabled(true);
            countDownTimer.cancel();
            timerHasStarted = false;
            text.setText("");
            startButton.setText("START TIMER");
        }
    }


    public void cancelTimer (View view){
        hours.setText("00");
        minutes.setText("00");
        seconds.setText("00");
        Intent i = new Intent(TimerActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed(){
        this.isDestroyed();
        super.onBackPressed();
    }

    @Override
    public void onInit(int i) {

    }
}