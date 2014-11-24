package com.crossmobile.phonetracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Created by aloknerurkar on 9/3/14.
 */

class UDP_payload extends Activity{
    Context context;
    UDP_payload(Context c){
        context = c;
    }

    InfoJsonSend json_send;
    LocationManager locationManager;

    public int sendUDP_Packet(int s_port, String ipHost, int d_port, String payload) throws IOException {

        InetAddress address = InetAddress.getByName(ipHost);

        DatagramSocket socket = new DatagramSocket(s_port);

        DatagramPacket packet = new DatagramPacket(payload.getBytes(),payload.length(),address,d_port);

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null){
            json_send = new InfoJsonSend(context,"ip","ip_mob");
            json_send.postMobileUsage(location,
                    payload.length(),"udp","http://192.168.2.18:3000/mobile_usage_stats");
        }

        socket.send(packet);
        socket.disconnect();
        socket.close();

        return 0;
    }

    public int sendUDP_Packet(String ipHost, int d_port, String payload) throws IOException {

        InetAddress address = InetAddress.getByName(ipHost);

        DatagramSocket socket = new DatagramSocket();

        DatagramPacket packet = new DatagramPacket(payload.getBytes(),payload.length(),address,d_port);

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        long bw = payload.length()/1000;
        if(location != null){
            json_send = new InfoJsonSend(context,"ip","ip_mob");
            json_send.postMobileUsage(location,
                    bw,"udp","http://10.0.18.117:3000/mobile_usage_stats");
        }

        socket.send(packet);
        socket.disconnect();
        socket.close();

        return 0;
    }

    public int sendUDP_Packet(int s_port, InetAddress address, int d_port, String payload) throws IOException {

        DatagramSocket socket = new DatagramSocket(s_port);

        DatagramPacket packet = new DatagramPacket(payload.getBytes(),payload.length(),address,d_port);

        socket.send(packet);
        socket.disconnect();
        socket.close();

        return 0;
    }

    public int sendUDP_Packet(InetAddress address, int d_port, String payload) throws IOException {

        DatagramSocket socket = new DatagramSocket();

        DatagramPacket packet = new DatagramPacket(payload.getBytes(),payload.length(),address, d_port);

        socket.send(packet);
        socket.disconnect();
        socket.close();

        return 0;
    }


}
