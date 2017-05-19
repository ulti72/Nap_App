package com.goole4moneygmail.napapp;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class MainActivity extends AppCompatActivity {

    int count;
    Thread t;
    private InterstitialAd mInterstitial;
    private AdView mAdView;
    boolean ss = true;
   MediaPlayer mp=null ;
    long[] pattern = { 0, 100, 800, 100, 800};


    @Override  //overriding method so that back button work as home button
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    @Override
    //starting of oncreate method
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//making status and navigation transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
          //  w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Toast.makeText(MainActivity.this, "Hold the Timer & Nap :)", Toast.LENGTH_LONG).show();  //toast on opening of app

        setContentView(R.layout.activity_main);



        //ad request
        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //loading intertitial ad
        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));

        AdRequest ar = new AdRequest.Builder().build();
        mInterstitial.loadAd(ar);

        startAlarm();

        //setting animation on upper corner button to open next activity
        final Button b1 = (Button) findViewById(R.id.button);//getting button to move to next activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        // setting oncliclistener on upper button to open another activity (info tab)
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity.class);   //crating intent to other activity
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation, R.anim.animation2).toBundle();//creating transiton animation between activites , andimations stored in anim resources folder
                startActivity(intent, bndlanimation);  // starting activity with animation
            }
        });

        final TextView t1 = (TextView) findViewById(R.id.timer);   // timer id
        final Switch b2 = (Switch) findViewById(R.id.switch1);    // switch button
        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);  // creating Vibrator object for vibration
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(this, notification);



        //creating thread for timer
        t = new Thread() {
            @Override
            public void run() {
                while (ss) {
                    try {

                        Thread.sleep(1000);//1000msec=1sec

                        runOnUiThread(new Runnable() {
                            //
                            @Override
                            public void run() {
                                NumberFormat f = new DecimalFormat("00"); //formatting time to display in 01 format
                                count++;
                                t1.setText(f.format(count) + " sec.");


                            }
                        });
                    } catch
                            (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };  // ending of thread

        //setting ontouch listener on timer
        t1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:       //performs on holding operations
                        t1.setTextColor(Color.parseColor("#ff1a1a"));    //changes color to cyan
                        if (t.getState() == Thread.State.NEW)
                        {
                            t.start();
                        }
                                             //starting the timer
                        b1.setEnabled(false);   //while holding the timer disabling the upper left corner button
                        break;
                    case MotionEvent.ACTION_UP:         //performs on relesing timer operations
                        t.interrupt();                  //interrupting thread
                        t1.setTextColor(Color.WHITE);   // changes color to white
                        t1.setOnTouchListener(null);    // disabling the touch on timer again unless activity restarted
                        b2.setVisibility(View.VISIBLE); // making switch button visible ,
                        ss = false;

                        t1.setEnabled(false);
                        //setting boolean for timer , so it will stop running
                        vib.vibrate(pattern , 0);              // vibration
                        mp.start();
                        //starting ringtone
                        mp.setLooping(true);
                        b1.setEnabled(true);
                        break;

                }
                b2.setChecked(true);   //swiching on the switch when timer is hold
                return true;
            }
        }); // ending of setOntouchListener on timer



        //functioning of switch button
        b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) //on unchecking the swithch do the following
                {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);             //three lines for restarting activity
                    overridePendingTransition(0, 0);   // removing animation on restarting activity
                    mp.stop();                         // stopping the ringtone
                    vib.cancel();                      // stopping the vibration
                    if(mInterstitial.isLoaded())       // for showing interstitial add if it loaded
                        mInterstitial.show();
                } //end of if


            }
        });        // ending of setonclicklistner on switch

    } //end of oncreate method

    private void startAlarm() {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent = null;





            myIntent = new Intent(MainActivity.this,AlarmNotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);


            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000*60*300,AlarmManager.INTERVAL_HALF_DAY,pendingIntent);


    }

} //end of mainActivity class
