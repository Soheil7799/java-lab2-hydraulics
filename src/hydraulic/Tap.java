package hydraulic;

/**
 * Represents a tap that can interrupt the flow.
 * 
 * The status of the tap is defined by the method
 * {@link #setOpen(boolean) setOpen()}.
 */

public class Tap extends Element {
	private boolean open;
	/**
	 * Constructor
	 * @param name name of the tap element
	 */
	public Tap(String name) {
		super(name);
	}

	/**
	 * Set whether the tap is open or not. The status is used during the simulation.
	 *
	 * @param open opening status of the tap
	 */
	public void setOpen(boolean open){
		// TODO: to be implemented
		this.open = open;
	}
	public boolean isOpen(){
		return open;
	}
	
}
