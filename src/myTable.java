
import java.util.*;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.Scrollable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rossmann
 */
public class myTable extends JTable {

    public interface XXXListener extends EventListener {

        public void dispatchXXX(String e);
    }

    public String test123 = "";

    EventListenerList xxxListeners = new EventListenerList();

    public void addXXXListener(XXXListener listener) {
        xxxListeners.add(XXXListener.class, listener);
    }

    public void removeXXXListener(XXXListener listener) {
        xxxListeners.remove(XXXListener.class, listener);
    }

    public void fireXXX(String test) {
        Object[] listeners = xxxListeners.getListenerList();
        // loop through each listener and pass on the event if needed
        int numListeners = listeners.length;
        for (int i = 0; i < numListeners; i += 2) {
            if (listeners[i] == XXXListener.class) {
                // pass the event to the listeners event dispatch method
                ((XXXListener) listeners[i + 1]).dispatchXXX(test);
            }
        }
    }
}
