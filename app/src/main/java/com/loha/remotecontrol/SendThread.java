package com.loha.remotecontrol;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendThread implements Runnable {
    private final String needSendMessage;
    private final String ServerIP;

    public SendThread(String message, String IP) {
        needSendMessage = message;
        ServerIP = IP;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(200);
            byte[] bufSend = needSendMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, InetAddress.getByName(ServerIP), 8888);
            socket.send(packet);
        } catch (Exception e) {
            Log.e(TAG, "Mouse : ", e);
        }
    }
}