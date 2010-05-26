package estore.core;

import java.util.ArrayList;

/**
 * @author      Tumas Bajoras PS3
 * @version     0.3 2009-10-23
 */

public class Customer
{
    /** name name of a customer */
    private String name = new String();

    /** address address of a customer */
    private String address = new String();

    /** orders list of completed customer's orders  */
    private ArrayList<Order> orders = new ArrayList<Order>();

    /** currentOrder Order that isn't completed yet */
    private Order currentOrder = new Order();

    /** Class Constructor specyfing customer's name and address */
    public Customer(String name, String address)
    {
        setName(name);
        setAddress(address);
    }

    //Getters/Setters
    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return address;
    }

    public Order getCurrentOrder()
    {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder)
    {
        this.currentOrder = currentOrder;
    }

    /**
     * @param index integer specifying particular order inside customer's order list
     * @return Order specified by its index
     */
    public Order getOrder(int index)
    {
        return orders.get(index);
    }

    public ArrayList<Order> getOrders()
    {
        return orders;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    /**
     * Add a specified order to customer's list of orders
     * @param  order order to add to customer's list of orders
     * @return true if order was succesfully added and false otherwise
     */
    public boolean addOrder(Order order)
    {
        return orders.add(order);
    }

    public int size()
    {
        return orders.size();
    }

    /** Record currently active order to order history and create a new empty order    */
    public boolean completeCurrentOrder()
    {
        boolean result = addOrder(getCurrentOrder());
        setCurrentOrder(new Order());

        return result;
    }
}