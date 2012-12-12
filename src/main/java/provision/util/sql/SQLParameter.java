package provision.util.sql;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.sql.*;

public class SQLParameter {
  public static final int IN = 100;
  public static final int OUT = 200;
  public static final int IN_OUT = 300;
  private String paramName;
  private int paramType;
  private int inOutType;
  private String paramValueString;
  private int paramValueInt;
  private double paramValueDouble;
  private float paramValueFloat;
  private long paramValueLong;


  public SQLParameter() {
  }

  public SQLParameter(String paramName, int paramType, String paramValue) {
     this.paramName        = paramName;
     this.paramType        = paramType;

     switch (paramType) {
     case Types.INTEGER:
        this.paramValueInt = Integer.parseInt(paramValue);
        break;
     case Types.VARCHAR:
        this.paramValueString = paramValue;
        break;
     case Types.FLOAT:
        this.paramValueFloat=Float.parseFloat(paramValue);
        break;
     case Types.DOUBLE:
        this.paramValueDouble=Double.parseDouble(paramValue);
        break;
     case Types.BIGINT:
        this.paramValueLong=Long.parseLong(paramValue);
        break;
     case Types.DATE:
        //this.paramValueDate
        break;
     }



  }

  public SQLParameter(int paramType, String paramValue, int inOutType) {
     this.paramType  = paramType;
     this.paramValueString = paramValue;
     this.inOutType = inOutType;
  }

  public SQLParameter(int paramType, int paramValue, int inOutType) {
     this.paramType  = paramType;
     this.paramValueInt = paramValue;
     this.inOutType = inOutType;
  }

  public SQLParameter(int paramType, double paramValue, int inOutType) {
     this.paramType  = paramType;
     this.paramValueDouble = paramValue;
     this.inOutType = inOutType;
  }

  public SQLParameter(int paramType, float paramValue, int inOutType) {
     this.paramType  = paramType;
     this.paramValueFloat = paramValue;
     this.inOutType = inOutType;
  }

  public SQLParameter(int paramType, long paramValue, int inOutType) {
     this.paramType  = paramType;
     this.paramValueLong = paramValue;
     this.inOutType = inOutType;
  }

  public SQLParameter(int paramType, String paramValue) {
	 this(paramType, paramValue, IN);
  }

  public SQLParameter(int paramType, int paramValue) {
	 this(paramType, paramValue, IN);
  }

  public SQLParameter(int paramType, double paramValue) {
	 this(paramType, paramValue, IN);
  }

  public SQLParameter(int paramType, float paramValue) {
	 this(paramType, paramValue, IN);
  }
  public SQLParameter(int paramType, long paramValue) {
	 this(paramType, paramValue, IN);
  }

  public String getParamName() {
    if (paramName == null)
       return paramName;
    else
       return "";
  }

  public void setParamName(String newParamName) {
    paramName = newParamName;
  }

  public void setParamType(int newParamType) {
    paramType = newParamType;
  }

  public int getParamType() {
    return paramType;
  }

  public void setInOutType(int newInOutType) {
    inOutType = newInOutType;
  }

  public int getInOutType() {
    return inOutType;
  }

  public void setStringParamValue(String newParamValue) {
    paramValueString = newParamValue;
  }

  public String getStringParamValue() {
    return paramValueString;
  }

  public void setIntParamValue(int newParamValue) {
    paramValueInt = newParamValue;
  }

  public int getIntParamValue() {
    return paramValueInt;
  }

  public void setfloatParamValue(float newParamValue) {
    paramValueFloat = newParamValue;
  }

  public float getFloatParamValue() {
    return paramValueFloat;
  }

  public void setDoubleParamValue(double newParamValue) {
    paramValueDouble = newParamValue;
  }

  public double getDoubleParamValue() {
    return paramValueDouble;
  }
  public long getLongParamValue() {
    return paramValueLong;
  }

}
