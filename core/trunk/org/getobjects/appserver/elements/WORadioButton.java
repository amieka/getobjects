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

package org.getobjects.appserver.elements;

import java.util.Map;

import org.getobjects.appserver.core.WOAssociation;
import org.getobjects.appserver.core.WOContext;
import org.getobjects.appserver.core.WOElement;
import org.getobjects.appserver.core.WORequest;
import org.getobjects.appserver.core.WOResponse;

/**
 * WORadioButton
 * <p>
 * Create HTML form radio buttons.
 * <p>
 * Sample:
 * <pre>
 * Firstname: WORadioButton {
 *   name      = "products";
 *   value     = "iPhone";
 *   selection = selectedProduct;
 * }</pre>
 * 
 * Renders:
 *   <pre>&lt;input type="radio" name="products" value="iPhone" /&gt;</pre>
 * 
 * Bindings (WOInput):<pre>
 *   id         [in]  - string
 *   name       [in]  - string
 *   value      [io]  - object
 *   readValue  [in]  - object (different value for generation)
 *   writeValue [out] - object (different value for takeValues)
 *   disabled   [in]  - boolean</pre>
 * Bindings:<pre>
 *   selection [io] - object
 *   checked   [io] - boolean</pre>
 */
public class WORadioButton extends WOInput {

  protected WOAssociation selection;
  protected WOAssociation checked;

  public WORadioButton
    (String _name, Map<String, WOAssociation> _assocs, WOElement _template)
  {
    super(_name, _assocs, _template);

    this.selection = grabAssociation(_assocs, "selection");
    this.checked   = grabAssociation(_assocs, "checked");
    
    if (this.name != null && this.name == _assocs.get("id"))
      log.warn("WORadioButton with the same binding for id and name: " + this);
  }

  /* responder */

  @Override
  public void takeValuesFromRequest(final WORequest _rq, final WOContext _ctx) {
    final Object cursor = _ctx.cursor();
    
    if (this.disabled != null) {
      if (this.disabled.booleanValueInComponent(cursor))
        return;
    }
    
    /* retrieve form values */

    final String formName  = this.elementNameInContext(_ctx);
    final String formValue = _rq.stringFormValueForKey(formName);
    if (formValue == null) {
      /* radio buttons are special, they are selected based upon there value */ 
      log.debug("got not form value for radio button elem: " + formName);
      return;
    }
    
    /* retrieve model values */
    
    if (this.readValue == null) { /* nothing to push to */
      log.error("missing value binding for element: " + this);
      return;
    }
    final Object v  = this.readValue.valueInComponent(cursor);
    final String vs = v.toString();
    
    /* check whether we are the selected radio button */
    
    if (!formValue.equals(vs)) {
      /* ok, was a different element (with the same form name) */
      
      /*
       * Note: we set the checked binding to false which implies that the
       *       checked bindings of the radio buttons MUST bind to different
       *       variables! Otherwise they will overwrite each other!
       */
      if (this.checked != null) {
        if (this.checked.isValueSettableInComponent(cursor))
          this.checked.setBooleanValue(false, cursor);
      }
      return;
    }
    
    /* yup, we are the selected radio button, fill the bindings */
    
    if (this.checked != null) {
      if (this.checked.isValueSettableInComponent(cursor))
        this.checked.setBooleanValue(true, cursor);
    }
    
    if (this.selection != null) {
      if (this.selection.isValueSettableInComponent(cursor))
        this.selection.setValue(v, cursor);
    }
  }
  
  
  /* generate response */
  
  @Override
  public void appendToResponse(final WOResponse _r, final WOContext _ctx) {
    if (_ctx.isRenderingDisabled())
      return;

    // Note: we MUST have a value binding
    if (this.readValue == null) {
      _r.appendContentString("[ERROR: radio w/o value binding]");
      log.error("missing value binding for radio button: " + this);
      return;
    }
    
    final Object cursor   = _ctx.cursor();
    final String formName = this.elementNameInContext(_ctx);
      
    _r.appendBeginTag("input");
    _r.appendAttribute("type", "radio");
    
    String lid = this.eid!=null ? this.eid.stringValueInComponent(cursor):null;
    if (lid != null) _r.appendAttribute("id", lid);

    _r.appendAttribute("name", formName);
    
    Object v  = this.readValue.valueInComponent(cursor);
    String vs = v.toString();
    _r.appendAttribute("value", vs);
    
    if (this.disabled != null) {
      if (this.disabled.booleanValueInComponent(_ctx.cursor())) {
        _r.appendAttribute("disabled",
            _ctx.generateEmptyAttributes() ? null : "disabled");
      }
    }

    /* Note: the 'checked' binding has precedence, but its better to use either
     *       'checked' _or_ 'selection'.
     */
    if (this.checked != null) {
      if (this.checked.booleanValueInComponent(_ctx.cursor()))
        _r.appendAttribute("checked", "checked");
    }
    else if (this.selection != null) {
      /* compare selection with value */
      final Object s = this.selection.valueInComponent(cursor);
      if (v == s) {
        _r.appendAttribute("checked",
            _ctx.generateEmptyAttributes() ? null : "checked");
      }
      else if (v != null && v.equals(s)) { // s can be null!
        _r.appendAttribute("checked",
            _ctx.generateEmptyAttributes() ? null : "checked");
      }
      else if (false) {
        System.err.println("VALUES DO NOT MATCH: " + formName + "\n" +
            "  value: " + v + " | " + (v != null?v.getClass():"NULL") + "\n" +
            "  sel:   " + s + " | " + (s != null?s.getClass():"NULL") + "\n" +
            "  bind:  " + this.selection);
      }
    }
    else {
      /* if the button isn't handled by the page but by some DirectAction or
       * other script, its not an error not to have those bindings
       */
      log.info("no selection or checked binding set for radio button");
    }
    
    if (this.coreAttributes != null)
      this.coreAttributes.appendToResponse(_r, _ctx);
    this.appendExtraAttributesToResponse(_r, _ctx);
    // TODO: otherTagString
    
    _r.appendBeginTagClose(_ctx.closeAllElements());
  }

  
  /* description */
  
  @Override
  public void appendAttributesToDescription(final StringBuilder _d) {
    super.appendAttributesToDescription(_d);
    
    this.appendAssocToDescription(_d, "selection", this.selection);
    this.appendAssocToDescription(_d, "checked",   this.checked);
  }  
}
