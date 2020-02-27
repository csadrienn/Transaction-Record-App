
package com.csontaka.transaction_record_app.gui;

import java.util.EventListener;

/** Listener interface for receiving action events in form panels.
 *
 * @author Adrienn Csonták
 */
public interface FormListener extends EventListener{

    /** Invoked when an action occurs.
     *
     * @param e A FormEvent object the transfer data.
     */
    public void formEventOccured(FormEvent e);
}
