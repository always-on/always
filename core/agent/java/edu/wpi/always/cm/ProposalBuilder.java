package edu.wpi.always.cm;

import com.google.common.collect.Lists;
import edu.wpi.always.client.ClientPlugin;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuBehavior;
import java.util.*;

public class ProposalBuilder implements BehaviorBuilder {
   
   private final ArrayList<PrimitiveBehavior> primitives = Lists.newArrayList();
   private final ClientPlugin plugin;
   private BehaviorMetadataBuilder metadataBuilder = new BehaviorMetadataBuilder();

   public ProposalBuilder (ClientPlugin plugin) {
      this.plugin = plugin;
   }

   public ProposalBuilder () {
      this(null);
   }

   @Override
   public Behavior build () {
      return Behavior.newInstance(primitives);
   }

   private void internalAdd (PrimitiveBehavior pb) {
      Resource r = pb.getResource();
      for (PrimitiveBehavior b : primitives) {
         if ( b.getResource().equals(r) ) {
            primitives.remove(b);
            break;
         }
      }
      primitives.add(pb);
   }

   public boolean isEmpty () { return primitives.isEmpty(); }
   
   public ProposalBuilder say (String text) {
      internalAdd(new SpeechBehavior(text));
      return this;
   }

   public ProposalBuilder pluginAction (String actionName, Resource resource) {
      internalAdd(new PluginSpecificBehavior(plugin, actionName, resource));
      return this;
   }

   public ProposalBuilder gazeAtUser () {
      internalAdd(new FaceTrackBehavior());
      return this;
   }

   public ProposalBuilder showMenu (List<String> choices, boolean twoColumn) {
      internalAdd(new MenuBehavior(choices, twoColumn, false));
      return this;
   }
   
   public ProposalBuilder idle () {
      return say(".").gazeAtUser();
   }
   
   @Override
   public BehaviorMetadata getMetadata () {
      return metadataBuilder.build();
   }

   public void setMetadata (BehaviorMetadataBuilder metadata) {
      metadataBuilder = metadata;
   }

   public BehaviorMetadataBuilder metadataBuilder () {
      return metadataBuilder;
   }
}
