package provision.services.propagation;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.Message;

import provision.services.logging.Logger;

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
		PropagationContext pCtx = PropagationContextFactory.get();
		Object ret = null;
		
		try {
			Object []params = ctx.getParameters();
			if (params.length ==1 && params[0] instanceof Message) {
				Message jmsMsg = (Message)params[0];
				String reqId = jmsMsg.getStringProperty(PropagationContext.REQUEST_ID_KEY);
				pCtx.setId(reqId);
				ret = ctx.proceed();
			}
		}catch (Exception e) {
			Logger.error(getClass().getName(), "JMS_INTERCEPTOR_ER","msg",e.getMessage(),e);
		}finally {
			pCtx.unSetContext();
		}
		
		
		return ret;
				
	}
}
