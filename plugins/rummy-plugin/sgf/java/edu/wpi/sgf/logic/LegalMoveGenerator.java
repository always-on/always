package edu.wpi.sgf.logic;

import java.util.List;

public interface LegalMoveGenerator {

	public List<LegalMove> generate(GameLogicState state);

}
