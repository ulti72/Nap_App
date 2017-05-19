package com.goole4moneygmail.napapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity extends AppCompatActivity {
//start of oncreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);
        ImageView fimg = (ImageView)findViewById(R.id.imageView3);
        fimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri =Uri.parse("https://www.facebook.com/TheNapApp/");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        ImageView ra = (ImageView)findViewById(R.id.RA);
        ra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri =Uri.parse("https://play.google.com/store/apps/details?id=com.goole4moneygmail.napapp");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        Button b2 = (Button) findViewById(R.id.button1); //button id
        //setting onclicklistener to go on mainactivity (back)
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity.super.onBackPressed();
            }
        });

        //mail

        ImageView maimg = (ImageView)findViewById(R.id.imageView2);
        maimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:abhishekpro47@gmail.com")); // only email apps should handle thi
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        //share
        ImageView im =(ImageView) findViewById(R.id.share);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "NapApp");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.goole4moneygmail.napapp \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

    } //end of onCreate method
}//end of Activity class
