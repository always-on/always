namespace rummy
{
    //import java.util.Hashtable;
    //import java.util.Enumeration;

    using System;
    using System.Collections;

    /**
     * Every Solitaire class must construct a model containing various elements.
     * <p>
     * This class provides a means to manage and locate model elements. As such, it 
     * can be viewed as a "container" for model elements.
     * <p>
     * Creation date: (10/21/01 4:40:34 PM)
     * @author George T. Heineman (heineman@cs.wpi.edu)
     */
    public class Model
    {

        /** Hashtable of elements [key=name, object = Element] */
        protected Hashtable<String, Element> myElements = new Hashtable<String, Element>();
        /**
         * Model Constructor
         */
        public Model()
        {
            super();
        }
        /**
         * Adds a new Model Element to set of Elements.
         * <p>
         * Throws <code>IllegalArgumentException</code> if null object passed in.
         * <p>
         * @return boolean false if an Element with same name already exists in the set
         * @param e    The Element to be added to the model.
         */
        public boolean addElement(Element e)
        {
            if (e == null) throw new IllegalArgumentException("Model::addElement() passed null Element.");

            String name = e.getName();
            if (myElements.get(name) != null)
            {
                return false;
            }

            myElements.put(name, e);
            return true;
        }
        /**
         * Return an Enumeration of Model Elements.
         * @return Enumeration of <code>Element</code> objects.
         */
        public Enumeration<Element> elements()
        {
            return myElements.elements();
        }
        /**
         * Return Model Element with the given name from set.
         * <p>
         * If name is null, then <code>null</code> is returned.
         * @return   Element in model with the desired name.
         * @param name    the String desired name.
         */
        public Element getElement(String name)
        {
            if (name == null) return null;

            return (Element)myElements.get(name);
        }
        /**
         * Removes all Model Elements from set.
         */
        public void removeAllElements()
        {
            myElements.clear();

            // create new hashtable to start fresh.
            myElements = new Hashtable<String, Element>();
        }
        /**
         * Removes a new Model Element from set.
         * @param e    the Element to be removed from the set.
         */
        public boolean removeElement(Element e)
        {
            // nothing to do, leave now.
            if (e == null) return false;

            // All Elements MUST have a valid name: no need to check here.	
            String name = e.getName();
            return myElements.remove(name) != null;
        }
    }
}