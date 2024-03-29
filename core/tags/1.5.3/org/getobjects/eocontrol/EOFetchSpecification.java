/*
  Copyright (C) 2006-2007 Helge Hess

  This file is part of JOPE.

  JOPE is free software; you can redistribute it and/or modify it under
  the terms of the GNU Lesser General Public License as published by the
  Free Software Foundation; either version 2, or (at your option) any
  later version.

  JOPE is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
  License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with JOPE; see the file COPYING.  If not, write to the
  Free Software Foundation, 59 Temple Place - Suite 330, Boston, MA
  02111-1307, USA.
*/

package org.getobjects.eocontrol;

import java.util.HashMap;
import java.util.Map;

import org.getobjects.foundation.NSKeyValueStringFormatter;
import org.getobjects.foundation.NSObject;
import org.getobjects.foundation.UMap;

/**
 * EOFetchSpecification
 * <p>
 * Represents the parameters for a fetch, usually a database fetch. Parameters
 * include the qualifier which restricts the set of objects, the required
 * ordering or attributes.
 * <p>
 * Raw SQL hint: EOCustomQueryExpressionHintKey
 */
public class EOFetchSpecification extends NSObject implements Cloneable {

  protected String             entityName;
  protected String[]           attributeNames;
  protected EOQualifier        qualifier;
  protected EOSortOrdering[]   sortOrderings;
  protected int                fetchLimit;
  protected int                fetchOffset;
  protected Map                userInfo;
  protected Map<String,Object> hints;
  protected boolean            usesDistinct;
  protected boolean            locksObjects;
  protected boolean            deep;
  protected boolean            fetchesRawRows;
  protected boolean            fetchesReadOnly;
  protected boolean            requiresAllQualifierBindingVariables;
  protected String[]           prefetchingRelationshipKeyPaths;
  
  /* construction */
  
  public EOFetchSpecification() {
    this(null, null, null, false, false, null);
  }

  public EOFetchSpecification
    (String _entityName, EOQualifier _qualifier, EOSortOrdering[] _orderings)
  {
    this(_entityName, _qualifier, _orderings,
         false, false, null);
  }

  public EOFetchSpecification
    (String _entityName, EOQualifier _qualifier, EOSortOrdering[] _orderings,
     boolean _usesDistinct, boolean _isDeep, Map<String, Object> _hints)
  {
    this.entityName    = _entityName;
    this.qualifier     = _qualifier;
    this.sortOrderings = _orderings;
    this.usesDistinct  = _usesDistinct;
    this.deep          = _isDeep;
    
    /* properly copy mutable deep structures */
    // TODO: should we copy the sort orderings array?
    // Note: qualifiers are immutable
    this.hints = _hints != null ? new HashMap<String, Object>(_hints) : null;
  }
  
  public EOFetchSpecification(EOFetchSpecification _src) {
    // Note: we can't use a null _src here
    this(_src.entityName(), _src.qualifier(), _src.sortOrderings(),
         _src.usesDistinct(), _src.isDeep(), _src.hints());
    
    if (_src != null) {
      this.locksObjects    = _src.locksObjects();
      this.fetchLimit      = _src.fetchLimit();
      this.fetchOffset     = _src.fetchOffset();
      this.fetchesRawRows  = _src.fetchesRawRows();
      this.fetchesReadOnly = _src.fetchesReadOnly();
      this.requiresAllQualifierBindingVariables = 
        _src.requiresAllQualifierBindingVariables();
      
      // TODO: should we copy that array?
      this.attributeNames = _src.fetchAttributeNames();
      this.prefetchingRelationshipKeyPaths = 
        _src.prefetchingRelationshipKeyPaths();
    }
  }
  
  /* accessors */

  public String entityName() {
    return this.entityName;
  }
  
  public void setQualifier(EOQualifier _q) {
    this.qualifier = _q;
  }
  public EOQualifier qualifier() {
    return this.qualifier;
  }
  
  public void setSortOrderings(EOSortOrdering[] _sos) {
    this.sortOrderings = _sos;
  }
  public EOSortOrdering[] sortOrderings() {
    return this.sortOrderings;
  }
  
  public void setFetchLimit(int _limit) {
    this.fetchLimit = _limit;
  }
  public int fetchLimit() {
    return this.fetchLimit;
  }
  
  public void setFetchOffset(int _skipCount) {
    this.fetchOffset = _skipCount;
  }
  public int fetchOffset() {
    return this.fetchOffset;
  }
  
