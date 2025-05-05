package hydraulic;

/**
 * Represents a multisplit element, an extension of the Split that allows many
 * outputs
 * 
 * During the simulation each downstream element will
 * receive a stream that is determined by the proportions.
 */

public class Multisplit extends Split {
	private Element[] outputs;
	private double[] proportions;

	/**
	 * Constructor
	 * 
	 * @param name      the name of the multi-split element
	 * @param numOutput the number of outputs
	 */
	public Multisplit(String name, int numOutput) {
		super(name);
		outputs = new Element[numOutput];
		proportions = new double[numOutput];

		double equalProportion = 1.0 / numOutput;
		for (int i = 0; i < numOutput; i++) {
			proportions[i] = equalProportion;
		}
	}

	@Override
	public void connect(Element elem, int index) {
		if (index >= 0 && index < outputs.length) {
			outputs[index] = elem;
		}
	}

	@Override
	public Element[] getOutputs() {
		return outputs;
	}

	/**
	 * Define the proportion of the output flows w.r.t. the input flow.
	 * 
	 * The sum of the proportions should be 1.0 and
	 * the number of proportions should be equals to the number of outputs.
	 * Otherwise a check would detect an error.
	 * 
	 * @param proportions the proportions of flow for each output
	 */
	public void setProportions(double... proportions) {
		if (proportions.length == this.proportions.length){
			this.proportions = proportions;
		}
	}

	public double[] getProportions(){
		return proportions;
	}

}
