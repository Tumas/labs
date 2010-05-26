package estore.core;

import java.util.ArrayList;

/**
 * @author      Tumas Bajoras PS3
 */

public class Shop implements ShopModel
{
    /** registeredCustomers list of customer that use this shop */
    private ArrayList<Customer> registeredCustomers = new ArrayList<Customer>();

    /** stock items of this shop */
    private Stock stock = null;


    public Shop(Stock stock)
    {
        setStock(stock);
    }

    public Shop(Stock stock, ArrayList<Customer> registeredCustomers)
    {
        this(stock);
        setCustomerList(registeredCustomers);
    }

    //Customer information
    public void setCustomerList(ArrayList<Customer> registeredCustomers)
    {
        this.registeredCustomers = registeredCustomers;
    }

    public ArrayList<Customer> getCustomerList()
    {
        return registeredCustomers;
    }

    public Customer getCustomer(int index)
    {
        return getCustomerList().get(index);
    }

    public ArrayList<Customer> getCustomers()
    {
        return getCustomerList();
    }

    /** Get the number of registered customers */
    public int getNumberOfCustomers()
    {
        return registeredCustomers.size();
    }

    public boolean addCustomer(Customer customer)
    {
        return getCustomerList().add(customer);
    }

    //Stock information
    public Stock getStock()
    {
        return stock;
    }

    public void setStock(Stock stock)
    {
        this.stock = stock;
    }

    //Item information
    public ArrayList<Item> getAvailableItems()
    {
        return getStock().getItems();
    }

    public Item getAvailableItem(int index)
    {
        return getStock().getItem(index);
    }
    
    public int getNumberOfAvailableItems()
    {
        return getStock().getItems().size();
    }


    //Order information
    public ArrayList<Order> getCustomersCompletedOrders(Customer customer)
    {
        return customer.getOrders();
    }
    
    public Order getCustomersCurrentOrder(Customer customer)
    {
        return customer.getCurrentOrder();
    }
}