  public void setUserInfo(Map _ui) {
    this.userInfo = _ui;
  }
  public Map userInfo() {
    return this.userInfo;
  }
  
  public void setHints(Map<String,Object> _ui) {
    this.hints = _ui;
  }
  public Map<String,Object> hints() {
    /* Note: we do not return null when possible to avoid checks */
    return this.hints != null ? this.hints : new HashMap<String,Object>();
  }
  public void setHint(String _key, Object _value) {
    if (this.hints == null)
      this.hints = new HashMap<String, Object>(1);
    this.hints.put(_key, _value);
  }
  public void removeHint(String _key) {
    if (this.hints != null && _key != null)
      this.hints.remove(_key);
  }
  
  public void setUsesDistinct(boolean _flag) {
    this.usesDistinct = _flag;
  }
  public boolean usesDistinct() {
    return this.usesDistinct;
  }
  
  public void setLocksObjects(boolean _flag) {
    this.locksObjects = _flag;
  }
  public boolean locksObjects() {
    return this.locksObjects;
  }
  
  public void setIsDeep(boolean _flag) {
    this.deep = _flag;
  }
  public boolean isDeep() {
    return this.deep;
  }
  
  public void setFetchAttributeNames(String[] _attrNames) {
    this.attributeNames = _attrNames;
  }
  public String[] fetchAttributeNames() {
    return this.attributeNames;
  }
  
  public void setFetchesRawRows(boolean _flag) {
    this.fetchesRawRows = _flag;
  }
  public boolean fetchesRawRows() {
    return this.fetchesRawRows;
  }
  
  public void setFetchesReadOnly(boolean _flag) {
    this.fetchesReadOnly = _flag;
  }
  public boolean fetchesReadOnly() {
    return this.fetchesReadOnly;
  }
  
  public void setRequiresAllQualifierBindingVariables(boolean _flag) {
    this.requiresAllQualifierBindingVariables = _flag;
  }
  public boolean requiresAllQualifierBindingVariables() {
    // TODO: I think prepared statements for fspecs which return true can be
    //       cached in the adaptor.
    return this.requiresAllQualifierBindingVariables;
  }
  
  public void setPrefetchingRelationshipKeyPaths(String[] _keyPaths) {
    this.prefetchingRelationshipKeyPaths = 
      _keyPaths != null && _keyPaths.length > 0 ? _keyPaths : null;
  }
  public String[] prefetchingRelationshipKeyPaths() {
    return this.prefetchingRelationshipKeyPaths;
  }
  
  /* hint patterns */
  
  public Map<String, Object> resolveHintBindPatterns(Object _b) {
    if (this.hints == null)
      return null;
    
    Map<String, Object> boundHints = null;
    for (String key: this.hints.keySet()) {
      if (!key.endsWith("BindPattern"))
        continue;
      
      if (boundHints == null)
        boundHints = new HashMap<String, Object>(this.hints);
      
      boundHints.remove(key); /* remove pattern key */
      String value = this.hints.get(key).toString();
      key = key.substring(0, key.length() - 11);
      
      value = NSKeyValueStringFormatter.format(value, _b);
      boundHints.put(key, value);
    }
    return boundHints != null ? boundHints : this.hints;
  }
  
  /* operations */
  
  public EOFetchSpecification fetchSpecificationWithQualifierBindings
    (Object _b)
  {
    Map<String, Object> boundHints = this.resolveHintBindPatterns(_b);
    
    if (this.qualifier == null && boundHints == this.hints)
      return this;
    
    EOQualifier boundQualifier = null;
    if (this.qualifier != null) {
      boundQualifier = this.qualifier.qualifierWithBindings
        (_b, this.requiresAllQualifierBindingVariables());
      if (boundQualifier == null) /* not all bindings could be resolved */
        return null;
      
      if (boundQualifier == this.qualifier && boundHints == this.hints)
        /* nothing was bound */
        return this;
    }
    
    EOFetchSpecification fs = new EOFetchSpecification(this);
    fs.setQualifier(boundQualifier);
    fs.setHints(boundHints);
    return fs;
  }
  
