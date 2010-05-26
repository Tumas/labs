package estore.output.gui;

import estore.core.Customer;
import estore.core.Item;
import estore.core.Shop;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
/**
 *
 * @author hanniph
 */
public class GUIMenu extends JFrame
{
    //------------
    private Shop shop;
    private Customer activeCustomer;

    //--- visual elements : encapsulated to make the development of various Listeners easier
    private JList userList, cartList, itemList;
    private JTextPane itemInfoTextPane, cartItemInfoTextPane;

    // size of main window
    private final int height = 712;
    private final int width = 512;

    public GUIMenu(String windowTitle, Shop shop)
    {
        setSize(width, height);
        setShop(shop);
        setTitle(windowTitle);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JList getUserList()
    {
        return userList;
    }

    public JList getCartList()
    {
        return cartList;
    }

    public JList getItemList()
    {
        return itemList;
    }

    public void setItemList(JList itemList)
    {
        this.itemList = itemList;
    }

    public void setCartList(JList cartList)
    {
        this.cartList = cartList;
    }

    public void setUserList(JList userList)
    {
        this.userList = userList;
    }

    public void setItemInfoTextPane(JTextPane itemInfoTextPane)
    {
        this.itemInfoTextPane = itemInfoTextPane;
    }

    public void setCartItemInfoTextPane(JTextPane cartItemInfoTextPane)
    {
        this.cartItemInfoTextPane = cartItemInfoTextPane;
    }

    public JTextPane getItemInfoTextPane()
    {
        return itemInfoTextPane;
    }

    public JTextPane getCartItemInfoTextPane()
    {
        return cartItemInfoTextPane;
    }

    public void setShop(Shop shop)
    {
        this.shop = shop;
    }

    public Shop getShop()
    {
        return shop;
    }

    public void setActiveCustomer(Customer activeCustomer)
    {
        this.activeCustomer = activeCustomer;
    }

    public Customer getActiveCustomer()
    {
        return this.activeCustomer;
    }

    public void centerOnScreen()
    {
        Toolkit toolKit = getToolkit();
        Dimension size = toolKit.getScreenSize();
        setLocation(size.width/2 - getWidth()/2, size.height/2 - getHeight()/2);
    }

    public String[] getItemTitleArray()
    {
        int size = getShop().getStock().size();
        String[] array = new String[size];
        Item item;

        for (int i = 0; i < size; i++){
            item = getShop().getStock().getItem(i);
            array[i] = item.getTitle();
        }
        return array;
    }

    public String[] getCustomerNameArray()
    {
        int size = getShop().getNumberOfCustomers();
        String[] array = new String[size];
        Customer customer;

        for (int i = 0; i < size; i++){
            customer = getShop().getCustomer(i);
            array[i] = customer.getName() + " living in " + customer.getAddress();
        }
        return array;
    }

    public static String[] getCustomerCartItems(Customer customer)
    {
        int size = customer.getCurrentOrder().size();
        String[] array = new String[size];
        Item item;

        for (int i = 0; i < size; i++){
            item = customer.getCurrentOrder().getItem(i);
            array[i] = item.getTitle();
        }
        return array;
    }

    public void run()
    {
        centerOnScreen();

        //Pagrindinis konteineris
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panel);

        //Konteineris Pavadinimui
        JPanel customerPanel = new JPanel();
        customerPanel.setAlignmentX(0.1f);
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));

        JLabel customerLabel = new JLabel("Customers of the shop");
        customerPanel.add(customerLabel);

        //Konteineris Listui
        JPanel cPanel = new JPanel();
        cPanel.setAlignmentX(0.1f);
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.X_AXIS));

        setUserList(new JList(getCustomerNameArray()));
        JScrollPane customerPane = new JScrollPane();
        customerPane.getViewport().add(getUserList());
        cPanel.add(customerPane);

        //Konteineris mygtukui
        JPanel customerButtonPanel = new JPanel();
        customerButtonPanel.setAlignmentX(0.1f);
        customerButtonPanel.setLayout(new BoxLayout(customerButtonPanel, BoxLayout.Y_AXIS));

        JButton customerButton = new JButton("Activate");
        customerButtonPanel.add(customerButton);

        //Konteineris Pavadinimui
        JPanel itemPanel = new JPanel();
        itemPanel.setAlignmentX(0.1f);
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

        JLabel itemLabel = new JLabel("Items of the shop");
        itemPanel.add(itemLabel);

        //Konteineris listui
        JPanel iPanel = new JPanel();
        iPanel.setAlignmentX(0.1f);
        iPanel.setLayout(new BoxLayout(iPanel, BoxLayout.X_AXIS));

        setItemList(new JList(getItemTitleArray()));
        JScrollPane itemPane = new JScrollPane();
        itemPane.getViewport().add(getItemList());
        itemPane.setPreferredSize(new Dimension(200, 200));
        itemPane.setMinimumSize(new Dimension(200, 200));
        itemPane.setMaximumSize(new Dimension(200, 200));
        iPanel.add(itemPane);

        setItemInfoTextPane(new JTextPane());
        getItemInfoTextPane().setContentType("text/html");
        getItemInfoTextPane().setEditable(false);
        JScrollPane itemInfoTextScrollPane = new JScrollPane();
        itemInfoTextScrollPane.getViewport().add(getItemInfoTextPane());
        itemInfoTextScrollPane.setPreferredSize(new Dimension(200, 200));

        getItemList().addListSelectionListener(new ItemListSelectionListener(this));

        iPanel.add(Box.createRigidArea(new Dimension(25, 0)));
        iPanel.add(getItemInfoTextPane());

         //Konteineris mygtukui
        JPanel itemButtonPanel = new JPanel();
        itemButtonPanel.setAlignmentX(0.1f);
        itemButtonPanel.setLayout(new BoxLayout(itemButtonPanel, BoxLayout.Y_AXIS));

        JButton itemButton = new JButton("Add to cart");
        itemButtonPanel.add(itemButton);

         //Konteineris Pavadinimui
        JPanel cartPanel = new JPanel();
        cartPanel.setAlignmentX(0.1f);
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));

        JLabel cartLabel = new JLabel("Shopping cart information");
        cartPanel.add(cartLabel);

        //Konteineris listui
        JPanel scPanel = new JPanel();
        scPanel.setAlignmentX(0.1f);
        scPanel.setLayout(new BoxLayout(scPanel, BoxLayout.X_AXIS));

        setCartList(new JList());
        JScrollPane cartPane = new JScrollPane();
        cartPane.setMinimumSize(new Dimension(200, 200));
        cartPane.setMaximumSize(new Dimension(200, 200));
        cartPane.getViewport().add(getCartList());
        cartPane.setPreferredSize(new Dimension(200, 200));
        scPanel.add(cartPane);
       
        setCartItemInfoTextPane(new JTextPane());
        getCartItemInfoTextPane().setContentType("text/html");
        getCartItemInfoTextPane().setEditable(false);
        JScrollPane cartItemInfoPane = new JScrollPane();
        cartItemInfoPane.getViewport().add(getCartItemInfoTextPane());
        cartItemInfoPane.setPreferredSize(new Dimension(200, 200));

        scPanel.add(Box.createRigidArea(new Dimension(25, 0)));
        scPanel.add(getCartItemInfoTextPane());

        //Konteineris mygtukui
        JPanel cartButtonPanel = new JPanel();
        cartButtonPanel.setAlignmentX(0.1f);
        cartButtonPanel.setLayout(new BoxLayout(cartButtonPanel, BoxLayout.X_AXIS));

        JButton cartRemoveButton = new JButton("Remove");
        JButton checkoutButton = new JButton("Proceed to checkout");
        cartButtonPanel.add(cartRemoveButton);
        cartButtonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        cartButtonPanel.add(checkoutButton);

        //Registering listeners
        customerButton.addActionListener(new UserActivateButtonActionListener(this));
        itemButton.addActionListener(new ItemAddButtonActionListener(this));
        cartRemoveButton.addActionListener(new ItemRemoveFromCartButtonActionListener(this));
        checkoutButton.addActionListener(new CheckoutButtonActionListener(this));
        getCartList().addListSelectionListener(new CartListSelectionListener(this));


        //Adding things to main panel
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(customerPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(cPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(customerButtonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(itemPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(iPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(itemButtonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(cartPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(scPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(cartButtonPanel);
  
        setVisible(true);
    }
}