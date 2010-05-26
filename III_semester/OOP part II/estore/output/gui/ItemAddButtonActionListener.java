package estore.output.gui;

import estore.core.Item;
import estore.output.gui.exceptions.ActiveUserException;
import estore.exceptions.EStoreException;
import estore.exceptions.ItemNotAvailableException;
import estore.output.gui.exceptions.EStoreGUIException;
import estore.output.gui.exceptions.NothingSelectedException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author hanniph
 */
public class ItemAddButtonActionListener implements ActionListener
{
    GUIMenu menu;
    
    public ItemAddButtonActionListener(GUIMenu menu)
    {
        this.menu = menu;
    }

    public void actionPerformed(ActionEvent e)
    {
        try {
            if (menu.getActiveCustomer() == null)
                throw new ActiveUserException("No active User");

            int index = menu.getItemList().getSelectedIndex();
            if (index == -1)
                throw new NothingSelectedException("Nothing selected");
            
            Item item = menu.getShop().getAvailableItem(index);
            if (item.getQuantity() == 0)
                throw new ItemNotAvailableException("Selected item is currently unavailable");
            else{
                item.setQuantity(item.getQuantity() - 1);
                menu.getActiveCustomer().getCurrentOrder().addItem(item);
                menu.getCartList().setListData(GUIMenu.getCustomerCartItems(menu.getActiveCustomer()));
            }
        } catch (EStoreException ex){
            //System.out.println("Exception: " + ex.getMessage());
             JOptionPane.showMessageDialog(null, ex.getMessage(),
                    ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (EStoreGUIException exg){
            //System.out.println("Runtime Exception: " + exg.getMessage());
             JOptionPane.showMessageDialog(null, exg.getMessage(),
                    exg.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
