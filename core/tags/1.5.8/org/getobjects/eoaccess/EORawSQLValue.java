/*
  Copyright (C) 2006-2007 Helge Hess

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

package org.getobjects.eoaccess;

import org.getobjects.eocontrol.EOSQLQualifier;
import org.getobjects.foundation.NSObject;

/**
 * EORawSQLValue
 * <p>
 * This is used to insert some raw SQL for example as values in an
 * EOAdaptorOperation.
 * Also used by EOSQLQualifier to represent the SQL sections of its value.
 *
 * @see EOSQLQualifier
 * @see EOSQLExpression
 */
public class EORawSQLValue extends NSObject {

  protected String value;
  
  public EORawSQLValue(String _value) {
    this.value = _value;
  }
  public EORawSQLValue(StringBuilder _value) {
    this.value = _value != null ? _value.toString() : null;
  }
  
  /* toString */
  
  @Override
  public String toString() {
    return this.value;
  }

  /* description */
  
  @Override
  public void appendAttributesToDescription(StringBuilder _d) {
    super.appendAttributesToDescription(_d);
    _d.append(" sql='");
    _d.append(this.value);
    _d.append(" '");
  }
  
}
