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
 * Base class for all JPivot exceptions that are RuntimeExceptions. 
 * 
 * @author Richard M. Emberson
 * @since Jun 11 2007
 * @version $Id: JPivotRuntimeException.java,v 1.1 2007/07/09 16:17:10 remberson Exp $
 */
public class JPivotRuntimeException extends RuntimeException {
    public JPivotRuntimeException() {
        super();
    }
    public JPivotRuntimeException(final String msg) {
        super(msg);
    }
    public JPivotRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public JPivotRuntimeException(final Throwable cause) {
        super(cause);
    }
}
