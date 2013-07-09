package edu.wpi.always.client;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.AdjacencyPairBase;

public class KeyboardOverflowAdjacencyPair<C> extends KeyboardAdjacencyPair<C> {

		public KeyboardOverflowAdjacencyPair(final C context, final Keyboard keyboard) {
		super("The word is too long", context, keyboard);
		System.out.println("SSSSSSSSSSSSSSSSSSSSSSS");
	}

		@Override
		public AdjacencyPair success(String text) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AdjacencyPair cancel() {
			// TODO Auto-generated method stub
			return null;
		}

}
