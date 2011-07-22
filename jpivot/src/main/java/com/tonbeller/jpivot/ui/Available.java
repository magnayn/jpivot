package com.tonbeller.jpivot.ui;

/**
 * Indicates that the component may not be available in all situations.
 * An UI component may be not available, if the underlying OLAP server
 * does not support certain features (Extension's in JPivot).
 * <p />
 * For example, if the OLAP model does not support sorting, the UI
 * UI component for sorting would return false.
 * 
 * @see com.tonbeller.jpivot.core.Extension
 * @author av
 * @since 08.04.2005
 */
public interface Available {
  
  /**
   * return true, if this component currently can be used
   */
  boolean isAvailable();
}
