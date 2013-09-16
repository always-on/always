package edu.wpi.always.srummy.logic;

import edu.wpi.sgf.logic.LegalMove;

public class SrummyAnnotatedLegalMove extends 
				edu.wpi.sgf.logic.AnnotatedLegalMove{

	public SrummyAnnotatedLegalMove(LegalMove move, double moveStrength) {
		super(move, moveStrength);
	}
	

}
