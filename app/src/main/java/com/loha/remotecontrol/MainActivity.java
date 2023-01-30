package com.loha.remotecontrol;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@SuppressLint("ClickableViewAccessibility")


public class MainActivity extends AppCompatActivity {
    private String ServerIP = "192.168.18.128";

    private Context context = this;

    private boolean sendSuccess;

    private EditText sendInput;
    private EditText IPInput;

    private Button sendButton;
    private Button escButton;
    private Button spaceButton;
    private Button refreshButton;
    private Button backspaceButton;
    private Button enterButton;
    private Button sendIPButton;

    private Button enterTrackPad;
    private Button sTrackPad;

    private TextView Push;
    private TextView Get;

    private String leftClickMouseMessage = "\\M_l";
    private String rightClickMouseMessage = "\\M_r";
    private String refreshMessage = "\\K_r";
    private String backspaceMessage = "\\K_b";
    private String enterMessage = "\\K_e";
    private String spaceMessage = "\\K_s";
    private String escMessage = "\\K_x";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.IP_0:
                changeIP(getString(R.string.ip_0));
                break;
            case R.id.IP_1:
                changeIP(getString(R.string.ip_1));
                break;
        }
        IPInput.setText(ServerIP);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    protected void checkConnection() {
        if (sendMessage("\\TestConnect")) Toast("Done: Found Server!");
        else Toast("Error: Server Disconnect.");
    }

    protected void init() {
        sendButton = findViewById(R.id.sendButton);
        sendInput = findViewById(R.id.sendInput);

        sendButton.setOnClickListener((View view) -> {
            String sendText = String.valueOf(sendInput.getText());
            if (sendText.isEmpty()) {
                Toast("Message Empty!");
            } else {
                sendInput.setText("");
                if (!sendMessage(sendText)) Toast("Error: Server Disconnect.");
            }
        });
        sendButton.setOnLongClickListener((View view) -> {
            String sendText = String.valueOf(sendInput.getText());
            if (sendText.isEmpty()) {
                Toast("Warning: Message Empty!");
            } else {
                if (!sendMessage(sendText)) Toast("Error: Server Disconnect.");
            }
            return true;
        });

        escButton = findViewById(R.id.escButton);
        spaceButton = findViewById(R.id.spaceButton);
        refreshButton = findViewById(R.id.refreshButton);
        backspaceButton = findViewById(R.id.backspaceButton);
        enterButton = findViewById(R.id.enterButton);
        escButton.setOnClickListener((View view) -> sendMessage(escMessage));
        escButton.setOnLongClickListener((View view) -> {
            Toast("Send Message: " + escMessage);
            return true;
        });
        spaceButton.setOnClickListener((View view) -> sendMessage(spaceMessage));
        spaceButton.setOnLongClickListener((View view) -> {
            Toast("Send Message: " + spaceMessage);
            return true;
        });
        refreshButton.setOnClickListener((View view) -> sendMessage(refreshMessage));
        refreshButton.setOnLongClickListener((View view) -> {
            Toast("Send Message: " + refreshMessage);
            return true;
        });
        backspaceButton.setOnClickListener((View view) -> sendMessage(backspaceMessage));
        backspaceButton.setOnLongClickListener((View view) -> {
            Toast("Send Message: " + backspaceMessage);
            return true;
        });
        enterButton.setOnClickListener((View view) -> sendMessage(enterMessage));
        enterButton.setOnLongClickListener((View view) -> {
            Toast("Send Message: " + enterMessage);
            return true;
        });

        sendIPButton = findViewById(R.id.sendIPButton);
        IPInput = findViewById(R.id.IPInput);
        IPInput.setText(ServerIP);
        sendIPButton.setOnClickListener((View view) -> {
            String newIP = String.valueOf(IPInput.getText());
            if (newIP.matches("^(((1?\\d?\\d|2[0-4]\\d|25[0-5])\\.){1,3}\\*|((1?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(1?\\d?\\d|2[0-4]\\d|25[0-5]))$")) {
                changeIP(newIP);
            } else {
                Toast("Error: Invalid IP.");
                IPInput.setText(ServerIP);
            }
        });

        enterTrackPad = findViewById(R.id.enterTrackPad);
        enterTrackPad.setOnClickListener((View view) -> {
            Intent intent = new Intent(context, MouseActivity.class);
            intent.putExtra("ServerIP", ServerIP);
            startActivity(intent);
        });
        sTrackPad = findViewById(R.id.sTrackPad);
        sTrackPad.setOnTouchListener(new View.OnTouchListener() {
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
                        sendMouseMoveMessage(curX - preX, curY - preY);
                        preX = curX;
                        preY = curY;
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!moved) {
                            sendMessage(leftClickMouseMessage);
                        }
                        break;
                    }
                }
                return true;
            }
        });

        Push = findViewById(R.id.Push);
        Get = findViewById(R.id.Get);
    }

    protected void changeIP(String newIP) {
        ServerIP = String.valueOf(newIP);
        Toast("Done: ServerIP " + newIP);
        checkConnection();
    }

    protected void sendMouseMoveMessage(float mX, float mY) {
        String message = "\\M_" + (int) mX + "," + (int) mY;
        Push.setText(message);
        new Thread(new SendThread(message, ServerIP)).start();
        Get.setText("Not Accept Reply.");
    }

    protected boolean sendMessage(String message) {
        Get.setText("");
        Push.setText(message);
        long startTime = System.nanoTime();
        FutureTask<String> futureTask = new FutureTask<>(new SendFuture(message, ServerIP));
        new Thread(futureTask).start();
        String get = null;
        try {
            get = futureTask.get();
            long endTime = System.nanoTime();
            String setGetString = get + "    <" + (endTime - startTime)/1000000 + "ms>";
            if (get != null) Get.setText(setGetString);
        } catch (Exception e) {
            Log.d(TAG, "sendMessageQuietness: ", e);
        }

        return get != null;
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

