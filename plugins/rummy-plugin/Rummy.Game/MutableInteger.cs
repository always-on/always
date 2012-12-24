/**
 * An Integer-like object that can be set and accessed.
 * Creation date: (10/3/01 10:47:21 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class MutableInteger : Element {

	/** The value being managed. */
	protected int theInteger = 0;

	/** Used to uniquely identify named MutableIntegers (if no name is assigned). */
	private static int mutableNameCounter = 1;
	
	/**
	 * MutableInteger constructor comment.
	 */
	public MutableInteger(int i) {
		this(new String("MutableInteger" + mutableNameCounter++), i);

	}
	
	/**
	 * MutableInteger constructor comment.
	 * <p>
	 * @since V1.6.8
	 */
	public MutableInteger(String name, int i) {
		super();

		if (name == null) {
			name = new String("MutableInteger" + mutableNameCounter++);
		}

		// set the name. 
		setName(name);

		// keep track of the integer.
		theInteger = i;
	}
	
	/**
	 * Retrieve value.
	 * <p>
	 * @return int
	 */
	public int getValue() {
		return theInteger;
	}
	
	/**
	 * Update the value for this entity.
	 * <p>
	 * Generates modelChanged action if newValue is different from oldValue
	 * <p>
	 * @param newTheInteger int
	 */
	public void setValue(int newTheInteger) {
		// do nothing if the same.
		if (newTheInteger == theInteger)
			return;

		theInteger = newTheInteger;
		hasChanged(); // we have changed state.
	}
	
	/**
	 * Return value of Mutable Integer together with its Parent's toString().
	 * <p>
	 * @return java.lang.String
	 */
	public String toString() {
		return super.toString() + "=" + Integer.toString(theInteger);
	}
	
	/**
	 * Increment the value for this entity.
	 * <p>
	 * Generates modelChanged action if newValue is different from oldValue
	 * <p>
	 * @param delta   the value by which to increment (or decrement if negative).
	 * @since V2.0
	 */
	public void increment(int delta) {
		// do nothing if the same.
		if (delta == 0)
			return;

		theInteger += delta;
		hasChanged(); // we have changed state.
	}	
}
