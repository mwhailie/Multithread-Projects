package akkaHW2017F.messages;

public class FirstRespondMessage {
	
	private final Integer result;
	
	public FirstRespondMessage(Integer result) {
		this.result = result;
	}

	public Integer getResult() {
		return result;
	}

}
