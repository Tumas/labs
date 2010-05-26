package estore.output.gui;

import estore.output.gui.exceptions.ActiveUserException;
import estore.output.gui.exceptions.EStoreGUIException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class UserActivateButtonActionListener implements ActionListener
{
    private GUIMenu menu;
   
    public UserActivateButtonActionListener(GUIMenu menu)
    {
        this.menu = menu;
    }

    public void actionPerformed(ActionEvent e)
    {
        try {
            int index = menu.getUserList().getSelectedIndex();
            if (index == -1)
                throw new ActiveUserException("Nothing to activate");
            
            menu.setActiveCustomer(menu.getShop().getCustomer(index));
            System.out.println(menu.getActiveCustomer().getName());
            menu.getCartList().setListData(GUIMenu.getCustomerCartItems(menu.getActiveCustomer()));
        } catch (EStoreGUIException exg){
            //System.out.println("Runtime Exception: " + exg.getMessage());
            JOptionPane.showMessageDialog(null, exg.getMessage(),
                    exg.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
