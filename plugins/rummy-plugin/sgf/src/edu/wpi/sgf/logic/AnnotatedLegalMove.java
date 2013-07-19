package edu.wpi.sgf.logic;

/*An annotated move, abstract concept which contains 
 * parameters needed for evaluating moves according to current
 * edu.wpi.sgf.scenario, has a move and those parameters.
 */
public class AnnotatedLegalMove implements Comparable<AnnotatedLegalMove>{
	
	LegalMove move;
	double moveStrength; //Strength: (0, 1)
	//later, add a list of annotations? or hashmap?

	public AnnotatedLegalMove(LegalMove move, double moveStrength){
		this.move = move;
		this.moveStrength = moveStrength;
	}
	
	//many annotations
	public double getAnnotation(){
		return moveStrength;
	}
	
	//every move should have move strength
	public double getMoveStrength(){
		return moveStrength;
	}
	
	public LegalMove getMove(){
		return move;
	}

	@Override
	public int compareTo(AnnotatedLegalMove that) {
		if(this.moveStrength < that.moveStrength)
			return -1;
		else if(this.moveStrength > that.moveStrength)
			return 1;
		else
			return 0;
	}

}
