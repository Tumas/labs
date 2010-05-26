package estore.output.gui;

import estore.core.Item;
import estore.output.gui.exceptions.EStoreGUIException;
import estore.output.gui.exceptions.NothingSelectedException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CartListSelectionListener implements ListSelectionListener
{
    GUIMenu menu;

    public CartListSelectionListener(GUIMenu menu)
    {
        this.menu = menu;
    };

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String text;
            try {
                int index = menu.getCartList().getSelectedIndex();
                if (index == -1)
                    throw new NothingSelectedException("Nothing selected");

                Item item = menu.getActiveCustomer().getCurrentOrder().getItem(index);

                //item.generateDescription();
                text = "<html><ul><li>" + item.getTitle() + "</li>" +
                                      "<li><b>Price: </b>" + item.getPrice() + "</li>" +
                                      "<li>" + item.getDescription() + "</li>" +
                            "</ul></html>";
            } catch (EStoreGUIException ex){
                text = null;
            }
            menu.getCartItemInfoTextPane().setText(text);
        }
    }
}
