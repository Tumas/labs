package estore.core;

import java.math.BigDecimal;

/**
 *
 * @author hanniph
 */
public class Book extends Item {

    protected String author = new String();

    public Book(String title, String author, String description, BigDecimal price, int quantity)
    {
        super(title, description, price, quantity);
        setAuthor(author);
    }

    //Getters and setters
    public void setAuthor(String author){
        this.author = author;
    }

    public String getAuthor(){
        return author;
    }

    //Special methods
    @Override public String toString()
    {
        return getAuthor() + " - " + getTitle();
    }

    //methods from the abstract class
    public String generateDescription(){
        String oldDescription = getDescription();
        setDescription(toString());

        return oldDescription;
    };
}
