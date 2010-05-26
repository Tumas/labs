package estore.core;

import java.util.ArrayList;

/**
 *  Collection of items available to customers
 *
 * @author      Tumas Bajoras PS3
 * @version     0.1 2009-10-20
 */

public class Stock
{
    /** address address of stock */
    private String address;

    /** itemList list of items hold in stock */
    private ArrayList<Item> itemList = new ArrayList<Item>();

    public Stock(String address)
    {
        setAddress(address);
    }

    public Stock(String address, ArrayList<Item> itemList)
    {
        this(address);
        setItemList(itemList);
    }

    public void setItemList(ArrayList<Item> itemList)
    {
        this.itemList = itemList;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    /** Get item by its index in the item list */
    public Item getItem(int index)
    {
        return itemList.get(index);
    }

    public ArrayList<Item> getItems()
    {
        return this.itemList;
    }

    /** Get number of items in the stock */
    public int size()
    {
        return itemList.size();
    }

    /** Add item to the item list
     *     @return true, if item was added succesfully and false otherwise
     */
    public boolean addItem(Item itemToAdd)
    {
        return itemList.add(itemToAdd);
    }
}