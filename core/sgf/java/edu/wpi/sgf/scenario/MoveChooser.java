package edu.wpi.sgf.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.wpi.sgf.logic.AnnotatedLegalMove;

/**
 * This class by its {@link #choose(List)} method, will 
 * rank a list of {@link AnnotatedLegalMove} and returns
 * a random move among the ones with maximum Move Strength.
 * 
 * @author Morteza behrooz
 * @version 1.0
 */
public class MoveChooser {

	/**
	 * @param List of {@link AnnotatedLegalMove} someMoves
	 * @return a random {@link AnnotatedLegalMove} among
	 * the ones with the maximum Move Strength from input.
	 */
	public AnnotatedLegalMove choose(List<AnnotatedLegalMove> someMoves){

		List<AnnotatedLegalMove> candidates = 
				new ArrayList<AnnotatedLegalMove>();
	
		if(someMoves == null ||
				someMoves.isEmpty())
			return null;

		Collections.sort(someMoves);

		//debugging log
//		for(AnnotatedLegalMove m : someMoves)
//			System.out.print(m.getAnnotation() + "/");
		
		
		double maxStrength = someMoves.get(
				someMoves.size() - 1).getMoveStrength();
	
		for(AnnotatedLegalMove eachMove : someMoves)
			if(eachMove.getMoveStrength() >= maxStrength)
				candidates.add(eachMove);

		Collections.shuffle(candidates);
		return candidates.get(
				new Random().nextInt(candidates.size()));
		
	}

	//Main method for testing, mainly testing sort
	public static void main(String[] args) {
		
		MoveChooser mc = new MoveChooser();
		
		List<AnnotatedLegalMove> s =
				new ArrayList<AnnotatedLegalMove>();
		s.add(new AnnotatedLegalMove(null, .1));
		s.add(new AnnotatedLegalMove(null, .5));
		s.add(new AnnotatedLegalMove(null, .4));
		s.add(new AnnotatedLegalMove(null, .2));
		s.add(new AnnotatedLegalMove(null, .3));
		s.add(new AnnotatedLegalMove(null, .7));

		AnnotatedLegalMove a = mc.choose(s);
		System.out.println("\n" + a.getMoveStrength());
	}

}
