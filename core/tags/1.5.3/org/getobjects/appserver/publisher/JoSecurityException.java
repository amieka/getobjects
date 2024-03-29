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
package org.getobjects.appserver.publisher;

/**
 * JoSecurityException
 */
public class JoSecurityException extends JoException {
  private static final long serialVersionUID = 1L;
  
  protected IJoAuthenticator authenticator;

  public JoSecurityException() {
    super();
  }
  public JoSecurityException(String _reason) {
    super(_reason);
  }
  public JoSecurityException(IJoAuthenticator _authenticator, String _reason) {
    this(_reason);
    this.authenticator = _authenticator;
  }
  
  /* accessors */
  
  public IJoAuthenticator authenticator() {
    return this.authenticator;
  }

  /* description */
  
  @Override
  public void appendAttributesToDescription(StringBuilder _d) {
    super.appendAttributesToDescription(_d);
    
    if (this.authenticator != null) {
      _d.append(" authenticator=");
      _d.append(this.authenticator);
    }
  }
}
