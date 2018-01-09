package techknights.droidrush.getalarmed;


import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    boolean setClicked = AlarmActivity.setClicked;
    ListView listItem;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    SharedPreferences.Editor editor;
    SharedPreferences mainPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting FAB button (used to set Alarm)
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,AlarmActivity.class);
                startActivity(i);

            }
        });


        //Navigation Drawer settings
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listItem = (ListView)findViewById(R.id.list_view);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listItem.setAdapter(adapter);
        registerForContextMenu(listItem);

        Database mydb = new Database(this);

        mainPrefs = getSharedPreferences("main",MODE_PRIVATE);
        for (int i = 0;; ++i){
            final String str = mainPrefs.getString(String.valueOf(i), "");
            if (!str.equals("")){
                adapter.add(str);
            } else {
                break;          // empty String means the default value was returned
            }
        }

        if(setClicked==true)
        {
            SharedPreferences alarm = getSharedPreferences("request",MODE_PRIVATE);
            int RQS =alarm.getInt("request",1);
            RQS = RQS-1;
            String alarmtime = mydb.alarmGetTime(RQS);
            arrayList.add(alarmtime);
            adapter.notifyDataSetChanged();
            saveStringToPreferences();
            setClicked=false;
        }

    }//onCreate ends here

    private void saveStringToPreferences() {
        mainPrefs = getSharedPreferences("main",MODE_PRIVATE);
        editor = mainPrefs.edit();
        for (int i = 0; i < adapter.getCount(); ++i) {
            editor.putString(String.valueOf(i), adapter.getItem(i));
        }
        editor.apply();
    }

    //creating context menu for listView items
    @Override
    public void onCreateContextMenu(ContextMenu menu , View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.add("Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        if (item.getTitle() == "Delete") {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            adapter.remove(adapter.getItem(info.position));
            adapter.notifyDataSetChanged();
            editor = mainPrefs.edit();
            editor.clear().apply();
            saveStringToPreferences();
            SharedPreferences alarm = getSharedPreferences("request",MODE_PRIVATE);
            int RQS =alarm.getInt("request",1);
            RQS = RQS-1;
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent.getBroadcast(getBaseContext(), RQS, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
            Toast.makeText(MainActivity.this, "Alarm Deleted", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    //Setting menu buttons present on action bars
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {

            Intent i= new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    //Setting Navigation drawer Items
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.chronometer) {

            Intent i= new Intent(MainActivity.this,ChronometerActivity.class);
            startActivity(i);

        } else if (id == R.id.timer) {

            Intent i= new Intent(MainActivity.this,TimerActivity.class);
            startActivity(i);

        } else if (id == R.id.notes) {

            Intent i= new Intent(MainActivity.this,NotesActivity.class);
            startActivity(i);

        } else if(id == R.id.about_developer){

            Intent i= new Intent(MainActivity.this,AboutDeveloperActivity.class);
            startActivity(i);

        }else if (id == R.id.share) {
            ApplicationInfo info = getApplicationContext().getApplicationInfo();
            String filepath = info.sourceDir;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filepath)));
            startActivity(Intent.createChooser(intent, "Share app via"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    //When back button is pressed
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { //if navigation drawer is open,close it
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            if(settingsPrefs.getBoolean("exit",true)==true) { // Dialog for exit confirmation appears if user has done such settings

                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_exit)
                        .setTitle("Really Exit?").setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        }).create().show();
            }

            else
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