  /**
   * This method returns a fetch specification with resolved EOQualifierVariable
   * bindings. If the fetchspec has no bindings or if no bindings where given,
   * the object is returned as-is.
   * <p>
   * Example:<br>
   * <code>fs.fetchSpecificationWithQualifierBindings("id", 1000);</code>
   * <p>
   * Careful: you cannot just disable 'requiresAll' to perform a partial
   * replacement operation! This will strip all qualifiers with unresolved
   * bindings from the fetch specification.
   * 
   * @param _keyValuePairs
   * @return an EOFetchSpecification with the bindings resolved
   */
  @SuppressWarnings("unchecked")
  public EOFetchSpecification fetchSpecificationWithQualifierBindings
    (Object... _keyValuePairs)
  {
    Map<String, Object> binds = UMap.createArgs(_keyValuePairs);
    if (binds == null || binds.size() == 0)
      return this;
    
    return this.fetchSpecificationWithQualifierBindings(binds);
  }
  
  
  /* qualifier convenience methods */

  /**
   * This method combines the qualifier in the fetch-spec with the given
   * qualifier using an EOAndQualifier. The resulting qualifier is then
   * set as the new qualifier of the fetch-spec.
   * If no qualifier is set, the given qualifier is used as-is.
   * 
   * @param _qualifier - qualifier to add to the EOFetchSpecification
   */
  public void conjoinQualifier(EOQualifier _qualifier) {
    if (this.qualifier == null)
      this.setQualifier(_qualifier);
    else if (_qualifier != null) {
      // TBD: if this.qualifier is an AND qualifier, extend it?
      this.setQualifier(new EOAndQualifier(this.qualifier, _qualifier));
    }
  }
  /**
   * This method combines the qualifier in the fetch-spec with the given
   * qualifier using an EOOrQualifier. The resulting qualifier is then
   * set as the new qualifier of the fetch-spec.
   * If no qualifier is set, the given qualifier is used as-is.
   * 
   * @param _qualifier - qualifier to add to the EOFetchSpecification
   */
  public void disjoinQualifier(EOQualifier _q) {
    if (this.qualifier == null)
      this.setQualifier(_q);
    else if (_q != null) {
      // TBD: if this.qualifier is an OR qualifier, extend it?
      this.setQualifier(new EOOrQualifier(this.qualifier, _q));
    }
  }
  
  public void setQualifier(String _fmt, Object... _args) {
    EOQualifierParser parser = new EOQualifierParser(_fmt.toCharArray(), _args);
    this.setQualifier(parser.parseQualifier());
  }

  /**
   * This method combines the qualifier in the fetch-spec with the given
   * qualifier using an EOAndQualifier. The resulting qualifier is then
   * set as the new qualifier of the fetch-spec.
   * If no qualifier is set, the given qualifier is used as-is.
   */
  public void conjoinQualifier(String _fmt, Object... _args) {
    EOQualifierParser parser = new EOQualifierParser(_fmt.toCharArray(), _args);
    this.conjoinQualifier(parser.parseQualifier());
  }
  /**
   * This method combines the qualifier in the fetch-spec with the given
   * qualifier using an EOOrQualifier. The resulting qualifier is then
   * set as the new qualifier of the fetch-spec.
   * If no qualifier is set, the given qualifier is used as-is.
   */
  public void disjoinQualifier(String _fmt, Object... _args) {
    EOQualifierParser parser = new EOQualifierParser(_fmt.toCharArray(), _args);
    this.disjoinQualifier(parser.parseQualifier());
  }
  
  
  /* Cloneable */
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return this.copy();
  }
  
  /**
   * Convenience method to copy fetch specifications. (avoids the clone()
   * result cast)
   * 
   * @return a new fetch-spec object
   */
  public EOFetchSpecification copy() {
    return new EOFetchSpecification(this);
  }
  
  /* description */
  
  @Override
  public void appendAttributesToDescription(StringBuilder _d) {
    super.appendAttributesToDescription(_d);
    
    if (this.entityName != null) _d.append(" entity="    + this.entityName);

    if (this.qualifier  != null)
      _d.append(" qualifier=" + this.qualifier.stringRepresentation());

    if (this.hints      != null) _d.append(" hints="     + this.hints);
    
    if (this.fetchesRawRows)  _d.append(" raw");
    if (this.fetchesReadOnly) _d.append(" readonly");
    
    if (this.prefetchingRelationshipKeyPaths != null &&
        this.prefetchingRelationshipKeyPaths.length > 0)
    {
      _d.append(" prefetch=");
      for (int i = 0; i < this.prefetchingRelationshipKeyPaths.length; i++) {
        if (i != 0) _d.append(",");
        _d.append(this.prefetchingRelationshipKeyPaths[i]);
      }
    }
  }
}
