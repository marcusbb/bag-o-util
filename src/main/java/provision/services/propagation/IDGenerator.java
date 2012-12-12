package provision.services.propagation;

/**
 * Abstraction for utility generating unique ids.
 * 
 * No factory method for this interface, but its usage is limited to {@link WLPropagationContextImpl}.
 * 
 * Can be removed for berevity.
 * 
 * @author msimonsen
 *
 */
public interface IDGenerator {

	public String generateID();
	
	
}
