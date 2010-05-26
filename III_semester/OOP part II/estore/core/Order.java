package estore.core;

import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * @version     0.2 2009-10-21
 * @author      Tumas Bajoras PS3
 */

public class Order
{
    /** items items inside an order */
    private ArrayList<Item> items = new ArrayList<Item>();

    /** id unique number assigned to every order  */
    private long id;

    /* internal variable, to keep track of orders created. max value : 9,223,372,036,854,775,807 */
    private static long lastId = 0;

    /** Class constructor */
    public Order()
    {
        id = ++lastId;
    }

    /**
     * Class constructor specyfing initial list of items
     */
    public Order(ArrayList<Item> items)
    {
        this();
        this.items = items;
    }

    /** Get unique number given to the current instance of Order */
    public long getId()
    {
        return id;
    }

    /**
     * @param  index integer specifying particular item inside order
     * @return item from an order specified by its index
     */
    public Item getItem(int index)
    {
        return items.get(index);
    }

    /** @return number of items inside order */
    public int size()
    {
        return items.size();
    }

    /**
     *  @return true, if specified item was added succesfully and false otherwise
     */
    public boolean addItem(Item newItem)
    {
        return items.add(newItem);
    }

    /** Get total price of order */
    public BigDecimal getTotalSum()
    {
        BigDecimal sum = new BigDecimal(0);
        int size = items.size();

        for (int i = 0; i < size; i++){
            sum = sum.add(items.get(i).getPrice());
        }
        return sum;
    }

    public Item removeItem(int index)
    {
        return items.remove(index);
    }
}