package com.loha.remotecontrol;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@SuppressLint("ClickableViewAccessibility")

public class MouseActivity extends AppCompatActivity {
    private String ServerIP;
    private String leftClickMouseMessage="\\M_l";
    private String rightClickMouseMessage="\\M_r";
    private String leftClickDownMouseMessage = "\\M_ld";
    private String leftClickUpMouseMessage = "\\M_lu";
    private String rightClickDownMouseMessage = "\\M_rd";
    private String rightClickUpMouseMessage = "\\M_ru";

    private Button trackPad;
    private Button leftClick;
    private Button rightClick;

    private boolean leftPush=false,rightPush=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        ServerIP=getIntent().getStringExtra("ServerIP");
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void init(){
        trackPad=findViewById(R.id.trackPad);
        trackPad.setOnTouchListener(new View.OnTouchListener() {
            float preX,preY,curX,curY;
            boolean moved;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        moved=false;
                        preX = event.getX();
                        preY = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        moved=true;
                        curX = event.getX();
                        curY = event.getY();
                        sendMouseMoveMessage(curX-preX,curY-preY);
                        preX=curX;
                        preY=curY;
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        if(!moved){
                            sendMessageQuietness(leftClickMouseMessage);
                        }
                        break;
                    }
                }
                return true;
            }
        });
        leftClick=findViewById(R.id.leftClick);
        leftClick.setOnClickListener((View view)->{
            leftPush=false;
            sendMessageQuietness(leftClickMouseMessage);
        });
        leftClick.setOnLongClickListener((View view)->{
            if(leftPush){
                sendMessageQuietness(leftClickUpMouseMessage);
            }
            else {
                sendMessageQuietness(leftClickDownMouseMessage);
            }
            leftPush=!leftPush;
            return true;
        });
        rightClick=findViewById(R.id.rightClick);
        rightClick.setOnClickListener((View view)->{
            rightPush=false;
            sendMessageQuietness(rightClickMouseMessage);
        });
        rightClick.setOnLongClickListener((View view)->{
            if(rightPush){
                sendMessageQuietness(rightClickUpMouseMessage);
            }
            else {
                sendMessageQuietness(rightClickDownMouseMessage);
            }
            rightPush=!rightPush;
            return true;
        });
    }

    protected void sendMouseMoveMessage(float mX,float mY){
        String sendMessage="\\M_" + (int)mX + "," + (int)mY;
        trackPad.setText(sendMessage);
        new Thread(new SendThread(sendMessage,ServerIP)).start();
    }

    protected void sendMessageQuietness(String message) {
        trackPad.setText(message);
        new Thread(new SendThread(message,ServerIP)).start();
    }

    Toast preToast=null;
    protected void Toast(String msg) {
        if(preToast!=null){
            preToast.cancel();
        }
        preToast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        preToast.show();
    }
}