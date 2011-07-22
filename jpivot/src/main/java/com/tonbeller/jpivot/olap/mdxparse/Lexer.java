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

/**
 * @author hh
 * 
 * public Wrapper for generated Yylex
 */

public class Lexer extends Yylex {

  public Lexer(java.io.Reader reader) {
    super(reader);
  }

  public Lexer(java.io.InputStream instream) {
    super(instream);
  }

} // End Lexer
