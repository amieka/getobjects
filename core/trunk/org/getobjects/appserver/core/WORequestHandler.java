/*
  Copyright (C) 2006-2014 Helge Hess

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

package org.getobjects.appserver.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getobjects.appserver.publisher.IGoCallable;
import org.getobjects.appserver.publisher.IGoContext;
import org.getobjects.appserver.publisher.IGoObject;
import org.getobjects.foundation.NSObject;

/**
 * Abstract superclass for request handlers. Request handlers are objects which
 * decide what to do with a given request. That is, how to decode the URL and
 * how to map the URL to the controller objects.
 * <p>
 * There are four basic request handlers:
 * <ul>
 *   <li>WODirectActionRequestHandler
 *   <li>WOComponentRequestHandler
 *   <li>WOResourceRequestHandler
 *   <li>GoObjectRequestHandler
 * </ul>
 * Technically the first three are superflous in Go because of the
 * GoObjectRequestHandler. They are left in place for compatibility
 * reasons.
 */
public abstract class WORequestHandler extends NSObject
  implements IGoObject, IGoCallable
{
  protected static final Log log = LogFactory.getLog("WOApplication");
  
  protected WOApplication application;
  
  public WORequestHandler(WOApplication _app) {
    this.application = _app;
  }
  
  /* session handling */
  
  public String sessionIDFromRequest(final WORequest _rq) {
    /* this is overridden by the WOComponentRequestHandler */
    return _rq != null ? _rq.sessionID() : null;
  }
  
  public boolean restoreSessionsUsingIDs() {
    return true;
  }
  
  public boolean autocreateSession(final WOContext _ctx) {
    // TODO: implement
    return false;
  }
  
  /* request handling */
  
  public abstract WOResponse handleRequest
    (final WORequest _rq, final WOContext _ctx, final WOSession _s);
  
  public boolean doesRejectFavicon() {
    return true;
  }

  public WOResponse handleRequest(final WORequest _rq) {
    WOContext  ctx;
    WOResponse r = null;
    WOSession  session = null;
    String     sessionId;
    boolean    debugOn = log.isDebugEnabled();

    if (debugOn) log.debug("handleRequest: " + _rq);
    
    if (this.doesRejectFavicon()) {
      if ("/favicon.ico".equals(_rq.uri()))
        return null;
    }
    
    ctx = this.application.createContextForRequest(_rq);
    if (debugOn) log.debug("  created context: " + ctx);
    
    /* prepare session */
    
    if ((sessionId = this.sessionIDFromRequest(_rq)) != null) {
      if (sessionId.length() == 0)
        sessionId = null;
      else if (sessionId.equals("nil"))
        sessionId = null;
    }
    if (debugOn) log.debug("  session-id: " + sessionId);
    
    try {
      this.application.awake();
      
      /* restore session */
      
      if (this.restoreSessionsUsingIDs()) {
        if (sessionId != null) {
          if (debugOn) log.debug("  restore session: " + sessionId);
        
          session = this.application.restoreSessionWithID(sessionId, ctx);
          if (session == null) {
            final WOActionResults ar =
              this.application.handleSessionRestorationError(ctx);
            r = ar != null ? ar.generateResponse() : null;
            sessionId = null;
          }
        }
      
        if (r == null /* no error */ && session == null) {
          /* no error, but session is not here */
          if (this.autocreateSession(ctx)) {
            if (debugOn) log.debug("  autocreate session: " + sessionId);
            
            if (!this.application.refusesNewSessions()) {
              session = this.application.initializeSession(ctx);
              if (session == null) {
                final WOActionResults ar =
                  this.application.handleSessionRestorationError(ctx);
                r = ar != null ? ar.generateResponse() : null;
              }
            }
            else {
              // TODO: this already failed once? will it return null again?
              final WOActionResults ar =
                this.application.handleSessionRestorationError(ctx);
              r = ar != null ? ar.generateResponse() : null;
            }
          }
        }
        
        if (debugOn) {
          if (session != null)
            log.debug("  restored session: " + session);
          else if (sessionId != null)
            log.debug("  did not restore session with id: " + sessionId);
        }
      }
    
      /* run handler specific processing */
      
      if (r == null)
        r = this.handleRequest(_rq, ctx, session);
      
      /* save session */
      
      // TODO: store session cookies
      
      if (ctx.savePageRequired) {
        final WOComponent page = ctx.page();
        if (page != null) {
          final WOSession sn = ctx.session();
          if (sn != null)
            sn.savePage(page);
          else {
            /* this should never happen because the URL already requests a SID*/
            log.error("got no session to save page ...");
          }
        }
        else
          log.warn("requested save page, but got no page in context:" + ctx);
      }
      
      if (ctx.hasSession()) {
        // TODO: ensure that session gets a sleep?
        this.application.saveSessionForContext(ctx);
      }
      else
        log.debug("no session to store ...");
    }
    catch (Exception e) {
      // TODO: call some handler method
      // TODO: ensure that session gets a sleep?
      if (debugOn) log.debug("  handler catched exception", e);
      final WOActionResults ar =
        this.application.handleException(e, ctx);
      r = ar != null ? ar.generateResponse() : null;
    }
    
    /* deal with missing responses */
    
    if (r == null) {
      log.warn("request handler produced no result.");
      r = ctx.response();
    }
    if (r == null)
      r = new WOResponse(_rq);
    
    /* tear down context */
    // TODO: send sleep() or something to tear it down?
    ctx = null;
    
    return r;
  }

  /* GoObject */

  public Object lookupName(String _name, IGoContext _ctx, boolean _aquire) {
    /* we support no subobjects, all remaining handling is done by us */
    return null;
  }
  
  /**
   * Act as a GoMethod. The _object will usually be the WOApplication. The
   * default implementation just calls handleRequest().
   * 
   * @param _object - the object which the handler is attached to (WOApp)
   * @param _ctx    - the Go context used for tracking the object lookup
   * @return a result, usually, but not necessarily a WOResponse
   */
  public Object callInContext(final Object _object, final IGoContext _ctx) {
    if (_ctx == null)
      return null;
    
    WOContext wctx = (WOContext)_ctx;
    return this.handleRequest
      (wctx.request(), wctx, wctx.hasSession() ? wctx.session() : null);
  }
  
  /**
   * Checks whether the request handler is "callable" in the given context. This
   * object is only callable in WOContext's. If the context is null or a
   * different class, this method returns false.
   * 
   * @param _ctx - the IGoContext the call shall happen in
   * @return true if the object is callable in the given context
   */
  public boolean isCallableInContext(final IGoContext _ctx) {
    /* we are callable in WOContext's */
    return _ctx != null && _ctx instanceof WOContext;
  }
  
}
