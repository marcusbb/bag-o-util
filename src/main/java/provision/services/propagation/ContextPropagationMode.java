package provision.services.propagation;

/**
 * Supported propagation boundary conditions:
 * They are tied 1 to 1 to the weblogic concept of {@link PropagationMode},
 * but not all are enumerated. 
 * 
 * @author msimonsen
 *
 */
public enum ContextPropagationMode {
   
	//replaced weblogic class constants with ints, to be able to remove tie to weblogic depenedencies for non weblogic implementations of propogation 
	ALL(212), //PropagationMode.GLOBAL
	LOCAL_AND_RMI(1 //PropagationMode.LOCAL - Propagate a WorkContext only for the scope of the current thread.  
				 | 4  //PropagationMode.RMI - Propagate a WorkContext across RMI invocations. 
				 ),
	NO_SOAP(256); //PropagationMode.ONEWAY
	
	private ContextPropagationMode(int mode) {
		this.propagationMode = mode;
	}
	protected int getMode() {
		return propagationMode;
	}
	private int propagationMode;
	
}
