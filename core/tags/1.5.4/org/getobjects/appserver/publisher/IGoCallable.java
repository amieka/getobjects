/*
  Copyright (C) 2006-2008 Helge Hess

  This file is part of Go.

  Go is free software; you can redistribute it and/or modify it under
  the terms of the GNU Lesser General Public License as published by the
  Free Software Foundation; either version 2, or (at your option) any
  later version.

  Go is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
  License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with Go; see the file COPYING.  If not, write to the
  Free Software Foundation, 59 Temple Place - Suite 330, Boston, MA
  02111-1307, USA.
*/

package org.getobjects.appserver.publisher;

/**
 * IGoCallable
 * <p>
 * A IGoCallable is an object which can be called through the web or internally.
 */
public interface IGoCallable {
//TBD: document
//- special behaviour during lookup
//- time of invocation
//- use of isCallable

  public boolean isCallableInContext(IGoContext _ctx);
  public Object callInContext(Object _object, IGoContext _ctx);
  
}
