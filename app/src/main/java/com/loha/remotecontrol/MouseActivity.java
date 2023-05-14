package com.loha.remotecontrol;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.FutureTask;

@SuppressLint("ClickableViewAccessibility")

public class MouseActivity extends AppCompatActivity {
    private String ServerIP;
    private String leftClickMouseMessage = "\\M_l";
    private String rightClickMouseMessage = "\\M_r";
    private String leftClickDownMouseMessage = "\\M_ld";
    private String leftClickUpMouseMessage = "\\M_lu";
    private String rightClickDownMouseMessage = "\\M_rd";
    private String rightClickUpMouseMessage = "\\M_ru";
    private String middleClickMouseMessage = "\\M_w";

    private String middleClickDownMouseMessage = "\\M_wd";
    private String middleClickUpMouseMessage = "\\M_wu";

    private Button trackPad;
    private Button leftClick;
    private Button rightClick;
    private Button midClick;

    WindowInsetsController controller;
    private boolean leftPush = false, rightPush = false, middlePush = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        ServerIP = getIntent().getStringExtra("ServerIP");
        if (ServerIP == null) {
            ServerIP = this.getSharedPreferences("IP_Data", Context.MODE_PRIVATE).getString("preIP", "192.168.1.1");
            showServerIP();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trackpad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return false;
            case R.id.showip:
                showServerIP();
                break;
            case R.id.flipScreen:
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void init() {
        controller = getWindow().getInsetsController();
        trackPad = findViewById(R.id.trackPad);
        trackPad.setOnTouchListener(new View.OnTouchListener() {
            float preX, preY, curX, curY;
            boolean moved;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        moved = false;
                        preX = event.getX();
                        preY = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        moved = true;
                        curX = event.getX();
                        curY = event.getY();
                        int pointerNum = event.getPointerCount();
                        if (pointerNum == 1) {
                            sendMouseMoveMessage(curX - preX, curY - preY);
                        } else if (pointerNum == 2) {
                            sendMouseMoveMessage(preY - curY);
                        }
                        preX = curX;
                        preY = curY;
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!moved) {
                            sendMessageQuietness(leftClickMouseMessage);
                        }
                        break;
                    }
                }
                return true;
            }
        });
        leftClick = findViewById(R.id.leftClick);
        leftClick.setOnClickListener((View view) -> {
            leftPush = false;
            sendMessageQuietness(leftClickMouseMessage);
        });
        leftClick.setOnLongClickListener((View view) -> {
            if (leftPush) {
                sendMessageQuietness(leftClickUpMouseMessage);
            } else {
                sendMessageQuietness(leftClickDownMouseMessage);
            }
            leftPush = !leftPush;
            return true;
        });
        rightClick = findViewById(R.id.rightClick);
        rightClick.setOnClickListener((View view) -> {
            rightPush = false;
            sendMessageQuietness(rightClickMouseMessage);
        });
        rightClick.setOnLongClickListener((View view) -> {
            if (rightPush) {
                sendMessageQuietness(rightClickUpMouseMessage);
            } else {
                sendMessageQuietness(rightClickDownMouseMessage);
            }
            rightPush = !rightPush;
            return true;
        });
        midClick = findViewById(R.id.midClick);
        midClick.setOnClickListener((View view) -> {
            middlePush = false;
            sendMessageQuietness(middleClickMouseMessage);
        });
        midClick.setOnLongClickListener((View view) -> {
            if (middlePush) {
                sendMessageQuietness(middleClickUpMouseMessage);
            } else {
                sendMessageQuietness(middleClickDownMouseMessage);
            }
            middlePush = !middlePush;
            return true;
        });
    }

    protected void showServerIP() {
        Toast("Server IP: " + ServerIP);
    }

    protected void sendMouseMoveMessage(float mX, float mY) {
        String sendMessage = "\\M_" + (int) mX + "," + (int) mY;
        new Thread(new SendThread(sendMessage, ServerIP)).start();
        trackPad.setText(sendMessage);
    }

    protected void sendMouseMoveMessage(float Y) {
        String sendMessage = "\\MW_" + (int) Y;
        new Thread(new SendThread(sendMessage, ServerIP)).start();
        trackPad.setText(sendMessage);
    }

    protected void sendMessageQuietness(String message) {
        long startTime = System.nanoTime();
        FutureTask<String> futureTask = new FutureTask<>(new SendFuture(message, ServerIP));
        new Thread(futureTask).start();
        String get = null;
        try {
            get = futureTask.get();
            long usedTime = (System.nanoTime() - startTime) / 1000000;
            String getString = message;
            if (get != null) getString += '\n' + get + "    <" + usedTime + "ms>";
            trackPad.setText(getString);
        } catch (Exception e) {
            Log.d(TAG, "sendMessageQuietness: ", e);
        }
    }

    Toast preToast = null;

    protected void Toast(String msg) {
        if (preToast != null) {
            preToast.cancel();
        }
        preToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        preToast.show();
    }
}