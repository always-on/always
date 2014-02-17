package edu.wpi.always.client;

import java.net.*;

public class JsonHPmacAddresses {

   public static void main (String[] args) {
      
      JsonHPmacAddresses jsonHPmacAddress = new JsonHPmacAddresses();
      System.out.println(jsonHPmacAddress.getMacAddress());
   }

   private String getMacAddress() {
      
      NetworkInterface network = null;
      byte[] mac = null;
      
      try {
         InetAddress ip = InetAddress.getLocalHost();
         network = NetworkInterface.getByInetAddress(ip);
         mac = network.getHardwareAddress();
      } catch (SocketException | UnknownHostException e) {
         e.printStackTrace();
         e.printStackTrace();
      }
      
      StringBuilder sb = new StringBuilder();
      
      for (int i = 0; i < mac.length; i++) {
          sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
      }
      
      return sb.toString();
   }
}
