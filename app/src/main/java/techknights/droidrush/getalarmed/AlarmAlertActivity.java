package techknights.droidrush.getalarmed;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static android.R.attr.key;
import static android.speech.tts.TextToSpeech.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AlarmAlertActivity extends Activity implements TextToSpeech.OnInitListener,SensorEventListener {

    public String whatisit ;
    public String alarmName ;
    public String setTime;
    public static int RQS=1,count=0;
    int volume;
    public String t;
    TextView time,question;
    Button whatIsIt;
    TextToSpeech tts;
    public EditText givenAnswer;
    MediaPlayer myMediaPlayer;
    AudioManager audioManager;
    public Uri uri;
    Vibrator vibrator;
    Button dismiss;
    Button snooze;
    long currenttime;
    Calendar alarmCalendar = Calendar.getInstance();
    public float impLevelValue ;
    int snoozeTimes,snoozeDuration;
    SharedPreferences settingsPrefs;
    private GestureDetectorCompat gestureObject;
    private Sensor accelerometer;
    private SensorManager sm;
    private long curTime, lastUpdate;
    private float a,b,c,last_a,last_b,last_c;
    private final static long UPDATE_PERIOD = 100;
    private final static int SHAKE_THRESHOLD = 800;
    Animation animation;
    Random x,y,r;
    public int answer,result,num1,num2;
    char operator = '%';
    Database myDb;
    int reqCode;





    //The alarmAlert Activity is converted into a dialog theme
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_alert);



            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,700);
        this.setFinishOnTouchOutside(false);//don't finish this activity if clicked anywhere outside the dialog


        gestureObject = new GestureDetectorCompat(this,new LearnGesture()); //Swipping gesture for dismiss alarm

        settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        myDb = new Database(this);

        reqCode = getIntent().getIntExtra("reqCode",1);

        whatisit = myDb.alarmGetWhatsIt(reqCode);
        setTime = myDb.alarmGetTime(reqCode);
        alarmName = myDb.alarmGetName(reqCode);
        impLevelValue = myDb.alarmGetImp(reqCode);


        //setting the importance level and creating blinking animation
        final ImageView imp_level = (ImageView)findViewById(R.id.imp_level);
        switch ((int) impLevelValue){
            case 1: imp_level.setImageResource(R.drawable.one_star);
                break;
            case 2: imp_level.setImageResource(R.drawable.two_stars);
                break;
            case 3: imp_level.setImageResource(R.drawable.three_stars);
                break;
            case 4: imp_level.setImageResource(R.drawable.four_stars);
                break;
            case 5: imp_level.setImageResource(R.drawable.five_stars);
                break;
        }
        animation = new AlphaAnimation((float)2.0,0); // Change alpha from fully visible to invisible
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE);        // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);          // Reverse animation at the end so the button will fade back in
        imp_level.startAnimation(animation);

        //vibration
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 1000,1000,1000,1000};


        if(Uri.parse(myDb.alarmGetRingtone(reqCode))== null){ //If no ringtone set by user,set default ringtone of device
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        }
        else
        {
            uri = Uri.parse(myDb.alarmGetRingtone(reqCode));
        }

        //Setting mediaPlayer for alarm ringtone
        myMediaPlayer = new MediaPlayer();
        try {
            myMediaPlayer.setDataSource(this,uri);
             audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                myMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                myMediaPlayer.prepare();
                volume = settingsPrefs.getInt("volume",50);
                myMediaPlayer.setVolume((float)volume/100,(float)volume/100);
            }
        } catch (IOException e) {

        }


        //Vibrate or not acc. to settings

        if (settingsPrefs.getBoolean("vibration",true)==true) {
            vibrator.vibrate(pattern, 0);
        }
        else {
            vibrator.cancel();
        }

        //Play in silent mode or not
        if(settingsPrefs.getBoolean("silentMode",false)==true)
            myMediaPlayer.start();
        else {
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT){
                myMediaPlayer.start();
            }
        }

        myMediaPlayer.setLooping(true);


        //Generating Equation to stop alarm
        question = (TextView)findViewById(R.id.equation);
        x = new Random();
        num1 = x.nextInt(100);
        y = new Random();
        num2 = y.nextInt(100);
        r = new Random();
        switch (r.nextInt(4)){
            case 0: operator = '+';
                answer = num1 + num2;
                break;
            case 1: operator = '-';
                answer = num1 - num2;
                break;
            case 2: operator = '*';
                answer = num1 * num2;
                break;
            case 3: operator = '/';
                answer = num1 / num2;
                break;
            default: operator = '%';
                answer = num1%num2;
                break;
        }

        t = num1 +" "+ operator +" "  + num2 ;
        question.setText(t);

        //Write your answer
        givenAnswer = (EditText)findViewById(R.id.answer);


        //Setting What's it button
        tts = new TextToSpeech(this,this);
        tts.setLanguage(Locale.ENGLISH);
        whatIsIt= (Button)findViewById(R.id.whats_it);

        whatIsIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                vibrator.cancel();
                tts.speak(whatisit, QUEUE_FLUSH, null);

            }
        });


        // Time and alarm name to be displayed on Screen
        time = (TextView)findViewById(R.id.time);
        if(count==0) {
            time.setText(alarmName + "\n" + setTime);
        }
        else{
            time.setText(alarmName + "\n" + "You are already late");
        }

        // TO dismiss the alarm
        dismiss = (Button)findViewById(R.id.dismiss);



            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                if (givenAnswer.getText().toString().matches("")) {
                    Toast.makeText(AlarmAlertActivity.this, "First Write the Answer", Toast.LENGTH_LONG).show();
                    givenAnswer.getText().clear();
                } else {
                    result = Integer.parseInt(givenAnswer.getText().toString());
                    if (result == answer) {
                        myMediaPlayer.stop();
                        vibrator.cancel();
                        count = 0;

                        AlarmAlertActivity.this.finish();
                    } else  {
                        Toast.makeText(AlarmAlertActivity.this, "OOPS!! Try Again", Toast.LENGTH_LONG).show();
                        givenAnswer.getText().clear();
                    }
                }

                }
            });



        //To snooze the alarm
        snooze = (Button)findViewById(R.id.snooze);
        snoozeTimes = settingsPrefs.getInt("snoozeTimes",3);
        snoozeDuration = settingsPrefs.getInt("snoozeDuration",1);
        if((settingsPrefs.getBoolean("snooze",true)==false) || count==snoozeTimes){
            snooze.setEnabled(false);
        }
        else {
            if(count < snoozeTimes) { //count is keeping record of how much times user has snoozed the alarm
                snooze.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                String s = Integer.toString(snoozeDuration);
                Toast.makeText(AlarmAlertActivity.this, "Snoozed for " + s + " minutes",
                        Toast.LENGTH_LONG).show();


                alarmCalendar.add(Calendar.MINUTE, snoozeDuration);
                currenttime = alarmCalendar.getTimeInMillis();


                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent snoozeIntent = new Intent(getBaseContext(), AlarmReceiver.class);
                        snoozeIntent.putExtra("reqCode",reqCode);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmAlertActivity.this, RQS, snoozeIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, currenttime, pendingIntent);
                        RQS++;
                        myMediaPlayer.stop();
                        vibrator.cancel();
                        count = count++;
                        moveTaskToBack(true);
                    }

                });

            }


        }



        //For shake dismiss
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        if(settingsPrefs.getBoolean("shake",false)==true) {
            curTime = lastUpdate = (long) 0.0;
            a = b = c = last_a = last_b = last_c = (float) 0.0;
            sm.registerListener( this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            sm.unregisterListener(this);
        }

    }//onCreate ends here


