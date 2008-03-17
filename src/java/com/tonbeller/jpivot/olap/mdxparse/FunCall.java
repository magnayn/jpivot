/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.olap.mdxparse;

import org.apache.log4j.Logger;

/**
 * @author hh
 *
 */
public class FunCall implements Exp {

  static Logger logger = Logger.getLogger(FunCall.class);

  // Syntactip types
  public static final int TypeFunction = 0;
  public static final int TypeProperty = 1; // ???
  public static final int TypeMethod = 2;
  public static final int TypeInfix = 3;
  public static final int TypePrefix = 4;
  public static final int TypeBraces = 5;
  public static final int TypeParentheses = 6;
  public static final int TypeCase = 7;
  public static final int TypeMask = 0xFF;
  public static final int TypePropertyQuoted = TypeProperty | 0x100;
  public static final int TypePropertyAmpQuoted = TypeProperty | 0x200;

  private int syntacticType;
  private String function;
  private Exp[] args;

  ParsedQuery pQuery = null; // needed by Parameter FunCall 

  public FunCall(String fun, Exp[] args) {
    this(fun, args, TypeFunction);
  }

  public FunCall(String fun, Exp[] args, int syntacticType) {
    this.function = fun;
    this.args = args;
    this.syntacticType = syntacticType;
  }

  /**
   * format to MDX
   */
  public String toMdx() {
    if ((this.isCallTo("Parameter") || this.isCallTo("ParamRef"))
         && (pQuery.getParaMap().size() > 0)) {
      // parameters are evaluated to MDX
      return evaluateParameter();
    }

    StringBuffer sb = new StringBuffer();

    // "+" instead of Union yields much better readable MDX
    // however
    // - does not work with SAP
    // - is not compatible with Mondrian
    /* sorry
    if (this.isCallTo("Union")) {
      // render Union as "+"
      sb.append(args[0].toMdx());
      sb.append(" + ");
      sb.append(args[1].toMdx());
      return sb.toString();
    }
    */
    
    boolean isFollow = false;

    switch (syntacticType) {
      case TypeFunction : // f(a, b, c)
        sb.append(function);
        sb.append("(");
        for (int i = 0; i < args.length; i++) {
          if (isFollow)
            sb.append(", ");
          isFollow = true;
          sb.append(args[i].toMdx());
        }
        sb.append(")");
        break;

      case TypeBraces : // { a, b, c }
        sb.append("{");
        for (int i = 0; i < args.length; i++) {
          if (isFollow)
            sb.append(", ");
          isFollow = true;
          sb.append(args[i].toMdx());
        }
        sb.append("}");
        break;

      case TypeParentheses : // (a, b, c)
        sb.append("(");
        for (int i = 0; i < args.length; i++) {
          if (isFollow)
            sb.append(", ");
          isFollow = true;
          sb.append(args[i].toMdx());
        }
        sb.append(")");
        break;

      case TypePrefix : // NOT a
        sb.append(function);
        sb.append(" ");
        sb.append(args[0].toMdx());
        break;

      case TypeInfix : //  a + b
        sb.append(args[0].toMdx());
        sb.append(" ");
        sb.append(function);
        sb.append(" ");
        sb.append(args[1].toMdx());
        break;

      case TypeProperty : // a.b
      case TypePropertyQuoted :
      case TypePropertyAmpQuoted :
        sb.append(args[0].toMdx());
        sb.append(".");
        sb.append(function);
        break;

      case TypeMethod:
        sb.append(args[0].toMdx());
        sb.append(".");
        sb.append(function);
        sb.append("(");
        sb.append(args[1].toMdx());
        sb.append(")");
        break;

      default :
        throw new IllegalArgumentException("unexpected FunCall syntatic type");

    }
    return sb.toString();
  }

  /**
   * 
   * @see java.lang.Object#clone()
   */
  public Object clone() {
    Exp[] cloneArgs = new Exp[args.length];
    for (int i = 0; i < cloneArgs.length; i++) {
      cloneArgs[i] = (Exp) args[i].clone();
    }
    return new FunCall(function, cloneArgs, syntacticType);
  }

  /**
   * compare function name ignoring case
   * @param fName
   * @return boolean
   */
  public boolean isCallTo(String fName) {
    return (fName.compareToIgnoreCase(function) == 0);
  }

  /**
   * Returns the args.
   * @return Exp[]
   */
  public Exp[] getArgs() {
    return args;
  }

  /**
   * @return
   */
  public String getFunction() {
    return function;
  }

  /**
   * put parameter value to MDX
   */
  private String evaluateParameter() {
    Literal eName = (Literal) args[0];
    String paraName = eName.stringValue();
    Parameter param = (Parameter) pQuery.paraMap.get(paraName.toUpperCase());
    if (param == null) {
      // should not occur
      logger.error("could not find parameter " + paraName);
      return ("Parameter( \"" + paraName + "\" )"); // MDX parse will fail here  
    }
    int type = param.getType();
    if (type == Parameter.TYPE_NUMERIC) {
      Object value = param.getOValue();
      return value.toString(); // Integer or double
    } else if (type == Parameter.TYPE_STRING) {
      String str = (String) param.getOValue();
      return "\"" + str + "\"";
    } else {
      // member assumed
      String str = (String) param.getOValue();
      return str;

    }

  }

  /**
   * @see com.tonbeller.jpivot.olap.mdxparse.Exp#accept
   */
  public void accept(ExpVisitor visitor) {
    visitor.visitFunCall(this);
  }

} // End FunCall
