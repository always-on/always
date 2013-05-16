package edu.wpi.always.cm.primitives;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class AudioFileBehavior extends PrimitiveBehavior {

   private final URL resourceURL;

   public AudioFileBehavior (URL resourceURL) {
      this.resourceURL = resourceURL;
   }

   @Override
   public Resource getResource () {
      return AgentResources.SPEECH;
   }

   @Override
   public boolean equals (Object o) {
      if ( o == this )
         return true;
      if ( !(o instanceof AudioFileBehavior) )
         return false;
      AudioFileBehavior theOther = (AudioFileBehavior) o;
      return this.resourceURL.getPath().equals(theOther.resourceURL.getPath());
   }

   @Override
   public int hashCode () {
      return resourceURL.getPath().hashCode();
   }

   public AudioInputStream getAudioStream () throws IOException,
         UnsupportedAudioFileException {
      return AudioSystem.getAudioInputStream(resourceURL);
   }

   @Override
   public String toString () {
      return "AudioFile(" + resourceURL.getPath() + ')';
   }
}
