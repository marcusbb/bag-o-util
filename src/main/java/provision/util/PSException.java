/*
 * (C) 2001 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 */

package provision.util;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This exception is thrown for all problems that occur in the PS code
 * related to functional causes.
 */
public class PSException extends Exception implements java.io.Serializable {
    private String code;
    protected String msg;


    public PSException(String code) {
		  super();
		  //workaround for weblogic bug of remote exception throwing, codes shouldn't consist of spaces/new lines etc
		  //so discarding everything after first space, to remove the stack trace that gets appended to
		  //this call
		  if(code != null){
			 StringTokenizer tok = new StringTokenizer(code);
			 if(tok.hasMoreTokens())
                this.code = tok.nextToken();
		  }
	}

    /* deprecated */
    public PSException(int pserr) {
	}

    public PSException(String code, String msg) {
		super(msg);
        this.msg=msg;
        this.code = code;
	}

   public PSException(String code, String msg,Throwable t) {
		super(msg,t);
        this.msg=msg;
        this.code = code;
	}

    /* deprecated */
    public PSException(int pserr, String cause) {
	}

    public String getCode() {
		return code;
    }
    /*
	  Workaround for weblogic6.1 bug remote exception throwing doing some funny stuff by calling
	  PSException(String code) and sending as a parameter the result from getMessage call concatenated with
	  the stack trace,
	  by overloading getMessage in PSException and returning the code, the objects code
	  will be reset properly again.
	*/
	public String getMessage()
    {
      StringBuffer message;
      if (this.msg==null)
      {
         if (this.code != null)
         {
            message = new StringBuffer(this.code);
         }else
         {
            message = new StringBuffer();
         }
      } else
      {
         message = new StringBuffer(this.msg);
      }


       if (this.getCause() != null )
       {
          message.append( " ::Caused by [")
                  .append(this.getCause().getClass().getName())
                  .append("] ")
                  .append( this.getCause().getMessage());
       }
       return message.toString();
   }
}
