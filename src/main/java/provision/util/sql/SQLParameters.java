package provision.util.sql;

import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class SQLParameters {

  private Vector vParams = new Vector();

  public SQLParameters() {
  }

 /** This method will add SQLParameter object in the vector
 * @param @see provision.web.sql.SQLParameter
 * @return void
 * @throws
 */
  public void addParameter(SQLParameter SQLParamObject) {
     vParams.addElement(SQLParamObject);
  }

 /** This method will create SQLParameter object then add it to the vector
  * this method is very useful when creating 1 or 2 parameter
  * @param paramType an String
  * @param paramValue an int
  * @return void
  */
  public void addParameter(int paramType, String paramValue) {
       addParameter(new SQLParameter(paramType,paramValue));
  }

  public Vector getParameterObject() {
      return vParams;
  }

  public void Clear() {
     if (vParams != null)
       vParams.removeAllElements();
  }

}