//    @Override
//    protected void onPause() {
//        super.onPause();
//        sm.unregisterListener(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//    }


    //Setting dismiss by shaking
    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime  = System.currentTimeMillis();

        if((curTime-lastUpdate) > UPDATE_PERIOD){
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            a = event.values[0];
            b = event.values[1];
            c = event.values[2];

            float speed = Math.abs(a + b + c - last_a - last_b - last_c)/diffTime * 10000;
            if(speed > SHAKE_THRESHOLD){
                myMediaPlayer.stop();
                vibrator.cancel();
                count=0;

                finish();
            }
            last_a = a;
            last_b = b;
            last_c  = c;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onInit(int i) {

    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);

    }


    //Swipe Dismiss
    class LearnGesture extends SimpleOnGestureListener {


         @Override
        public boolean onFling(MotionEvent Event1,MotionEvent Event2,float velocityX,float velocityY){
                if(Event2.getX() > Event1.getX() && (num1>50 || num2>50) && operator =='*'){ //If Swipe Right, close the app
                    vibrator.cancel();
                    myMediaPlayer.stop();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                }
                else if(Event2.getX() < Event1.getX() && (num1>50 || num2>50) && operator == '*'){  //If swipe left,go the main activity of app
                    Intent intent = new Intent(AlarmAlertActivity.this,MainActivity.class);
                    vibrator.cancel();
                    myMediaPlayer.stop();
                    finish();
                    startActivity(intent);

                }
                return true;
        }
     }


    @Override
    public void onBackPressed(){
        vibrator.cancel();
        myMediaPlayer.stop();
        count=0;

    }

}

