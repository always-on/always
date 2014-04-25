package edu.wpi.always.client;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.disco.rt.util.Utils;

public class ClientProxy {

   private final List<ClientProxyObserver> observers;
   private final UIMessageDispatcher dispatcher;

   public ClientProxy (UIMessageDispatcher dispatcher) {
      this.dispatcher = dispatcher;
      observers = new CopyOnWriteArrayList<ClientProxyObserver>();
      registerOnDispatcher();
   }

   private void registerOnDispatcher () {
      this.dispatcher.registerReceiveHandler("done", new MessageHandler() {

         @Override
         public void handleMessage (JsonObject body) {
            String data = "";
            JsonElement dataElem = body.get("data");
            if ( dataElem != null && dataElem.isJsonPrimitive() )
               data = dataElem.getAsString();
            fireDoneMessage(body.get("action").getAsString(), data);
         }
      });
      this.dispatcher.registerReceiveHandler("menu_selected",
            new MessageHandler() {

               @Override
               public void handleMessage (JsonObject body) {
                  fireMenuSelectedMessage(body.get("text").getAsString()); 
               }
            });
   }

   private void enqueue (String messageType) {
      enqueue(new Message(messageType));
   }
   
   private void enqueue (Message message) {
      dispatcher.send(message);
   }

   private void enqueue (String messageType, HashMap<String, String> body) {
      enqueue(new Message(messageType, body));
   }

   public void say (String text) {
      HashMap<String, String> p = Maps.newHashMap();
      p.put("text", text);
      enqueue("speech", p);
   }

   // ideally should initialize these from neutral positions of config,
   // but it doesn't really matter since only happens once at system startup
   // and face tracker will automatically sync to this position
   private float hor, ver;
   
   //TODO need to get information back for gaze changes
   //     do to html markup
   
   public void setGazeHor (float hor) { this.hor = hor; }
   public void setGazeVer (float ver) { this.ver = ver; }
   
   public float getGazeHor () { return hor; }
   public float getGazeVer () { return ver; }
   
   public void gaze (float hor, float ver) {
      // allow fudge for round-off and coercion
      if ( Math.abs(hor) > 1.01f || Math.abs(ver) > 1.01f )
         throw new IllegalArgumentException("Gaze out of bounds: horizontal=\""
               +hor+"\" vertical=\""+ver+"\"");
      HashMap<String, String> p = Maps.newHashMap();
      p.put("horizontal", Float.toString(hor));
      p.put("vertical",	Float.toString(ver));
      enqueue("gaze", p);
      this.hor = hor; this.ver = ver;
   }
 
   public void express (AgentFaceExpression expression) {
      HashMap<String, String> p = Maps.newHashMap();
      p.put("expression", expression.toString());
      enqueue("express", p);
   }

   public void setAgentVisible (boolean visible) {
      // never make agent visible for Reeti-only mode
      if ( !visible || Always.getAgentType() != AgentType.Reeti ) {
         HashMap<String, String> p = Maps.newHashMap();
         p.put("status", Boolean.toString(visible));
         enqueue("setVisible", p);
      }
   }
   
   private static Robot robot;
   
   static { 
      try { robot = new Robot(); } 
      catch (AWTException e) { edu.wpi.cetask.Utils.rethrow("Cannot create robot", e); }
   }
   
   public void setScreenVisible (boolean visible) {
      // use blank screen saver because reacts more quickly to touch than turning display off
      try {
         if ( visible ) robot.mousePress(InputEvent.BUTTON1_MASK);           
         else Runtime.getRuntime().exec( "bin\\nircmdc screensaver");
      } catch (IOException e) { Utils.lnprint(System.out, "Error running nircmdc: "+e); }
   }
   
   public static float ZOOM = 1.6f;
   
   public void zoom (float zoom) {
      // need some text or else IVONA ignores the command
      say(" hm <CAMERA ZOOM=\""+ZOOM+"\"/>");
   }
   
   public void reetiIP (String address) {
      HashMap<String, String> p = Maps.newHashMap();
      p.put("address", address);
      enqueue("reetiIP", p);
   }
   
   public void addObserver (ClientProxyObserver observer) {
      if ( observer == null )
         throw new IllegalArgumentException("observer is null");
      observers.add(observer);
   }

   public void removeObserver (ClientProxyObserver observer) {
      observers.remove(observer);
   }

   public void fireDoneMessage (String action, String data) {
      for (ClientProxyObserver o : observers) {
         o.notifyDone(this, action, data);
      }
   }

   private void fireMenuSelectedMessage (String text) {
      for (ClientProxyObserver o : observers) {
         o.notifyMenuSelected(this, text);
      }
   }
   
   public void showMenu (List<String> items, boolean twoColumn, boolean extension) {
      JsonArray menus = new JsonArray();
      if ( items != null ) for (String s : items) menus.add(new JsonPrimitive(s));
      JsonObject body = new JsonObject();
      body.add("menus", menus);
      body.addProperty("twoColumn", twoColumn);
      body.addProperty("extension", extension);
      enqueue(new Message("show_menu", body));
   }

   public void stopSpeech () {
      enqueue("stop_speech");
   }

   public void idle (boolean enable) {
      HashMap<String, String> p = Maps.newHashMap();
      p.put("enabled", enable ? "true" : "false");
      enqueue("idle", p);
   }
   
   public void startPlugin (String pluginName, InstanceReuseMode mode, JsonObject params) {
      ClientPluginUtils.startPlugin(dispatcher, pluginName, mode, params);
   }
   
   public void showPlugin (String pluginName) { 
      ClientPluginUtils.showPlugin(dispatcher, pluginName);
   }

   public void hidePlugin () {
      ClientPluginUtils.hidePlugin(dispatcher);
   }
}
