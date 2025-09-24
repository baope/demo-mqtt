//package com.example.demo;
//
//import com.rimelink.data.common.Connection;
//import com.rimelink.data.common.messages.UplinkMessage;
//import com.rimelink.data.mqtt.Client;
//
//public class demoRowaMqtt {
//
//    public static void main(String[] args) throws Exception{
//        Client client = new Client("lorawan.timeddd.com", "36", "3c83e380-a393-4301-ab93-62c16159b04e");
//
//        client.onMessage((String devEUI, UplinkMessage data) -> {
//            try {
//                byte[] received = data.getData();
//                System.out.println("received: devEUI=" + devEUI + " \r\n data: " + bytesToHex(received));
//
//                System.out.println("send led ");
//                client.send(devEUI, "led".getBytes(), 1);
//            } catch (Exception ex) {
//                System.out.println("exception: " + ex.getMessage());
//            }
//        });
//
//        client.onError((Throwable _error) -> System.err.println("error: " + _error.getMessage()));
//
//        client.onConnected((Connection _client) -> System.out.println("connected !"));
//
//        client.start();
//    }
//
//    public static String bytesToHex(byte[] bytes) {
//        StringBuffer sb = new StringBuffer();
//        for(int i = 0; i < bytes.length; i++) {
//            String hex = Integer.toHexString(bytes[i] & 0xFF);
//            if(hex.length() < 2) sb.append(0);
//
//            sb.append(hex);
//            sb.append(" ");
//        }
//        return sb.toString();
//    }
//}