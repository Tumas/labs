package estore.core;

import java.util.ArrayList;

/**
 * @author hanniph
 *  Interface that Shop class has to implement. This is a contract between shop and its UI clients.
 */
public interface ShopModel
{
    //Customer information
    public ArrayList<Customer> getCustomers();
    public void setCustomerList(ArrayList<Customer> registeredCustomers);
    public int getNumberOfCustomers();
    public Customer getCustomer(int index);
    public boolean addCustomer(Customer customer);

    //Item information
    public ArrayList<Item> getAvailableItems();
    public Item getAvailableItem(int index);
    public int getNumberOfAvailableItems();

    //Stock information
    public Stock getStock();
    public void setStock(Stock stock);

    //Order information
    public ArrayList<Order> getCustomersCompletedOrders(Customer customer);
    public Order getCustomersCurrentOrder(Customer customer);
}