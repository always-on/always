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
   private boolean needsFocusResource;
   
   @Override
   public void setNeedsFocusResource (boolean focus) {
      this.needsFocusResource = focus;
   }

   public ProposalBuilder (ClientPlugin plugin) {
      this.plugin = plugin;
   }

   public ProposalBuilder () {
      this(null);
   }

   @Override
   public Behavior build () {
      if ( needsFocusResource ) internalAdd(new FocusRequestBehavior());
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
      for (PrimitiveBehavior pb : new SpeechMarkupBehavior(text).getPrimitives(false)) 
         internalAdd(pb);
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

   /**
    * Add specified menu behavior to built behavior.  If choices is null, the
    * {@link MenuBehavior#EMPTY} is added (see documentation).
    */
   public ProposalBuilder showMenu (List<String> choices, boolean twoColumn) {
      internalAdd(choices == null ? MenuBehavior.EMPTY : 
         new MenuBehavior(choices, twoColumn, false));
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
