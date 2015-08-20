package com.example.rohan.ecg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;


public class Splash extends ActionBarActivity {
    Intent intent;
    int check;
    TextView tv;
    RelativeLayout rl;
    ImageView iv;
    MediaPlayer hb;
    //  AnimationDrawable Anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //  getSupportActionBar().setTitle("");
        tv = (TextView) findViewById(R.id.textView);

        //   rl.setBackgroundResource(R.drawable.happy);
        //  Anim = (AnimationDrawable) rl.getBackground();
        //this.rl.setBackgroundColor(colors[0]);

        hb = MediaPlayer.create(this, R.raw.heartbeat);
        hb.start();
        tv.startAnimation(AnimationUtils.loadAnimation(Splash.this, android.R.anim.slide_in_left));
        iv = (ImageView) findViewById(R.id.imageView);
        //            iv.setAnimation(AnimationUtils.loadAnimation(Splash.this,android.R.anim.slide_in_left));

        YoYo.with(Techniques.FadeIn).duration(1000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                YoYo.with(Techniques.Pulse).duration(1000).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        YoYo.with(Techniques.Pulse).duration(1000).withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                YoYo.with(Techniques.Pulse).duration(1000).withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).playOn(findViewById(R.id.imageView));
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).playOn(findViewById(R.id.imageView));
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).playOn(findViewById(R.id.imageView));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).playOn(findViewById(R.id.imageView));


        SharedPreferences preferences = getSharedPreferences("Yes", Context.MODE_PRIVATE);
        check = preferences.getInt("Check", 0);
        new CountDownTimer(5500, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            //   @Override
            public void onFinish() {
                if (check == 0) {
                    intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    hb.stop();
                    finish();
                } else {
                    intent = new Intent(Splash.this, Details.class);
                    startActivity(intent);
                    hb.stop();
                    finish();


                }
            }

        }.start();
    }

}



