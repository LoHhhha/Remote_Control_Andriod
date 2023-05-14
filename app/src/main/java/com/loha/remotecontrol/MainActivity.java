package com.loha.remotecontrol;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.FutureTask;


@SuppressLint("ClickableViewAccessibility")


public class MainActivity extends AppCompatActivity {
    private String ServerIP;

    private Context context = this;

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
    private Button wheelDown;
    private Button wheelUp;
    private Button enterCMD;


    private TextView Push;
    private TextView Get;

    private SubMenu reservedIP;

    private String leftClickMouseMessage = "\\M_l";
    private String rightClickMouseMessage = "\\M_r";
    private String wheelDownMouseMessage = "\\M_d";
    private String wheelUpMouseMessage = "\\M_u";
    private String refreshMessage = "\\K_r";
    private String backspaceMessage = "\\K_b";
    private String enterMessage = "\\K_e";
    private String spaceMessage = "\\K_s";
    private String escMessage = "\\K_x";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().putString("preIP", ServerIP).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        reservedIP = menu.addSubMenu(0, 8, 1, R.string.ip_setting);
        updateReservedIP();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void updateReservedIP() {
        Log.d(TAG, "updateReservedIP: " + sharedPreferences.getInt("IP_num", 0));
        for (int i = 1; i <= sharedPreferences.getInt("IP_num", 0); i++) {
            String IP = sharedPreferences.getString("IP" + i, "");
            if (!IP.isEmpty()) {
                if (reservedIP.findItem(i) == null) reservedIP.add(0, i, i, IP);
            } else {
                break;
            }
        }
        if (reservedIP.findItem(6) == null) reservedIP.add(0, 6, 6, "Clear");
        if (reservedIP.findItem(7) == null) reservedIP.add(0, 7, 7, "Help");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.help:
                Intent intent = new Intent(context, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.testConnection:
                checkConnection();
                break;
            case R.id.exit:
                sendMessage("\\exit");
                break;
            case 1:
                changeIP(sharedPreferences.getString("IP1", ""));
                break;
            case 2:
                changeIP(sharedPreferences.getString("IP2", ""));
                break;
            case 3:
                changeIP(sharedPreferences.getString("IP3", ""));
                break;
            case 4:
                changeIP(sharedPreferences.getString("IP4", ""));
                break;
            case 5:
                changeIP(sharedPreferences.getString("IP5", ""));
                break;
            case 6:
                sharedPreferences.edit().putInt("IP_num", 0).apply();
                reservedIP.clear();
                updateReservedIP();
                Log.d(TAG, "Clear IP.");
                break;
            case 7:
                Toast("You Can Long-press <SET> to Add a Valid IP.");
                break;
        }
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
        sharedPreferences = this.getSharedPreferences("IP_Data",Context.MODE_PRIVATE);
        ServerIP = sharedPreferences.getString("preIP", "192.168.1.1");

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
        sendIPButton.setOnLongClickListener((View view) -> {
            String newIP = String.valueOf(IPInput.getText());
            if (newIP.matches("^(((1?\\d?\\d|2[0-4]\\d|25[0-5])\\.){1,3}\\*|((1?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(1?\\d?\\d|2[0-4]\\d|25[0-5]))$")) {
                changeIP(newIP);
                int id = sharedPreferences.getInt("IP_num", 0);
                if (id++ < 5) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("IP" + id, newIP);
                    editor.putInt("IP_num", id);
                    editor.apply();
                    Log.d(TAG, "Add IP " + newIP + " As id " + id);
                    updateReservedIP();
                    Toast("Add an new IP " + newIP);
                } else {
                    Toast("Error: Too Much Reserved IP.");
                }
            } else {
                Toast("Error: Invalid IP.");
                IPInput.setText(ServerIP);
            }

            return true;
        });

        enterTrackPad = findViewById(R.id.enterTrackPad);
        enterTrackPad.setOnClickListener((View view) -> {
            Intent intent = new Intent(context, MouseActivity.class);
            intent.putExtra("ServerIP", ServerIP);
            startActivity(intent);
        });
        enterCMD = findViewById(R.id.enterCMD);
        enterCMD.setOnClickListener((View view) -> {
            Intent intent=new Intent(context, CmdActivity.class);
            intent.putExtra("ServerIP",ServerIP);
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

        wheelDown = findViewById(R.id.wheelDown);
        wheelDown.setOnTouchListener(new View.OnTouchListener() {
            boolean onPush;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Get.setText("Not Accept Reply.");
                        if (Get.getCurrentTextColor() == Color.RED) Get.setTextColor(Color.GRAY);
                        Push.setText(wheelDownMouseMessage);
                        onPush = true;
                        new Thread(() -> {
                            while (onPush) {
                                sendMessageOnly(wheelDownMouseMessage);
                                try {
                                    sleep(20);
                                } catch (Exception e) {
                                    Log.e(TAG, "wheelDown: ", e);
                                }
                            }
                        }).start();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        onPush=false;
                        break;
                    }
                }
                return true;
            }
        });
        wheelUp = findViewById(R.id.wheelUp);
        wheelUp.setOnTouchListener(new View.OnTouchListener() {
            boolean onPush;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Get.setText(R.string.not_accept_reply);
                        if (Get.getCurrentTextColor() == Color.RED) Get.setTextColor(Color.GRAY);
                        Push.setText(wheelUpMouseMessage);
                        onPush = true;
                        new Thread(() -> {
                            while (onPush) {
                                sendMessageOnly(wheelUpMouseMessage);
                                try {
                                    sleep(20);
                                } catch (Exception e) {
                                    Log.e(TAG, "wheelDown: ", e);
                                }
                            }
                        }).start();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        onPush=false;
                        break;
                    }
                }
                return true;
            }
        });
    }

    protected void changeIP(String newIP) {
        ServerIP = String.valueOf(newIP);
        IPInput.setText(ServerIP);
        Toast("Done: ServerIP " + newIP);
        checkConnection();
    }

    protected void sendMouseMoveMessage(float mX, float mY) {
        String message = "\\M_" + (int) mX + "," + (int) mY;
        new Thread(new SendThread(message, ServerIP)).start();
        Push.setText(message);
        if (Get.getCurrentTextColor() == Color.RED) Get.setTextColor(Color.GRAY);
        Get.setText(R.string.not_accept_reply);
    }

    protected void sendMessageOnly(String message) {
        new Thread(new SendThread(message, ServerIP)).start();
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
            long usedTime = (System.nanoTime() - startTime) / 1000000;
            String getString = get + "    <" + usedTime + "ms>";
            if (get != null) {
                Get.setText(getString);
                if (usedTime >= 30) {
                    Get.setTextColor(Color.RED);
                } else {
                    Get.setTextColor(Color.GRAY);
                }
            }
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

