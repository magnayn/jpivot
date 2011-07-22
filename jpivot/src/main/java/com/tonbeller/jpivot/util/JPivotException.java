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
package com.tonbeller.jpivot.util;

/** 
 * Base class for all JPivot exceptions that are not RuntimeExceptions. 
 * 
 * @author Richard M. Emberson
 * @since Jun 11 2007
 * @version $Id: JPivotException.java,v 1.1 2007/07/09 16:17:10 remberson Exp $
 */
public class JPivotException extends Exception {
    public JPivotException() {
        super();
    }
    public JPivotException(final String msg) {
        super(msg);
    }
    public JPivotException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public JPivotException(final Throwable cause) {
        super(cause);
    }
}
