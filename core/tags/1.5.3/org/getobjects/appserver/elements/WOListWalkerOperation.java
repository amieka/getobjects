/*
  Copyright (C) 2006 Helge Hess

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

package org.getobjects.appserver.elements;

import org.getobjects.appserver.core.WOContext;

/*
 * WOListWalkerOperation
 * 
 * This is the operation triggered by the list walker for a given item. When
 * the method is called all the relevant bindings like index are set. This
 * _includes_ 'item'.
 * 
 * Usually an implementor will just trigger an operation on its template.
 */
public interface WOListWalkerOperation {

  public void processItem(int _idx, Object _item, WOContext _ctx);
  
}
