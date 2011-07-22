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
package com.tonbeller.jpivot.olap.model;

import java.util.List;


/**
 * Browse an OLAP dataSource in order to retrieve
 * specific Olap Items as dimensions, levels, members
 */
public interface OlapDiscoverer {

  public static final int PROVIDER_MICROSOFT = 1;
  public static final int PROVIDER_SAP = 2;
  public static final int PROVIDER_MONDRIAN = 3;

  /**
   * retrieve catalogs in data source
   * @return List of OlapItems for the catalogs
   * @throws OlapException
   */
  public java.util.List discoverCat() throws OlapException;

  /**
   * retrieve cubes in data source for a given catalog
   * @param cat catalog
   * @return List of OlapItems for the cubes
   * @throws OlapException
   */
  public java.util.List discoverCube(String cat) throws OlapException;

  /**
   * retrieve dimensions in data source for given catalog, cube
   * @param cat catalog name
   * @param cube cube name 
   * @return List of OlapItems for the dimensions
   * @throws OlapException
   */
  public java.util.List discoverDim(String cat, String cube) throws OlapException;

  /**
   * retrieve hierarchies in data source for given catalog, cube, dimension 
   * @param cat name of catalog 
   * @param cube name of cube  
   * @param dimension unique name of dimension, can be null
   * @return List of OlapItems for the hierarchies
   * @throws OlapException
   */
  public java.util.List discoverHier(String cat, String cube, String dimension)
    throws OlapException;

  /**
   * retrieve levels in data source for given catalog, cube, dimension 
   * @param cat name of catalog 
   * @param cube name of cube  
   * @param dimension unique name of dimension, can be null 
   * @param hier unique name of hierarchy, can be null
   * @return List of OlapItems for the levels
   * @throws OlapException
   */
  public java.util.List discoverLev(String cat, String cube, String dimension, String hier)
    throws OlapException;

  /**
   * retrieve members in data source for given catalog, cube, dimension, level
   * @param cat name of catalog 
   * @param cube name of cube  
   * @param dimension unique name of dimension
   * @param hierarchy unique name of hierarchy   
   * @param level unique name of level
   * @return List of OlapItems for the members
   * @throws OlapException
   */
  public java.util.List discoverMem(
    String cat,
    String cube,
    String dimension,
    String hierarchy,
    String level)
    throws OlapException;

  /**
   * retrieve member tree in data source for given catalog, cube, member
   * @param cat name of catalog 
   * @param cube name of cube  
   * @param member unique name of member
   * @param treeop bit combination according to TREEOP specification
   *               MDTREEOP_CHILDREN = 1
   *               MDTREEOP_SIBLINGS = 2
   *               MDTREEOP_PARENT = 4
   *               MDTREEOP_SELF = 8
   *               MDTREEOP_DESCENDANTS = 16
   *               MDTREEOP_ANCESTORS = 32
   * @return List of OlapItems for the members
   * @throws OlapException
   */
  public java.util.List discoverMemTree(String cat, String cube, String member, int treeop)
    throws OlapException;

  /**
   * retrieve a map describing the datasource 
   * @return map of key, value strings
   *             keys are, for instance, DataSourceName and DataSourceDescription
   * @throws OlapException
   */
  public java.util.Map discoverDS() throws OlapException;

  /**
   * retrieve member properties in data source for given catalog, cube, dimension, hierarchy, level
   * @param cat name of catalog 
   * @param cube name of cube  
   * @param dimension unique name of dimension
   * @param hierarchy unique name of hierarchy   
   * @param level unique name of level
   * @return List of OlapItems for the members
   * @throws OlapException
   */
  public java.util.List discoverProp(
    String cat,
    String cube,
    String dimension,
    String hierarchy,
    String level)
    throws OlapException;


  /**
     * retrieve SAP variables for given catalog, cube
     * @param cat name of catalog 
     * @param cube name of cube  
     * @return List of OlapItems for the members
     * @throws OlapException
     * @see com.tonbeller.jpivot.olap.model.OlapDiscoverer#discoverProp 
     */
   public java.util.List discoverSapVar(String cat, String cube) throws OlapException ;

  /**
   * @return provider
   */
  public int getProvider();

  /**
   * retrieve datasource properties
   * @return List of OlapItems for the datasource properties
   */
  public List discoverDSProps() throws OlapException;
  
} // OlapDiscoverer
