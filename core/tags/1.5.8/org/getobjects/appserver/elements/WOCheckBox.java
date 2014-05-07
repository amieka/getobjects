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

import java.util.Collection;
import java.util.Map;

import org.getobjects.appserver.core.WOAssociation;
import org.getobjects.appserver.core.WOContext;
import org.getobjects.appserver.core.WOElement;
import org.getobjects.appserver.core.WORequest;
import org.getobjects.appserver.core.WOResponse;

/**
 * WOCheckBox
 * <p>
 * Create HTML form checkbox field.
 * <p>
 * Checkboxes are different to other form elements in the request handling.
 * Regular form elements always send a value when being submitted. Checkboxes
 * only do so if they are checked.<br>
 * So we cannot distinguish between a checkbox not being submitted and a
 * checkbox being disabled :-/
 * <p>
 * To fix the issue we also render a hidden form field which is always
 * submitted. If this is not required, it can be turned off with the 'safeGuard'
 * binding.
 * <p>
 * Sample:<pre>
 * IsAdmin: WOCheckBox {
 *   name    = "isadmin";
 *   checked = account.isAdmin;
 * }</pre>
 * 
 * Renders:<pre>
 *   &lt;input type="hidden"   name="isadmin_sg" value="1" /&gt;
 *   &lt;input type="checkbox" name="isadmin"    value="1" /&gt;</pre>
 *   
 * <p>
 * Bindings:<pre>
 *   selection [io] - object / Collection
 *   checked   [io] - boolean
 *   safeGuard [in] - boolean (def: true, submit unchecked state)</pre> 
 *   
 * Bindings (WOInput):<pre>
 *   id         [in]  - string
 *   name       [in]  - string
 *   value      [io]  - object
 *   readValue  [in]  - object (different value for generation)
 *   writeValue [out] - object (different value for takeValues)
 *   disabled   [in]  - boolean</pre>
 */
public class WOCheckBox extends WOInput {
  // TBD: should we support a label? (<label for=id>label</label>)
  
  protected WOAssociation selection;
  protected WOAssociation checked;
  protected WOAssociation safeGuard;

  public WOCheckBox
    (String _name, Map<String, WOAssociation> _assocs, WOElement _template)
  {
    super(_name, _assocs, _template);

    this.selection = grabAssociation(_assocs, "selection");
    this.checked   = grabAssociation(_assocs, "checked");
    this.safeGuard = grabAssociation(_assocs, "safeGuard");
  }
  
  /* responder */

  @SuppressWarnings("unchecked")
  @Override
  public void takeValuesFromRequest(final WORequest _rq, final WOContext _ctx) {
    /*
     * Checkboxes are special in their form-value handling. If the form is
     * submitted and the checkbox is checked, a 'YES' value is transferred in
     * the request.
     * BUT: If the checkbox is not-checked, no value is transferred at all!
     * 
     * TODO: this one is really tricky because we don't know whether the
     *       takeValues is actually triggered by a request which represents the
     *       form that contains the checkbox!
     *       Best workaround for now is to run takeValues only for POST.
     */
    final Object cursor = _ctx.cursor();
    
    if (this.disabled != null) {
      if (this.disabled.booleanValueInComponent(cursor))
        return;
    }
    
    final String formName  = this.elementNameInContext(_ctx);
    final Object formValue = _rq.formValueForKey(formName);
    boolean doIt = true;
    
    if (this.safeGuard==null || this.safeGuard.booleanValueInComponent(cursor)){
      /* we are configured to have a safeguard, check whether its submitted */
      doIt = _rq.formValueForKey(formName + "_sg") != null;
    }
    
    if (doIt && this.checked != null) {
      if (this.checked.isValueSettableInComponent(cursor))
        this.checked.setBooleanValue(formValue != null, cursor);
    }
    
    // TODO: document why we don't reset the value if missing
    if (doIt && formValue != null && this.writeValue != null) {
      if (this.writeValue.isValueSettableInComponent(cursor))
        this.writeValue.setValue(formValue, cursor);
    }

    if (this.selection != null && doIt) {
      Object sel = this.selection.valueInComponent(cursor);
      Object rv  = formValue;
      if (this.readValue != null)
        rv = this.readValue.valueInComponent(cursor);
      
      if (sel instanceof Collection) {
        if (formValue != null)
          ((Collection)sel).add(rv);
        else
          ((Collection)sel).remove(rv);
      }
      else if (doIt) /* push simple value */
        this.selection.setValue(formValue != null ? rv : null, cursor);
    }
  }
  
  
  /* generate response */
  
  @Override
  public void appendToResponse(final WOResponse _r, final WOContext _ctx) {
    if (_ctx.isRenderingDisabled())
      return;

    final Object cursor = _ctx.cursor(); 
    String lid = this.eid!=null ? this.eid.stringValueInComponent(cursor):null;
    
    final String cbname = this.elementNameInContext(_ctx);
    _r.appendBeginTag("input");
    _r.appendAttribute("type", "checkbox");
    if (lid != null) _r.appendAttribute("id", lid);
    _r.appendAttribute("name", cbname);
    
    Object v = null;
    if (this.readValue != null)
      v = this.readValue.valueInComponent(cursor);
    _r.appendAttribute("value", v != null ? v.toString() : "1");
    
    if (this.disabled != null) {
      if (this.disabled.booleanValueInComponent(cursor)) {
        _r.appendAttribute("disabled",
            _ctx.generateEmptyAttributes() ? null : "disabled");
      }
    }

    boolean isChecked = false;
    if (this.checked != null)
      isChecked = this.checked.booleanValueInComponent(cursor);
    else if (this.selection != null) {
      Object sel = this.selection.valueInComponent(cursor);
      if (v == sel || (v != null && v.equals(sel)))
        isChecked = true;
      else if (v != null && (sel instanceof Collection))
        isChecked = ((Collection)sel).contains(v);
    }
    if (isChecked) {
      _r.appendAttribute("checked",
          _ctx.generateEmptyAttributes() ? null : "checked");
    }

    
    if (this.coreAttributes != null)
      this.coreAttributes.appendToResponse(_r, _ctx);
    this.appendExtraAttributesToResponse(_r, _ctx);
    // TODO: otherTagString
    
    _r.appendBeginTagClose(_ctx.closeAllElements());
    
    /* add safeguard field */
    
    if (this.safeGuard==null || this.safeGuard.booleanValueInComponent(cursor)){
      _r.appendBeginTag("input");
      _r.appendAttribute("type", "hidden");
      _r.appendAttribute("name",  cbname + "_sg");
      _r.appendAttribute("value", cbname); /* doesn't really matter */
      _r.appendBeginTagClose(_ctx.closeAllElements());
    }
  }

  
  /* description */
  
  @Override
  public void appendAttributesToDescription(final StringBuilder _d) {
    super.appendAttributesToDescription(_d);
    
    this.appendAssocToDescription(_d, "selection", this.selection);
    this.appendAssocToDescription(_d, "checked",   this.checked);
  }  
}
