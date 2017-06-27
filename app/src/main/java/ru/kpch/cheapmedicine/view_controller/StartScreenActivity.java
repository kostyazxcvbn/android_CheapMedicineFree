package ru.kpch.cheapmedicine.view_controller;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.MotionEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.model.AppLogicImpl;

public class StartScreenActivity extends Activity {

    protected int splashTime = 5000;

    private Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ExecutorService appThreadPool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        appThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                AppLogicImpl.createInstance(getApplicationContext(), appThreadPool);
            }
        });

        setContentView(R.layout.activity_start_screen);
        final StartScreenActivity sPlashScreen = this;

        // thread for displaying the StartScreenActivity
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized(this){

                        wait(splashTime);
                    }

                } catch(InterruptedException e) {}
                finally {
                    finish();
                    Intent i = new Intent();
                    i.setClass(sPlashScreen, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        splashTread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            synchronized(splashTread){
                splashTread.notifyAll();
            }
        }

        return true;
    }


}
