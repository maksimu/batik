/*

   Copyright 2001-2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.gvt.event;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.util.EventObject;

/**
 * A concrete version of {@link org.apache.batik.gvt.event.AWTEventDispatcher}.
 *
 * This class is used for JDKs &gt;= 1.4, which have MouseWheelEvent
 * support.  For JDKs &lt; 1.4, the file
 * sources-1.3/org/apache/batik/gvt/event/AWTEventDispatcher defines a
 * version of this class that does not support MouseWheelEvents.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class AWTEventDispatcher extends AbstractAWTEventDispatcher
                                implements MouseWheelListener {

    /**
     * Dispatches the specified AWT mouse wheel event down to the GVT tree.
     * The mouse wheel event is mutated to a GraphicsNodeMouseWheelEvent.
     * @param evt the mouse event to propagate
     */
    public void mouseWheelMoved(MouseWheelEvent evt) {
        dispatchEvent(evt);
    }

    /**
     * Dispatches the specified AWT event.
     * @param evt the event to dispatch
     */
    public void dispatchEvent(EventObject evt) {
        if (evt instanceof MouseWheelEvent) {
            if (root == null) // No root do not store anything.
                return;
            if (!eventDispatchEnabled) {
                if (eventQueueMaxSize > 0) {
                    eventQueue.add(evt);
                    while (eventQueue.size() > eventQueueMaxSize)
                        // Limit how many events we queue - don't want
                        // user waiting forever for them to clear.
                        eventQueue.remove(0); 
                }
                return;
            }
            dispatchMouseWheelEvent((MouseWheelEvent) evt);
        } else {
            super.dispatchEvent(evt);
        }
    }

    /**
     * Dispatches the specified AWT mouse wheel event.
     * @param evt the mouse wheel event to dispatch
     */
    protected void dispatchMouseWheelEvent(MouseWheelEvent evt) {
        if (lastHit != null) {
            processMouseWheelEvent
                (new GraphicsNodeMouseWheelEvent(lastHit,
                                                 evt.getID(),
                                                 evt.getWhen(),
                                                 evt.getModifiers(),
                                                 getCurrentLockState(),
                                                 evt.getWheelRotation()));
        }
    }

    /**
     * Processes the specified event by firing the 'global' listeners
     * attached to this event dispatcher.
     * @param evt the event to process
     */
    protected void processMouseWheelEvent(GraphicsNodeMouseWheelEvent evt) {
        if (glisteners != null) {
            GraphicsNodeMouseWheelListener[] listeners =
                (GraphicsNodeMouseWheelListener[])
                getListeners(GraphicsNodeMouseWheelListener.class);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].mouseWheelMoved(evt);
            }
        }
    }

    /**
     * Dispatches the specified AWT key event.
     * @param evt the key event to dispatch
     */
    protected void dispatchKeyEvent(KeyEvent evt) {
        currentKeyEventTarget = lastHit;
        if (currentKeyEventTarget != null) {
            processKeyEvent
                (new GraphicsNodeKeyEvent(currentKeyEventTarget,
                                          evt.getID(),
                                          evt.getWhen(),
                                          evt.getModifiers(),
                                          getCurrentLockState(),
                                          evt.getKeyCode(),
                                          evt.getKeyChar(),
                                          evt.getKeyLocation()));
        }
    }
}
