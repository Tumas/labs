package estore.output.gui;

import estore.core.Item;
import estore.output.gui.exceptions.EStoreGUIException;
import estore.output.gui.exceptions.NothingSelectedException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ItemListSelectionListener implements ListSelectionListener
{
   private GUIMenu menu;

   public ItemListSelectionListener(GUIMenu menu)
   {
       this.menu = menu;
   }

   public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String text;
                    
                    try {
                        if (menu.getItemList().getSelectedIndex() == -1)
                           throw new NothingSelectedException("Nothing selected");
                    Item item = menu.getShop().getStock().getItem(menu.getItemList().getSelectedIndex());
                    text = "<html><ul><li>" + item.getTitle() + "</li>" +
                                      "<li><b>Price: </b>" + item.getPrice() + "</li>" +
                                      "<li><b>Items in Stock: </b>" + item.getQuantity() + "</li>" +
                                      "<li>" + item.getDescription() + "</li>" +
                            "</ul></html>";
                    } catch (EStoreGUIException ex){
                        text = null;
                    }
                    menu.getItemInfoTextPane().setText(text);
                }
            }
}