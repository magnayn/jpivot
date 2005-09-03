
Welcome to the JPivot Portlet!

1. Overview
2. Installation
3. Design and Implementation Notes
4. Known Issues
5. Acknowledgements


1. Overview
-----------

The JPivot portlet shows how to integrate JPivot into a portal server implementing the
JSR 168 specification. The current version only works with JBoss Portal, due to some 
specific JBoss Portal code, but the dependencies on JBoss Portal are minimal in the code,
so porting to other portal servers should be relatively straight forward.

As packaged, the portlet works with JBoss Portal 2.X and JBoss 4.0.2. Testing has been
done against JBoss Portal 2.0.1 RC1.

The test/demonstration database assumes Microsoft Access, but can be easily changed to
other major open source and commercial RDBMSs.


2. Installation
---------------

* Get the WCF, JPivot and jpivot_repository projects out of the JPivot CVS.

* Build WCF

* Install a binary version of JBoss 4.0.2 and JBoss Portal. The JBoss Portal distribution
  should be unzipped into ${jboss.home}/server/default/deploy. You will see a 
  jboss-portal.sar there.

* Edit the JBoss Portal file in jboss-portal.sar/portal-core.war/WEB-INF/default-portal.xml.

      <property>
         <name>org.jboss.portal.property.layout</name>
         <value>2ColumnLayout</value>
      </property>
      <property>
         <name>org.jboss.portal.property.renderSet</name>
         <value>divRenderer</value>
      </property>

  An example of this is in:
  
  src/jboss-portlet/jboss-portal/jboss-portal-war/WEB-INF/default-portal.xml


* Set the jboss.home Ant property in your ${user.home}/build.properties file

* Build and deploy the jpivot-portlet-1.4.0.war and jpivot-portlet-theme.war with 
  the portlet-dist target
  
* Build the demo Foodmart datatbase.

* If your database is not the default Access one, change the query tags in JSPS in
  jpivot-portlet-1.4.0.war/WEB-INF/queries appropriately
  
* Start JBoss

* Hit http://localhost:8080/portal/index.html

* Select JPivotPortlet from the menu. The index screen for the portlet appears


3. Design and Implementation Notes
----------------------------------

The WCF framework that the JPivot framework uses has  a RequestFilter as part of 
the core JPivot servlet configuration, that serves as a controller to route user 
interface operations to Listeners. The JSR 168 Portal server specification does 
not allow for something like servlet filters in a portlet. The JPivotPortlet 
reimplements the RequestFilter in the portal server environment to allow WCF and 
JPivot to function.

The charting, Excel and PDF functions in JPivot use servlets. The JPivotPortlet and
supporting classes reimplement those functions as portlet actions.

JPivot relies on specific stylesheets to display screens. The JPivotTheme provides
a template that includes the needed stylesheets. This new theme needs to be made the
default theme for the whole portal environment for JPivot to work.

Minimal changes were required to JPivot and WCF in order to make this portlet work.
Several tags had their definitions changed to allow JSP expression language parameters.


4. Known Issues
---------------

Clickable members are not working. You can see this with the options for Parameters with
Builtin Testdata and Parameters with Mondrian



5. Acknowledgements
-------------------

JPivot was created by Andreas Muller of Tonbeller, Germany
Mondrian was created by Julian Hyde of San Francisco, USA
JBoss Portal was created by Julien Viet, Roy Russo and team, JBoss Inc 