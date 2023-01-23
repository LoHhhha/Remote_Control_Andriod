package com.loha.remotecontrol;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private String ServerIP = "192.168.18.128";

    private boolean sendSuccess;

    private EditText sendInput;
    private EditText IPInput;

    private Button sendButton;
    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;
    private Button clickButton;
    private Button refreshButton;
    private Button backspaceButton;
    private Button enterButton;
    private Button sendIPButton;

    private String upMouseMessage = "\\M_u";
    private String downMouseMessage = "\\M_d";
    private String leftMouseMessage = "\\M_l";
    private String rightMouseMessage = "\\M_r";
    private String leftClickMouseMessage = "\\M_a";
    private String rightClickMouseMessage = "\\M_b";
    private String refreshMessage = "\\K_r";
    private String backspaceMessage = "\\K_b";
    private String enterMessage = "\\K_e";


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

    protected void checkConnection() {
        Thread t = new Thread(new SendThread("\\connectTest"));
        t.start();
        while (t.isAlive()) {
            try {
                sleep(1);
            } catch (InterruptedException e) {
                Log.e(TAG, "SendThread: ", e);
            }
        }
        if (sendSuccess) Toast("Done: Found Server!");
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
                Thread t = new Thread(new SendThread(sendText));
                t.start();
                Toast("Try to Send Message: " + sendText);
                sendInput.setText("");
                while (t.isAlive()) {
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "SendThread: ", e);
                    }
                }
                if (sendSuccess) Toast("Done: Get Server Reply!");
                else Toast("Error: Server Disconnect.");
            }
        });
        sendButton.setOnLongClickListener((View view) -> {
            String sendText = String.valueOf(sendInput.getText());
            if (sendText.isEmpty()) {
                Toast("Warning: Message Empty!");
            } else {
                Thread t = new Thread(new SendThread(sendText));
                t.start();
                Toast("Try to Send Message: " + sendText);
                while (t.isAlive()) {
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "SendThread: ", e);
                    }
                }
                if (sendSuccess) Toast("Done: Get Server Reply!");
                else Toast("Error: Server Disconnect.");
            }
            return true;
        });

        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        //upButton.setOnClickListener((View view) -> sendMessageQuietness(upMouseMessage));
        upButton.setOnTouchListener(new View.OnTouchListener() {
            boolean comeOn;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new Thread(() -> {
                        comeOn = true;
                        while (comeOn) {
                            sendMessageQuietness(upMouseMessage);
                            try {
                                sleep(20);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "UP_onTouch: ", e);
                            }
                        }
                    }).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    comeOn = false;
                }
                return true;
            }
        });
        //downButton.setOnClickListener((View view) -> sendMessageQuietness(downMouseMessage));
        downButton.setOnTouchListener(new View.OnTouchListener() {
            boolean comeOn;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new Thread(() -> {
                        comeOn = true;
                        while (comeOn) {
                            sendMessageQuietness(downMouseMessage);
                            try {
                                sleep(20);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "DOWN_onTouch: ", e);
                            }
                        }
                    }).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    comeOn = false;
                }
                return true;
            }
        });
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        clickButton = findViewById(R.id.clickButton);
        //leftButton.setOnClickListener((View view) -> sendMessageQuietness(leftMouseMessage));
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            boolean comeOn;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new Thread(() -> {
                        comeOn = true;
                        while (comeOn) {
                            sendMessageQuietness(leftMouseMessage);
                            try {
                                sleep(20);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "LEFT_onTouch: ", e);
                            }
                        }
                    }).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    comeOn = false;
                }
                return true;
            }
        });
        //rightButton.setOnClickListener((View view) -> sendMessageQuietness(rightMouseMessage));
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            boolean comeOn;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new Thread(() -> {
                        comeOn = true;
                        while (comeOn) {
                            sendMessageQuietness(rightMouseMessage);
                            try {
                                sleep(20);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "RIGHT_onTouch: ", e);
                            }
                        }
                    }).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    comeOn = false;
                }
                return true;
            }
        });
        clickButton.setOnClickListener((View view) -> sendMessageQuietness(leftClickMouseMessage));
        clickButton.setOnLongClickListener((View view) -> {
                    sendMessageQuietness(rightClickMouseMessage);
                    return true;
                }
        );

        refreshButton = findViewById(R.id.refreshButton);
        backspaceButton = findViewById(R.id.backspaceButton);
        enterButton = findViewById(R.id.enterButton);
        refreshButton.setOnClickListener((View view) -> sendMessageQuietness(refreshMessage));
        backspaceButton.setOnClickListener((View view) -> sendMessageQuietness(backspaceMessage));
        enterButton.setOnClickListener((View view) -> sendMessageQuietness(enterMessage));

        sendIPButton = findViewById(R.id.sendIPButton);
        IPInput = findViewById(R.id.IPInput);
        IPInput.setText(ServerIP);
        sendIPButton.setOnClickListener((View view) -> {
            String newIP = String.valueOf(IPInput.getText());
            if (newIP.matches("^(((1?\\d?\\d|2[0-4]\\d|25[0-5])\\.){1,3}\\*|((1?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(1?\\d?\\d|2[0-4]\\d|25[0-5]))$")) {
                ServerIP = newIP;
                Toast("Done: ServerIP " + newIP);
                checkConnection();
            } else {
                Toast("Error: Invalid IP.");
                IPInput.setText(ServerIP);
            }
        });
    }

    protected void sendMessage(String message) {
        if (message.isEmpty()) {
            Toast("Message Empty!");
        } else {
            Thread t = new Thread(new SendThread(message));
            t.start();
            Toast("Try to Send Message: " + message);
            sendInput.setText("");
            while (t.isAlive()) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    Log.e(TAG, "SendThread: ", e);
                }
            }
            if (sendSuccess) Toast("Done: Get Server Reply!");
            else Toast("Error: Server Disconnect.");
        }
    }

    protected void sendMessageQuietness(String message) {
        new Thread(new SendThread(message)).start();
    }

    protected void Toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    class SendThread implements Runnable {
        private final String needSendMessage;

        public SendThread(String message) {
            needSendMessage = message;
        }

        @Override
        public void run() {
            byte[] buf = new byte[1024];
            try (DatagramSocket socket = new DatagramSocket()) {
                sendSuccess = false;
                socket.setSoTimeout(200);
                byte[] bufSend = needSendMessage.getBytes();
                DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, InetAddress.getByName(ServerIP), 8888);
                socket.send(packet);
                if (!needSendMessage.equals("\\exit")) {
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(receivePacket);
                        sendSuccess = true;
                    } catch (java.net.SocketTimeoutException e) {
                        Log.e(TAG, "SendThread: ", e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "onCreate: ", e);
            }
        }
    }
}

