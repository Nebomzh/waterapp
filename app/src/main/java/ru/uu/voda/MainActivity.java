package ru.uu.voda;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity  {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);




        // Create a new ImageView
       // ImageView imageView = new ImageView(this);
        // Set the background color to white
      //  imageView.setBackgroundColor(Color.WHITE);
        // Parse the SVG file from the resource
       // SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.android);
        // Get a drawable from the parsed SVG and set it as the drawable for the ImageView
      //  imageView.setImageDrawable(svg.createPictureDrawable());
        // Set the ImageView as the content view for the Activity
       // setContentView(imageView);

       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);



    }



}

