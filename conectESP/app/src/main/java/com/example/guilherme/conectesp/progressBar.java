package com.example.guilherme.conectesp;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ProgressBar;

import java.util.logging.LogRecord;

/**
 * Created by guilherme on 04/04/17.
 */

public class progressBar extends Activity{
    private static final int PROGRESS = 0x1;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("mProgressStatus  CRIEI 1:" +  mProgressStatus);

        setContentView(R.layout.loadingscreen);

        mProgress = (ProgressBar) findViewById(R.id.progressbar);
        mProgress.setProgress(10);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                System.out.println("mProgressStatus  ENTREI 1:" +  mProgressStatus);
                while (mProgressStatus < 100) {
                    //mProgressStatus = doWork();

                    System.out.println("mProgressStatus  ENTREI :" +  mProgressStatus);

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                            SystemClock.sleep(1000);
                            mProgressStatus++;

                            System.out.println("mProgressStatus  status :" +  mProgressStatus);
                        }
                    });
                }
            }
        }).start();
    }



    private void setProgressColor (int color){
        //int color = 0xFF00FF00;
        mProgress.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mProgress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }


}
