package ch.hsr.isf.serepo.data.restinterface.seitem;

public class Relation {

	private String type;
	
	private String target;
	
	public Relation() {
	}

	public Relation(String type, String target) {
		this.type = type;
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
