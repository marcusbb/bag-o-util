package provision.services.propagation;

import provision.services.logging.Logger;

public class RequestIDTypeGenerator implements IDGenerator {

	
	public String generateID() {
		try {
			return new RequestID().toString();
		} catch (Exception e) {
			Logger.error(RequestIDTypeGenerator.class.getName(), "generateID", null,e.getMessage(),e);
			
		}
		return null;
	}

}
