package provision.services.propagation;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.Message;

/**
 * Interceptor for JMS MDB consumption, that will add propagation to 
 * local implementation (non-weblogic) version
 * 
 * @author msimonsen
 *
 */
public class JMSPropagationInterceptor {

	@AroundInvoke
	public Object propagate(InvocationContext ctx) throws Exception {
		
		try {
			Object []params = ctx.getParameters();
			if (params.length ==1 && params[0] instanceof Message) {
				Message jmsMsg = (Message)params[0];
				String reqId = jmsMsg.getStringProperty(PropagationContext.REQUEST_ID_KEY);
				PropagationContextFactory.get().setId(reqId);
			}
		}catch (Exception e) {
			//TODO
			e.printStackTrace();
		}
		return ctx.proceed();
				
	}
}
