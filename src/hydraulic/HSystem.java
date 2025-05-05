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

	public HSystem() {
		elements = new Element[MAX_ELEMENTS];
		count = 0;
	}

	/**
	 * Adds a new element to the system
	 * 
	 * @param elem the new element to be added to the system
	 */
	public void addElement(Element elem) {
		// DONE: to be implemented
		if (count < MAX_ELEMENTS) {
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
	 *         the number of added elements
	 */
	public Element[] getElements() {
		// DONE: to be implemented
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
		} else if (element instanceof Split) {
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
		} else if (element instanceof Sink) {
			observer.notifyFlow(element.getClass().getSimpleName(),
					element.getName(),
					inFlow,
					SimulationObserver.NO_FLOW);
		} else if (element instanceof Multisplit) {
			Multisplit ms = (Multisplit) element;
			Element[] outputs = ms.getOutputs();
			double[] proportions = ms.getProportions();

			double[] outFlows = new double[outputs.length];
			for (int i = 0; i < outputs.length; i++) {
				outFlows[i] = inFlow * proportions[i];
			}

			observer.notifyFlow(element.getClass().getSimpleName(),
					element.getName(),
					inFlow,
					outFlows);

			for (int i = 0; i < outputs.length; i++) {
				if (outputs[i] != null) {
					simulateElement(outputs[i], outFlows[i], observer);
				}
			}
		}
	}

	// R6
	/**
	 * Deletes a previously added element
	 * with the given name from the system
	 */
	public boolean deleteElement(String name) {
		Element toDelete = null;
		int index = -1;
		for (int i = 0; i < count; i++) {
			if (elements[i].getName().equals(name)) {
				toDelete = elements[i];
				index = i;
				break;
			}
		}
		if (toDelete == null) {
			return false;
		}
		if (toDelete instanceof Split) {
			Element[] outputs = toDelete.getOutputs();
			int connectedOutputs = 0;

			for (Element output : outputs) {
				if (output != null) {
					connectedOutputs++;
				}
			}
			if (connectedOutputs > 1) {
				return false;
			}
		}

		Element previous = null;
		for (int i = 0; i < count; i++) {
			if (elements[i] != toDelete) {
				// Check if this element is connected to the one we want to delete
				if (elements[i].getOutput() == toDelete) {
					previous = elements[i];
					break;
				}

				// Also check in case it's a Split or Multisplit
				if (elements[i] instanceof Split) {
					Element[] outputs = elements[i].getOutputs();
					for (int j = 0; j < outputs.length; j++) {
						if (outputs[j] == toDelete) {
							previous = elements[i];
							break;
						}
					}
				}
			}
		}
		Element next = null;
		if (toDelete instanceof Split) {
			Element[] outputs = toDelete.getOutputs();
			for (Element output : outputs) {
				if (output != null) {
					next = output;
					break;
				}
			}
		} else {
			next = toDelete.getOutput();
		}

		if (previous != null && next != null) {
			if (previous instanceof Split) {
				Element[] outputs = previous.getOutputs();
				for (int i = 0; i < outputs.length; i++) {
					if (outputs[i] == toDelete) {
						previous.connect(next, i);
						break;
					}
				}
			} else {
				previous.connect(next);
			}
		}
		for (int i = index; i < count - 1; i++) {
			elements[i] = elements[i + 1];
		}
		count--;

		return true;
	}

	// R7
	/**
	 * starts the simulation of the system; if {@code enableMaxFlowCheck} is
	 * {@code true},
	 * checks also the elements maximum flows against the input flow
	 * 
	 * If {@code enableMaxFlowCheck} is {@code false} a normals simulation as
	 * the method {@link #simulate(SimulationObserver)} is performed
	 * 
	 * Before performing a checked simulation the max flows of the elements in thes
	 * system must be defined.
	 */
	public void simulate(SimulationObserver observer, boolean enableMaxFlowCheck) {
		if (!enableMaxFlowCheck) {
			simulate(observer);
			return;
		}

		for (int i = 0; i < count; i++) {
			Element element = elements[i];

			if (element instanceof Source) {
				Source source = (Source) element;
				double outFlow = source.getFlow();

				observer.notifyFlow(element.getClass().getSimpleName(),
						element.getName(),
						SimulationObserver.NO_FLOW,
						outFlow);

				simulateElementWithCheck(element.getOutput(), outFlow, observer);
			}
		}
	}

	private void simulateElementWithCheck(Element element, double inFlow, SimulationObserver observer) {
		if (element == null) {
			return;
		}
		
		if (!(element instanceof Source) && element.getMaxFlow() > 0 && inFlow > element.getMaxFlow()) {
			observer.notifyFlowError(element.getClass().getSimpleName(), 
								  element.getName(), 
								  inFlow, 
								  element.getMaxFlow());
		}
		
		double outFlow = 0.0;
		
		if (element instanceof Tap) {
			Tap tap = (Tap) element;
			outFlow = tap.isOpen() ? inFlow : 0.0;
			
			observer.notifyFlow(element.getClass().getSimpleName(), 
							   element.getName(), 
							   inFlow, 
							   outFlow);
							   
			simulateElementWithCheck(element.getOutput(), outFlow, observer);
		} 
		else if (element instanceof Split) {
			Split split = (Split) element;
			Element[] outputs = split.getOutputs();
			
			if (element instanceof Multisplit) {
				Multisplit ms = (Multisplit) element;
				double[] proportions = ms.getProportions();
				
				double[] outFlows = new double[outputs.length];
				for (int i = 0; i < outputs.length; i++) {
					outFlows[i] = inFlow * proportions[i];
				}
				
				observer.notifyFlow(element.getClass().getSimpleName(), 
								   element.getName(), 
								   inFlow, 
								   outFlows);
								   
				for (int i = 0; i < outputs.length; i++) {
					if (outputs[i] != null) {
						simulateElementWithCheck(outputs[i], outFlows[i], observer);
					}
				}
			} 
			else {
				double[] outFlows = new double[outputs.length];
				for (int i = 0; i < outputs.length; i++) {
					outFlows[i] = inFlow / outputs.length;
				}
				
				observer.notifyFlow(element.getClass().getSimpleName(), 
								   element.getName(), 
								   inFlow, 
								   outFlows);
								   
				for (int i = 0; i < outputs.length; i++) {
					if (outputs[i] != null) {
						simulateElementWithCheck(outputs[i], outFlows[i], observer);
					}
				}
			}
		} 
		else if (element instanceof Sink) {
			observer.notifyFlow(element.getClass().getSimpleName(), 
							   element.getName(), 
							   inFlow, 
							   SimulationObserver.NO_FLOW);
		}
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
