package hydraulic;

/**
 * Main class that acts as a container of the elements for
 * the simulation of an hydraulics system 
 * 
 */
public class HSystem {

// R1
	private Element[] elements;
	private static final int MAX_ELEMENTS = 100;
	private int count;

	public HSystem(){
		elements = new Element[MAX_ELEMENTS];
		count = 0;
	}

	/**
	 * Adds a new element to the system
	 * 
	 * @param elem the new element to be added to the system
	 */
	public void addElement(Element elem){
		//DONE: to be implemented
		if (count < MAX_ELEMENTS){
			elements[count++] = elem;
		}
	}

	/**
	 * returns the number of element currently present in the system
	 * 
	 * @return count of elements
	 */
	public int size() {
        // DONE: to be implemented
		return count;
    }

	/**
	 * returns the element added so far to the system
	 * 
	 * @return an array of elements whose length is equal to 
	 * 							the number of added elements
	 */
	public Element[] getElements(){
		//DONE: to be implemented
		Element[] result = new Element[count];
		System.arraycopy(elements, 0, result, 0, count);
		return result;
	}

// R4
	/**
	 * starts the simulation of the system
	 * 
	 * The notification about the simulations are sent
	 * to an observer object
	 * 
	 * Before starting simulation the parameters of the
	 * elements of the system must be defined
	 * 
	 * @param observer the observer receiving notifications
	 */
	public void simulate(SimulationObserver observer) {
        for (int i = 0; i < count; i++) {
            Element element = elements[i];
            
            if (element instanceof Source) {
                Source source = (Source) element;
                double outFlow = source.getFlow();
                
                observer.notifyFlow(element.getClass().getSimpleName(), 
                                   element.getName(), 
                                   SimulationObserver.NO_FLOW,
                                   outFlow);
                
                simulateElement(element.getOutput(), outFlow, observer);
            }
        }
    }
    
    private void simulateElement(Element element, double inFlow, SimulationObserver observer) {
        if (element == null) {
            return;
        }
        
        double outFlow = 0.0;
        
        if (element instanceof Tap) {
            Tap tap = (Tap) element;
            outFlow = tap.isOpen() ? inFlow : 0.0;
            
            observer.notifyFlow(element.getClass().getSimpleName(), 
                               element.getName(), 
                               inFlow, 
                               outFlow);
                               
            simulateElement(element.getOutput(), outFlow, observer);
        } 
        else if (element instanceof Split) {
            Split split = (Split) element;
            Element[] outputs = split.getOutputs();
            
            double[] outFlows = new double[outputs.length];
            for (int i = 0; i < outputs.length; i++) {
                outFlows[i] = inFlow / outputs.length;
            }
            
            observer.notifyFlow(element.getClass().getSimpleName(), 
                               element.getName(), 
                               inFlow, 
                               outFlows);
                               
            for (int i = 0; i < outputs.length; i++) {
                simulateElement(outputs[i], outFlows[i], observer);
            }
        } 
        else if (element instanceof Sink) {
            observer.notifyFlow(element.getClass().getSimpleName(), 
                               element.getName(), 
                               inFlow, 
                               SimulationObserver.NO_FLOW);
        }
    }


// R6
	/**
	 * Deletes a previously added element 
	 * with the given name from the system
	 */
	public boolean deleteElement(String name) {
		//TODO: to be implemented
		return false;
	}

// R7
	/**
	 * starts the simulation of the system; if {@code enableMaxFlowCheck} is {@code true},
	 * checks also the elements maximum flows against the input flow
	 * 
	 * If {@code enableMaxFlowCheck} is {@code false}  a normals simulation as
	 * the method {@link #simulate(SimulationObserver)} is performed
	 * 
	 * Before performing a checked simulation the max flows of the elements in thes
	 * system must be defined.
	 */
	public void simulate(SimulationObserver observer, boolean enableMaxFlowCheck) {
		//TODO: to be implemented
	}

// R8
	/**
	 * creates a new builder that can be used to create a 
	 * hydraulic system through a fluent API 
	 * 
	 * @return the builder object
	 */
    public static HBuilder build() {
		return new HBuilder();
    }
}
