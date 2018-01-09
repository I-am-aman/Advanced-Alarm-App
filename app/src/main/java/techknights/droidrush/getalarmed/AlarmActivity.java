package techknights.droidrush.getalarmed;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TimePicker;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Calendar;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity  {

    public static boolean setClicked = false;
    Button Setalarm;
    public static String setTime;
    PendingIntent pendingIntent;
    Intent intent;
    TimePickerDialog timePickerDialog;
    AlarmManager alarmManager;
    public String whatisit = "",alarmName = "";
    EditText whatsIt;
    EditText alarm_name;
    Button ringtone;
    String fileName;
    public static Uri uri = null;
    Intent custom;
    public  Calendar calNow = Calendar.getInstance();
    public  Calendar calSet = (Calendar) calNow.clone();
    RatingBar impLevel;
    public static float impLevelValue;
    Database myDb;
    SharedPreferences alarm;
    SharedPreferences.Editor editor;
    Switch Repeat;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        whatsIt = (EditText) findViewById(R.id.whats_it);
        Repeat = (Switch) findViewById(R.id.repeat);
        alarm_name = (EditText) findViewById(R.id.alarm_name);
        Setalarm = (Button) findViewById(R.id.set_time);
        ringtone = (Button) findViewById(R.id.ringtone);
        impLevel = (RatingBar) findViewById(R.id.imp_level_bar);

        myDb = new Database(this);

        Button set = (Button)findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmAlarm();
            }
        });

        //For setting importance level
        impLevel = (RatingBar)findViewById(R.id.imp_level_bar);
        impLevelValue = impLevel.getRating();
        impLevel.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar impLevel, float rating,boolean fromUser) {
                impLevelValue = impLevel.getRating();
            }
        });



        // for setting ringtone
        ringtone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                custom = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                custom.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                custom.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select AlarmTone");
                custom.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(Intent.createChooser(custom, "Choose Sound File"), 5);
            }

        });

        //For setting alarm time
        Setalarm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                openTimePickerDialog(false);

            }
        });


    }//onCreate method ends here


    //Opening TimePicker dialog
    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(AlarmActivity.this, AlertDialog.THEME_HOLO_DARK, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Set Alarm");

        timePickerDialog.show();

    }


    OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            int hours = hourOfDay;
            int minutes = minute;
            String am_pm = "";
            //To get the AM/PM value set by user
            if (hours > 12) {
                hours -= 12;
                am_pm = "PM";
            } else if (hours == 0) {
                hours += 12;
                am_pm = "AM";
            } else if (hours == 12) {
                am_pm = "PM";
            } else {
                am_pm = "AM";
            }

            String min = "";
            if (minutes < 10)
                min = "0" + minutes;
            else
                min = String.valueOf(minutes);

            // Append in a StringBuilder to show setTime at the time of alarm ring
            setTime = new StringBuilder().append(hours).append(":")
                    .append(min).append(" ").append(am_pm).toString();

            //Getting the time difference of current time and time set by user
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) { //If time set by user is already passed, set it for next day
                calSet.add(Calendar.DATE, 1);
            }

            alarm = getSharedPreferences("request",MODE_PRIVATE);
            int RQS = alarm.getInt("request",1);

            intent = new Intent(getBaseContext(), AlarmReceiver.class);
            intent.putExtra("reqCode",RQS);
            pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if(Repeat.isChecked()) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            else
            {
                alarmManager.set(AlarmManager.RTC_WAKEUP,calSet.getTimeInMillis(),pendingIntent);
            }
            RQS=RQS+1;
            updatePrefsValue(RQS);
        }

    };

    public void updatePrefsValue(int RQS){
        alarm = getSharedPreferences("request",MODE_PRIVATE);
        editor = alarm.edit();
        editor.putInt("request",RQS);
        editor.apply();
    }



    //Getting the ringtone set by user
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == AlarmActivity.RESULT_OK && requestCode == 5) {
            uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            fileName = null;
            Context context=getApplicationContext();
            String scheme = uri.getScheme();
            if (scheme.equals("file")) {
                fileName = uri.getLastPathSegment();

            }
            else if (scheme.equals("content")) {
                String[] proj = {MediaStore.Audio.Media.TITLE};

                Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    cursor.moveToFirst();
                    fileName = cursor.getString(columnIndex);
                }
            }


        }

    }

    //If clicked on CANCEL button
    public void CancelAlarm(View view) {
        if(alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
        }
        setClicked=false;
        Intent i = new Intent(AlarmActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    //If clicked on SET Button
    public void confirmAlarm() {

        //To obtain a checkbox for set alarm, make sure set button is clicked
        if (setTime == null) {
            setClicked = false;
        }
        else {
            setClicked = true;
            Toast.makeText(AlarmActivity.this, "Alarm set at " + setTime, Toast.LENGTH_SHORT).show();
            alarmName = alarm_name.getText().toString();        // Getting the alarm name set by user
            whatisit = whatsIt.getText().toString();            //Getting the purpose(WHAT's It) set by user
            alarm = getSharedPreferences("request",MODE_PRIVATE);
            int RQS = alarm.getInt("request",1);
            myDb.mainInsertData(RQS-1,setTime);
            boolean res = myDb.alarmInsertData(RQS-1,setTime,alarmName,uri.toString(),whatisit,impLevelValue);
            if(res==true)
                Toast.makeText(AlarmActivity.this, "Done", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(AlarmActivity.this, "Tumse na hoga", Toast.LENGTH_SHORT).show();
            setTime=null;
        }
        Intent i = new Intent(AlarmActivity.this, MainActivity.class);
        startActivity(i);

    }


    //If cellPhone's Back button is pressed without confirming alarm by clicking on SET button
    @Override
    public void onBackPressed(){
        if(alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
        }
        setClicked=false;
        Intent i=new Intent(AlarmActivity.this,MainActivity.class);
        startActivity(i);

        super.onBackPressed();
        finish();
    }



}


