package com.sijo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IntroActivity extends Activity {

    Intent it_next;
    Thread thr_next=new Thread(){

        @Override
        public void run() {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            it_next=new Intent(IntroActivity.this, MainActivity.class);
            startActivity(it_next);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }


    @Override
    protected void onResume() {
        super.onResume();
        thr_next.start();
    }

    protected void onPause(){
        super.onPause();
        //backgroundMusic.release();
        finish();
    }
}
