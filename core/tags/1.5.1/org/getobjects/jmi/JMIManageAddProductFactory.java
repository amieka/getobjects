/*
  Copyright (C) 2007 Helge Hess

  This file is part of JOPE JMI.

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
package org.getobjects.jmi;

import org.getobjects.appserver.publisher.IJoClassValueFactory;
import org.getobjects.appserver.publisher.IJoContext;
import org.getobjects.foundation.NSObject;

/**
 * JMIManageAddProductFactory
 * <p>
 * Create context-sensitive JMIManageAddProduct objects (those require a
 * context and possibly the object they are bound to.
 * <p>
 * This object is instantiated as part of the product.plist loading process,
 * it gets registered as a class slot. When the lookup of the class slot is
 * performed, the class will turn this object into a JMIManageAddProduct,
 * passing it the context and context object.
 */
public class JMIManageAddProductFactory extends NSObject
  implements IJoClassValueFactory
{
  
  public JMIManageAddProductFactory() {
  }

  public Object valueForObjectInContext
    (Object _object, String _name, IJoContext _ctx)
  {
    return new JMIManageAddProduct(_object, _ctx);
  }

}
