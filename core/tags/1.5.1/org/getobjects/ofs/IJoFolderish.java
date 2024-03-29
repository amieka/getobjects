/*
  Copyright (C) 2007 Helge Hess

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
package org.getobjects.ofs;

import org.getobjects.appserver.publisher.IJoContext;
import org.getobjects.eocontrol.EODataSource;

/**
 * IJoFolderish
 * <p>
 * A common interface for 'folder like' objects. Note that in OFS something
 * represented as a filesystem folder is NOT necessarily a folder at runtime
 * (eg .wo wrappers).
 * <p>
 * Whether an object is considered folderish or whether its a wrapper is
 * determined using the isFolderish() method on OFSBaseObject.
 */
public interface IJoFolderish {

  public EODataSource folderDataSource(IJoContext _ctx);
  
}
