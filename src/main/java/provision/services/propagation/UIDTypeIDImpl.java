package provision.services.propagation;

import java.rmi.server.UID;

/**
 * {@link UID} only version of generating unique id, there is no modification of
 * the string that is originally produced.
 * 
 * @author msimonsen
 *
 */
public class UIDTypeIDImpl implements IDGenerator {

	public String generateID() {
		return new UID().toString();
	}

}
