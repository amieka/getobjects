/*
  Copyright (C) 2008 Helge Hess

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EOGroupedList
 * <p>
 * A List which has an additional Map which maintains a grouping of its values.
 * A grouping is a binding of some value to a subset of a list. Its distinct,
 * that is an item of the array can only be in the sublist of one grouping
 * value.
 * <p>
 * Example:<pre>
 *   [
 *     { name = "Duck";  firstname = "Donald";   age = 23; }
 *     { name = "Duck";  firstname = "Dagobert"; age = 73; }
 *     { name = "Mouse"; firstname = "Mickey";   age = 42; }
 *     { name = "Duck";  firstname = "Trick";    age =  7; }
 *     { name = "Mouse"; firstname = "Minney";   age = 32; }
 *   ]</pre>
 * A grouping by 'name' (eg <code>EOKey("name")</code>) would result in a Map
 * like:<pre>
 *   {
 *     "Duck" = [
 *       { name = "Duck";  firstname = "Donald";   age = 23; }
 *       { name = "Duck";  firstname = "Dagobert"; age = 73; }
 *       { name = "Duck";  firstname = "Trick";    age =  7; }
 *     ];
 *     "Mouse" = [
 *       { name = "Mouse"; firstname = "Mickey";   age = 42; }
 *       { name = "Mouse"; firstname = "Minney";   age = 32; }
 *     ];
 *   }</pre>
 * 
 * <p>
 * Note: for simple KVC keypath groupings there are also utility methods in
 *       foundation.UList.
 */
public class EOGroupedList<E> extends ArrayList<E> {
  private static final long serialVersionUID = 1L;
  
  protected Map<Object, List<E>> groupedItems;
  protected EOValueEvaluation    expression;
  protected EOValueEvaluation[]  subexpressions;
  protected boolean needsReGroup;
  
  public EOGroupedList(EOValueEvaluation[] _exprs) {
    super();
    this.groupedItems = new HashMap<Object, List<E>>(16);
    if (_exprs != null && _exprs.length > 0) {
      this.expression = _exprs[0];
      if (_exprs.length > 1) {
        this.subexpressions = new EOValueEvaluation[_exprs.length - 1];
        System.arraycopy(_exprs, 1, this.subexpressions, 0, _exprs.length - 1);
      }
    }
  }
  public EOGroupedList(EOValueEvaluation _expr) {
    super();
    this.groupedItems = new HashMap<Object, List<E>>(16);
    this.expression   = _expr;
  }
  public EOGroupedList(EOValueEvaluation _expr, int _capacity) {
    super(_capacity);
    this.groupedItems = new HashMap<Object, List<E>>(16);
    this.expression   = _expr;
  }
  public EOGroupedList(EOValueEvaluation _expr, Collection<? extends E> _c) {
    super(_c);
    this.groupedItems = new HashMap<Object, List<E>>(16);
    this.expression   = _expr;
    this.needsReGroup = true;
  }
  
  /* grouping */
  
  /**
   * Returns the List of objects grouped for the given value. Eg the _value
   * could be 'Duck' if the List was grouped by 'lastname'.
   * 
   * @param the grouping value
   * @return List of objects grouped for the given value, or null
   */
  public List<E> getGroup(Object _value) {
    if (this.needsReGroup)
      this.regroup();
    
    return this.groupedItems.get(_value);
  }

  /**
   * Returns the values of the groups. Eg if we grouped by lastname, this will
   * return the distinct Set of lastnames (eg [ Duck, Mouse ]).
   * 
   * @return a Set of values which got grouped
   */
  public Set<Object> getGroups() {
    if (this.needsReGroup)
      this.regroup();
    
    return this.groupedItems.keySet();
  }
  
  protected void regroup() {
    this.needsReGroup = false;
    
    this.groupedItems.clear();
    if (this.expression == null)
      this.groupedItems.put(null, new ArrayList<E>(this));
    else {
      for (E item: this) {
        Object  value   = this.expression.valueForObject(item);
        List<E> sublist = this.groupedItems.get(value);
        
        if (sublist == null) {
          sublist = this.subexpressions != null
            ? new EOGroupedList<E>(this.subexpressions) : new ArrayList<E>(16);
          this.groupedItems.put(value, sublist);
        }
        
        sublist.add(item);
      }
    }
  }
  
  
  /* indices */
  
  public E set(int index, E element) {
    this.needsReGroup = true;
    return super.set(index, element);
  }
  
  
  /* clear */
  
  public void clear() {
    this.groupedItems.clear();
    this.needsReGroup = false;
    super.clear();
  }
  
  
  /* add items */

  public boolean add(final E _o) {
    if (!super.add(_o))
      return false;
    
    /* inline regroup */
    if (!this.needsReGroup) {
      Object value = (this.expression != null)
        ? this.expression.valueForObject(_o) : null;
      
      List<E> sublist = this.groupedItems.get(value);
      if (sublist == null) {
        sublist = this.subexpressions != null
          ? new EOGroupedList<E>(this.subexpressions) : new ArrayList<E>(16);
        this.groupedItems.put(value, sublist);
      }
      
      sublist.add(_o);
    }
    
    return true;
  }
  
  public void add(final int _index, final E _element) {
    this.needsReGroup = true;
    super.add(_index, _element);
  }
  
  @SuppressWarnings("unchecked") // TBD
  public boolean addAll(final Collection _c) {
    this.needsReGroup = true;
    return super.addAll(_c);
  }
  @SuppressWarnings("unchecked") // TBD
  public boolean addAll(final int _idx, final Collection _c) {
    this.needsReGroup = true;
    return super.addAll(_idx, _c);
  }
  
  public boolean retainAll(final Collection _c) {
    this.needsReGroup = true;
    return super.retainAll(_c);
  }

  
  /* remove items */

  public boolean remove(final Object _o) {
    this.needsReGroup = true;
    this.groupedItems.clear();
    return super.remove(_o);
  }
  public E remove(final int _idx) {
    this.needsReGroup = true;
    this.groupedItems.clear();
    return super.remove(_idx);
  }
  public boolean removeAll(final Collection _c) {
    this.needsReGroup = true;
    this.groupedItems.clear();
    return super.removeAll(_c);
  }

}
