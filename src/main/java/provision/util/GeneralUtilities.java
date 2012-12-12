package provision.util;

import java.util.StringTokenizer;
import java.util.Date;
import java.util.Calendar;

/**
 * (C) 2001 Research In Motion Ltd. All Rights Reserved. RIM, Research In Motion -- Reg. U.S. Patent and Trademark
 * Office The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited All materials confidential
 * information of Research In Motion, Limited
 * <p/>
 * User: ctaylor Date: Jun 22, 2005 Time: 11:10:32 AM
 */
public class GeneralUtilities
{
   private static int MAX_RESP_MSG_LEN = 999;

   /**
    * Parses the specified string into tokens and return an array of those tokens
    *
    * @param _string the string to be parsed
    * @param _delimiter the delimiter used to separate tokens
    *
    * @return an array of strings
    */
   public static String[] parseStringIntoArray(String _string, String _delimiter) {
     if (_string == null)
     {
       _string="";
     }
     StringTokenizer tokenizer = new StringTokenizer(_string, _delimiter);

     String[]        result = null;

     if (tokenizer.countTokens() == 0) {
       result      = new String[1];
       result[0]   = _string;
     }
     else {
       result = new String[tokenizer.countTokens()];

       for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
         result[i] = tokenizer.nextToken().trim();
       }
     }

     return result;
   }
   public static String flattenStringArray( String[] s_array, String delimiter )
   {
      StringBuffer flat = new StringBuffer();
      for (int i=0;i<s_array.length;i++)
      {
         flat.append( s_array[i]).append( delimiter );
      }
      return flat.toString();
   }

   public static long calcBusDayExpiry( long timeoutMillis )
   {

      long dayMillis = 1000 * 60 * 60 * 24;
      long expiryMillis = timeoutMillis;

      Date today = new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime( today );
      int todayDOW = calendar.get( Calendar.DAY_OF_WEEK );

      Date rawExpiry = new Date( System.currentTimeMillis() + timeoutMillis );
      Calendar calendar2 = Calendar.getInstance();
      calendar2.setTime( rawExpiry );
      int rawExpiryDOW = calendar2.get( Calendar.DAY_OF_WEEK );

      // note this logic will not work if timeout is greater then one week!!
      switch ( todayDOW )
      {
         case Calendar.SUNDAY:
            expiryMillis = expiryMillis + dayMillis;
            break;
         case Calendar.SATURDAY:
            expiryMillis = expiryMillis + ( 2 * dayMillis );
            break;
         default:
            if ( todayDOW > rawExpiryDOW )
            {
               expiryMillis = expiryMillis + ( 2 * dayMillis );
            }
            break;
      }

      return expiryMillis;
   }

   public static String limitRespMsg( String respMsg, String oldRespMsg, String delim )
   {
      String retMsg = respMsg;
      if ( oldRespMsg != null )
      {
         retMsg = oldRespMsg + delim + respMsg;
         if ( retMsg.length() > MAX_RESP_MSG_LEN )
            retMsg = retMsg.substring( 0, MAX_RESP_MSG_LEN );
      }
      return retMsg;
   }
}
