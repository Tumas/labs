package estore.input.plain;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

import estore.core.Book;
import estore.core.MusicAlbum;
import estore.core.Item;
import estore.core.Customer;

public class FileDataReader
{
    private static Scanner s;

    /**
     *  Fill given Stock with items from the file
     *      @param stock Stock to fill with items
     *      @param fileName  Filename or path to data file
     *      @param delimiter delimiter to use when scanning data file. (Data Fields in the data file should be seprated by specified delimiter)
     *
     *  Expected file format:
     *      Title description price quantity
     *          (one item per line!)
     *
     *  @return ArrayList of items read/indexed
     */

    public static ArrayList<Item> readStockItems(String fileName, String delimiter) throws FileNotFoundException
    {
        String title = "";
        String desc = "";
        String price = "";
        String type = "";
        String author = "";

        int quantity = 0;
        ArrayList<Item> itemList = new ArrayList<Item>();

        try {
            s = new Scanner(new BufferedReader(new FileReader(fileName)));
            s.useDelimiter(delimiter);
            /*  File reading happens here */
            while (s.hasNext()){
               type = s.next();
               title = s.next();
               desc = s.next();
               price = s.next();
               quantity = s.nextInt();
               author = s.next();
               
               if (type.equalsIgnoreCase("Book")){
                    itemList.add(new Book(title, author, desc, new BigDecimal(price), quantity));
               } else if (type.equalsIgnoreCase("MusicAlbum")){ 
                   MusicAlbum m = new MusicAlbum(title, author, desc, new BigDecimal(price), quantity, null);
                   String track;
                   quantity = s.nextInt();
                   for (int i = 0; i < quantity; i++){
                        track = s.next();
                        m.addTrack(track);
                   }
                   itemList.add(m);
               }
               /* Skip LF  */
                s.nextLine();
            }
        } finally {
            if (s != null)
                s.close();
        }
        return itemList;
    }

    /**
     *  Fill given shop with customers from given data file
     *      @param shop      Shop object to fill
     *      @param fileName  Filename of data file
     *      @param delimiter to use when scanning data file. (Data Fields in the data file should be seprated by specified delimiter)
     *
     *  Expected file format:
     *      name address
     *          (one item per line!)
     *
     *   @return ArrayList of customers read/indexed
     */

    public static ArrayList<Customer> readShopCustomers(String fileName, String delimiter) throws FileNotFoundException
    {
        String name = "";
        String address = "";
        ArrayList<Customer> customerList = new ArrayList<Customer>();

        try {
            s = new Scanner(new BufferedReader(new FileReader(fileName)));
            s.useDelimiter(delimiter);
            /*  File reading happens here */
            while (s.hasNext()){
               name = s.next();
               address = s.next();
               /* Skip LF  */
               s.nextLine();
               customerList.add(new Customer(name, address));
              // shop.addCustomer(new Customer(name, address));

            }
        } finally {
            if (s != null)
                s.close();
        }
        return customerList;
    }
}