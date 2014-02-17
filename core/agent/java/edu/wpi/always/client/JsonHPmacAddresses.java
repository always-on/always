package edu.wpi.always.client;

import org.json.simple.*;
import java.io.InputStreamReader;
import java.net.*;

public class JsonHPmacAddresses {

   private boolean hpMachine;

   public JsonHPmacAddresses () {

      hpMachine = this.checkMacAddress(this.getMacAddress());
   }

   public static void main (String[] args) {

      new JsonHPmacAddresses();
   }

   public boolean isHpMachine () {
      return hpMachine;
   }

   private String getMacAddress () {

      NetworkInterface network = null;
      byte[] mac = null;

      StringBuilder sb = new StringBuilder();

      try {
         mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost())
               .getHardwareAddress();
      } catch (SocketException | UnknownHostException e) {
         e.printStackTrace();
      }

      for (int i = 0; i < mac.length; i++)
         sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-"
            : ""));

      return sb.toString();
   }

   private boolean checkMacAddress (String MacAddress) {

      JSONObject jsonObject = (JSONObject) JSONValue
            .parse(new InputStreamReader(getClass().getResourceAsStream(
                  "/HPmacAddresses.json")));

      for (int i = 0; i < jsonObject.size(); i++)
         if ( MacAddress.equals((String) jsonObject.get("HP0" + (i + 1))) )
            return true;

      return false;
   }
}
