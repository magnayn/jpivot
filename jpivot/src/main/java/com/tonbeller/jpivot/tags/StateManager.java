package com.tonbeller.jpivot.tags;

/**
 * Manages state of JSPs.
 * <p />
 * Example: the user displays customerdetail.jsp for customer A, then productdetail.jsp for product B,
 * and then again customerdetail.jsp for customer C. What should happen to A and B at this point?
 * There are two possible strategies: stack and page.
 * <p />
 * PageStateStrategy preserves the latest state for every page. In the example A will be destroyed and 
 * B and C will be preserved. So the user can display the productdetails.jsp with B and the 
 * customerdetail.jsp with C using browser history buttons.
 * <p />
 * StackStateStrategy puts the pages into a stack. In the example, first customerdetail.jsp/A is pushed,
 * then productdetail.jsp/B is pushed. In the last step, productdetail.jsp is popped because there is
 * already a customerdetail.jsp on the stack. After productdetail.jsp is popped, the associated
 * value of customerdetail.jsp is changed from A to C. The stack will only contain the value C for
 * customerdetail.jsp, the values A and B will be destroyed.
 *  
 * @author av
 * @since 15.02.2005
 */
public interface StateManager {

  /**
   * lifecycle: initialize() -> show() -> hide() -> ... -> show() -> hide() -> destroy()
   * @author av
   * @since 15.02.2005
   */
  public interface State {
    /**
     * the name of the state, for example the name of the JSP file. States that belong
     * to the same name will replace each other, i.e. there will be only one state
     * for each name.
     */
    String getName();
    /**
     * called once before the state is used
     */
    void initialize() throws Exception;
    /**
     * called once after the state is no longer used
     */
    void destroy() throws Exception;
    /**
     * called when this state is made the visible one. There is only one visible state
     */
    void show() throws Exception;
    /**
     * called when the 
     */
    void hide() throws Exception;
    
  }
  
  /**
   * makes s the current state. If there is already another state with the name of <code>s</code>,
   * it will be destroyed and replaced by <code>s</code>. 
   */
  public void initializeAndShow(State s) throws Exception;
  
  /**
   * makes a state with <code>name</code> the current state.
   */
  public void showByName(String name) throws Exception;
  
  /**
   * removes all states
   */
  public void destroyAll() throws Exception;

  /**
   * removes + destroys a named state
   */
  public void destroyByName(String name) throws Exception;
  
  /**
   * logger for debug/test
   */
  void setLogger(StateLogger logger);
  StateLogger getLogger();
}
