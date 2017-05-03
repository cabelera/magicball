package com.example.guilherme.conectesp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lsitec on 03/05/17.
 */

public class MainActivity extends AppCompatActivity {

    EditText editIp;
    Button btnOn, btnOff;
    TextView textInfo1, textInfo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editIp = (EditText)findViewById(R.id.ip);
        btnOn = (Button)findViewById(R.id.bon);
        btnOff = (Button)findViewById(R.id.boff);
        textInfo1 = (TextView)findViewById(R.id.info1);
        textInfo2 = (TextView)findViewById(R.id.info2);



        btnOn.setOnClickListener(btnOnOffClickListener);
        btnOff.setOnClickListener(btnOnOffClickListener);

    }

    View.OnClickListener btnOnOffClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String onoff;
            if(v==btnOff){
                onoff="off";
            }else{
                onoff="on";
            }

            //btnOn.setEnabled(false);
            //btnOff.setEnabled(false);

            String serverIP = editIp.getText().toString()+":80";

            ESPControler controler = new ESPControler(serverIP);
            controler.execute(onoff);
        }
    };


}
