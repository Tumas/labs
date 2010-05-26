package estore.core;

import java.math.BigDecimal;

/**
 * @version     0.2 2009-10-18
 * @author      Tumas Bajoras PS3
 */

public abstract class Item
{
    /**
     *  Price is an instance of BigDecimal class for greater accuracy.
     */

    /** title title of an item */
    protected String title = new String();

    /** price price of an item */
    protected BigDecimal price = new BigDecimal(0);

    /** description description of an item */
    protected String description = new String();

    /** quantity number showing how many units of item are available and have not been ordered yet */
    protected int quantity = 0;

    /* Required by inheritance */
    public Item(){}

    /**
     * Class constructor specifying title, description and price of an item
     */
    public Item(String title, String description, BigDecimal price, int quantity)
    {
        setPrice(price);
        setDescription(description);
        setTitle(title);
        setQuantity(quantity);
    }

    //Getters and Setters
    public String getTitle()
    {
        return title;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getDescription()
    {
        return description;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setQuantity(int quantity)
    {
        if (quantity >= 0 && quantity != this.getQuantity())
            this.quantity = quantity;
    }


    public void setPrice(BigDecimal price)
    {
        if (price.compareTo(new BigDecimal(0)) == 1){
            this.price = price;
        }
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    /* True if Item is available in stock  */
    public boolean isAvailable()
    {
        if (quantity > 0)
            return true;
        else
            return false;
    }

    @Override public String toString()
    {
        return getDescription();
    }

    //abstract methods
    /**
     * Generate description acording to Item's class and set it to description state
     * @return previous description state 
     */
    public abstract String generateDescription();

    /* Get some sample from product
        TOC from books,
        trailer from movies etc..
     */

    //public abstract String getSample();
}