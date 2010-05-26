package estore.output.gui;

import estore.output.gui.exceptions.ActiveUserException;
import estore.exceptions.EStoreException;
import estore.exceptions.EmptyOrderException;
import estore.output.gui.exceptions.EStoreGUIException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class CheckoutButtonActionListener implements ActionListener
{
    GUIMenu menu;

    public CheckoutButtonActionListener(GUIMenu menu)
    {
        this.menu = menu;
    }

    public void actionPerformed(ActionEvent e)
    {
        try {
            if (menu.getActiveCustomer() == null)
                throw new ActiveUserException("Active user not set");
            if (menu.getActiveCustomer().getCurrentOrder().size() == 0)
                throw new EmptyOrderException("Zero items to purchase");

            menu.getActiveCustomer().completeCurrentOrder();
            menu.getCartList().setListData(GUIMenu.getCustomerCartItems(menu.getActiveCustomer()));

        } catch (EStoreException ex){
     //       System.out.println("Exception: " + ex.getMessage());
             JOptionPane.showMessageDialog(null, ex.getMessage(),
                    ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        } catch (EStoreGUIException exg){
       //     System.out.println("Runtime Exception: " + exg.getMessage());
             JOptionPane.showMessageDialog(null, exg.getMessage(),
                    exg.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
