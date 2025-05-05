package hydraulic;

/**
 * Represents a split element, a.k.a. T element
 * 
 * During the simulation each downstream element will
 * receive a stream that is half the input stream of the split.
 */

public class Split extends Element {
	private Element[] outputs;

	/**
	 * Constructor
	 * @param name name of the split element
	 */

	public Split(String name) {
		super(name);
		outputs = new Element[2];
	}
	@Override
	public void connect(Element elem, int index){
		if (index >= 0 && index < outputs.length){
			outputs[index] = elem;
		}
	}

	@Override
	public void connect(Element elem){
		connect(elem,0);
	}

	@Override
	public Element[] getOutputs(){
		return outputs;
	}

	@Override
	public Element getOutput(){
		return outputs[0];
	}
}
