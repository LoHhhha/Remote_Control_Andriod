package com.loha.remotecontrol;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class SendFuture implements Callable<String> {
    private final String needSendMessage;
    private final String ServerIP;

    public SendFuture(String message, String IP) {
        needSendMessage = message;
        ServerIP = IP;
    }

    @Override
    public String call() {
        String ret = null;
        byte[] buf = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(200);
            byte[] bufSend = needSendMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, InetAddress.getByName(ServerIP), 8888);
            socket.send(packet);
            if (!needSendMessage.equals("\\exit")) {
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(receivePacket);
                    ret = new String(buf, 0, receivePacket.getLength());
                } catch (java.net.SocketTimeoutException e) {
                    Log.e(TAG, "SendThread: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
        return ret;
    }
}
