package estore.output.gui;

import estore.core.Item;
import estore.output.gui.exceptions.ActiveUserException;
import estore.output.gui.exceptions.EStoreGUIException;
import estore.output.gui.exceptions.NothingSelectedException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ItemRemoveFromCartButtonActionListener implements ActionListener
{
    GUIMenu menu;

    public ItemRemoveFromCartButtonActionListener(GUIMenu menu)
    {
        this.menu = menu;
    }

    public void actionPerformed(ActionEvent e)
    {
        try {
            if (menu.getActiveCustomer() == null)
                throw new ActiveUserException("Active user is not set");

            int index = menu.getCartList().getSelectedIndex();
            if (index == -1)
                throw new NothingSelectedException("Nothing to remove from cart");

            Item item = menu.getActiveCustomer().getCurrentOrder().getItem(index);
            item.setQuantity(item.getQuantity() + 1);
            menu.getActiveCustomer().getCurrentOrder().removeItem(index);
            menu.getCartList().setListData(GUIMenu.getCustomerCartItems(menu.getActiveCustomer()));

        } catch (EStoreGUIException exg){
            //System.out.println("Runtime Exception: " + exg.getMessage());
             JOptionPane.showMessageDialog(null, exg.getMessage(),
                    exg.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }
}