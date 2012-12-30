package edu.wpi.always.story;

import org.picocontainer.*;
import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.dummy.DummySpeechPerceptor;
import edu.wpi.always.story.schema.*;
import edu.wpi.disco.rt.ComponentRegistry;
import edu.wpi.disco.rt.schema.*;

public class StoryPlugin extends PluginBase {
   
   public StoryPlugin () { 
      // note we have to use longer form of addActivity because we
      // need to specify non-default update intervals below
      addActivity("RecordStory", 0, 0, 0, 0,
         new SchemaRegistry() {

            @Override
            public void register (SchemaManager manager) {
               manager.registerSchema(BackChannelSchema.class, 100, true);
               manager.registerSchema(StorySchema.class, 1000, true);
            }},

         new ComponentRegistry() {

            @Override
            public void register (MutablePicoContainer container) {
               container.as(Characteristics.CACHE).addComponent(StoryManager.class);
               container.as(Characteristics.CACHE).addComponent(DummySpeechPerceptor.class);
            }
         });
   }
   
   /**
    * For testing Story by itself
    */
   public static void main (String[] args) {
      new Always(true, StoryPlugin.class, "RecordStory").start();
   }
  

  
}
