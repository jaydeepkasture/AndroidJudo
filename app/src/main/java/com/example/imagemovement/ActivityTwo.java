package com.example.imagemovement;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.net.BindException;

public class ActivityTwo extends AppCompatActivity {

    private static final int NUM_OF_VIEWS = 5; // Number of ImageViews
    private ImageView[] imageViews = new ImageView[NUM_OF_VIEWS];
    private ImageView pawn ;
    private GridLayout gridLayout ;
    private int currentPosition =91; // Start position for the image
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_two);
        pawn = findViewById(R.id.google);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(gridLayout.getChildAt(currentPosition).getWidth(), gridLayout.getChildAt(currentPosition).getHeight());
//        pawn.setLayoutParams(layoutParams);
       gridLayout = findViewById(R.id.boardgrid);

        moveImage();
    }
    private void moveImage() {
        if(currentPosition>96)return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gridLayout = findViewById(R.id.boardgrid);

                View target = gridLayout.getChildAt(currentPosition);
                int[] targetLocation = new int[2];
                target.getLocationInWindow(targetLocation);

                // Move the pawn to the new position
                pawn.animate()
                        .x(targetLocation[0] )
                        .y(targetLocation[1])
                        .setDuration(800)
                        .start();
                 currentPosition++;
                moveImage();
            }

        }, 1000); // Delay in milliseconds (e.g., 1000ms = 1 second)
    }
}