package com.example.imagemovement;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Looper;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.net.BindException;

public class ActivityTwo extends AppCompatActivity {

    private static final int NUM_OF_VIEWS = 5; // Number of ImageViews
    private ImageView[] imageViews = new ImageView[NUM_OF_VIEWS];
    private ImageView pawn ;
    private GridLayout gridLayout ;
    private int currentPosition =91; // Start position for the image
    private Handler handler = new Handler();
    int[] bluePath = new int[]{133,132,131,130,129,143,158,173,188,203,218,217,216,201,186,171,156,141,125,124,123,122,121,120,105,90,91,92,93,94,95,81,66,51,36,21,6,7,8,23,38,53,68,83,99,100,101,103,104,119,118,117,116,115,114,113};

    int[] yellowPath = new int[]{23,38,53,68,83,99,100,101,102,103,104,119,134,133,132,131,130,129,143,158,173,188,203,218,217,216,201,186,171,156,141,125,124,123,122,121,120,105,90,91,92,93,94,95,81,66,51,36,21,6,7,22,37,52,67,82,97};

    int[] redPath = new int[]{201,186,171,156,141,125,124,123,122,121,120,105,90,91,92,93,94,95,81,66,51,36,21,6,7,8,23,38,53,68,83,99,100,101,102,103,104,119,134,133,132,131,130,129,143,158,173,188,203,218,217,202,187,172,157,142,127};

    int[] greenPath = new int[]{91,92,93,94,95,81,66,51,36,21,6,7,8,23,38,53,68,83,99,100,101,102,103,104,119,134,133,132,131,130,129,143,158,173,188,203,218,217,216,201,186,171,156,141,125,124,123,122,121,120,105,106,107,108,109,110,111};

    int[] safeSpot = new int[]{188,201,122,91,36,23,102,133};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_two);
        pawn = findViewById(R.id.google);
        gridLayout = findViewById(R.id.boardgrid);

        View firstChild = gridLayout.getChildAt(0);

        // Ensure the view is not null and has been laid out
        if (firstChild != null) {
            // Use a post to ensure the layout is completed
            firstChild.post(() -> {
                // Get the width and height of the first child
                int width = firstChild.getWidth();
                int height = firstChild.getHeight();

                // Set these dimensions to the ImageView
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                pawn.setLayoutParams(layoutParams);
            });
        }
        setHeightOfGridLayout();
//        moveImage();
        moveImageToSpots(redPath);
    }

    private void setHeightOfGridLayout() {
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent it from being called multiple times
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Get the width of the GridLayout
                int gridWidth = gridLayout.getWidth();

                // Set the height of the GridLayout equal to its width
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) gridLayout.getLayoutParams();
                layoutParams.height = gridWidth;
                layoutParams.width = gridWidth;
                gridLayout.setLayoutParams(layoutParams);
            }
        });
    }

    private void moveImage() {
        if(currentPosition>95)return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gridLayout = findViewById(R.id.boardgrid);

                View target = gridLayout.getChildAt(currentPosition);
                pawn.getLayoutParams().width=target.getWidth();
                pawn.getLayoutParams().height=target.getHeight();
                int[] targetLocation = new int[2];
                target.getLocationInWindow(targetLocation);

                // Move the pawn to the new position
                pawn.animate()
                        .x(targetLocation[0] )
                        .y(targetLocation[1])
                        .setDuration(500)
                        .start();
                 currentPosition++;
                moveImage();
            }

        }, 510); // Delay in milliseconds (e.g., 1000ms = 1 second)
    }

    private void moveImageToSpots(int[] indices) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final int[] currentIndex = {0};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex[0] < indices.length) {
                    int index = currentIndex[0];
                    ImageView targetSpot = (ImageView) gridLayout.getChildAt(indices[index]);
                    int[] location = new int[2];
                    int[] pawnLocation = new int[2];
                    targetSpot.getLocationOnScreen(location);
                    pawn.getLocationOnScreen(pawnLocation);
                    float targetX = location[0];
                    float targetY = location[1];

//                    ObjectAnimator.ofFloat(pawn, "translationX", pawn.getX(), targetX).setDuration(500).start();
//                    ObjectAnimator.ofFloat(pawn, "translationY", pawn.getY(), targetY).setDuration(500).start();

                    AnimatorSet animatorSet = new AnimatorSet();

                    ObjectAnimator translateX = ObjectAnimator.ofFloat(pawn, "translationX", pawn.getX(), targetX);
                    ObjectAnimator translateY = ObjectAnimator.ofFloat(pawn, "translationY", pawn.getY(), targetY);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(pawn, "scaleX", 1f, 1.15f, 1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(pawn, "scaleY", 1f, 1.15f, 1f);
                    ObjectAnimator rotate = ObjectAnimator.ofFloat(pawn, "rotation", 0f, 360f);
                    translateX.setDuration(1000);
                    translateY.setDuration(1000);
                    scaleX.setDuration(1000);
                    scaleY.setDuration(1000);
                    rotate.setDuration(1000);

                    translateX.setInterpolator(new BounceInterpolator());
                    translateY.setInterpolator(new BounceInterpolator());
                    scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
                    scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
                    rotate.setInterpolator(new LinearInterpolator());

                    animatorSet.playTogether(translateX, translateY, scaleX, scaleY, rotate);
                    animatorSet.start();

                    currentIndex[0]++;
                    handler.postDelayed(this, 1200); // Delay for animation

                             }
            }
        };

        handler.post(runnable);
    }

}