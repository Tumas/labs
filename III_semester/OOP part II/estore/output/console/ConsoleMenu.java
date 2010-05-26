package estore.output.console;

import estore.core.*;

import java.io.Console;
import java.io.IOException;

import java.util.InputMismatchException;
import java.util.Scanner;


/**
 *
 * @author hanniph
 */
public class ConsoleMenu
{
     /** screen user screen */
    private Console screen = null;

    /** activeUser user that uses menu */
    private Customer activeUser = null;

    /** shop shop that uses this menu */
    private Shop shop = null;

    /**
     * Class constructor specifying shop that uses this menu
     *      @param shop shop that uses menu. Cannot be null
     */
    public ConsoleMenu(Shop shop)
    {
        try {
            initScreen();
            registerShop(shop);
        } catch (IOException e){
            System.out.println("An error occured. Could not get system console. Aborting..");
            System.exit(-1);
        } catch (NullPointerException n){
            System.out.println("An error occured. You cannot create menu without a shop that uses it. Aborting..");
            System.exit(-1);
        }
    }

    /**
     *  Initialize Screen
     *  @throws IOException if screen could not be initialized
     */
    public void initScreen() throws IOException
    {
        screen = System.console();
        if (screen == null)
            throw new IOException();
    }

    public Console getScreen()
    {
        return screen;
    }

    public Customer getActiveUser()
    {
        return activeUser;
    }

    public void setActiveUser(Customer activeUser)
    {
        this.activeUser = activeUser;
    }

    public void registerShop(Shop shop) throws NullPointerException
    {
        if (shop == null)
            throw new NullPointerException();
        this.shop = shop;
    }

    public Shop getShop()
    {
        return this.shop;
    }

    public void printHelpControls()
    {
       System.out.println("\t1 - Change user");
       System.out.println("\t2 - Add item to your shopping cart");
       System.out.println("\t3 - Delete item from your shopping cart");
       System.out.println("\t4 - Show your shopping cart info");
       System.out.println("\t5 - Proceed to checkout (Complete order)");
       System.out.println("\t6 - Show your order history");
       System.out.println("\t9 - Exit");
    }

    //Utility methods
    public void printString(String string)
    {
        getScreen().printf("%s\n", string);
    }

    public int selectNumber(int lowerBound, int UpperBound, String outOfBoundsErrorMessage)
    {
       Scanner s = new Scanner(System.in);
       int num =  s.nextInt();

       while (num < lowerBound || num >= UpperBound){
            printString(outOfBoundsErrorMessage);
            num = s.nextInt();
       }
       return num;
    }

    public void printCustomerList()
    {
        int size = getShop().getNumberOfCustomers();
        for (int i = 0; i < size; i++){
            printString(i + " " + getShop().getCustomer(i).getName());
        }
    }

    public void printItemList()
    {
        int size = getShop().getNumberOfAvailableItems();
        for (int i = 0; i < size; i++){
            printString(i + " " + getShop().getAvailableItem(i));
        }
    }


    public void printOrdersItems(Order order)
    {
        int size = order.size();
        for (int i = 0; i < size; i++){
            printString(i + " " + order.getItem(i));
        }
    }

    public void setActiveCustomer()
    {
        printString("Select a customer from a list below");
        printCustomerList();
        printString("Enter the number of customer you'd like to choose: ");
        int choice = selectNumber(0, getShop().getNumberOfCustomers(), "Such customer number does not exist. Please try again.");
        setActiveUser(getShop().getCustomer(choice));
        printString("You're now registered as " + getActiveUser().getName() + "\n");
    }

    public void addItemToBasket()
    {
        if (getActiveUser() == null)
            throw new NullPointerException("You must select a user first!");

        printString("List of available items: ");
        printItemList();
        printString("Enter the number of item you'd like to add to your shopping basket");
        int choice = selectNumber(0, getShop().getNumberOfAvailableItems(), "Such item number does not exist. Please try again");

        /* TODO: fix according to API. Active user is state of UI not shop! */
        Item selectedItem = getShop().getAvailableItem(choice);
        if (selectedItem.getQuantity() > 0){
            getActiveUser().getCurrentOrder().addItem(selectedItem);
            selectedItem.setQuantity(selectedItem.getQuantity() - 1);
            printString(selectedItem + " was succesfully added to your shopping basket. Proceed to checkout whenever you are ready.");
        }
        else{
            printString("Your selected item is not available at the moment");
        }
    }

    public void removeItemFromBasket()
    {
       if (getActiveUser() == null)
            throw new NullPointerException("You must select a user first!");

       if (getActiveUser().getCurrentOrder().size() > 0)
       {
        printString("Items in your shopping basket");
        printOrdersItems(getActiveUser().getCurrentOrder());
        printString("Enter the number of item you'd like to remove from your shopping basket");
        int choice = selectNumber(0, getActiveUser().getCurrentOrder().size() ,"Index out of range");
        Item selectedItem = getActiveUser().getCurrentOrder().getItem(choice);

        getActiveUser().getCurrentOrder().removeItem(choice);
        selectedItem.setQuantity(selectedItem.getQuantity() + 1);

        printString("Item was deleted from your shopping basket");
       }
       else
           printString("Your shopping cart is empty!");
       }

    public void printBasket()
    {
        if (getActiveUser() == null)
            throw new NullPointerException("You must select a user first!");

        if (getActiveUser().getCurrentOrder().size() > 0){
            printString("Items in your shopping cart");
            printOrdersItems(getActiveUser().getCurrentOrder());
        }
        else
            printString("Your shopping cart is empty");
    }

          /** Main method */
   public void run() throws InputMismatchException
   {
       Scanner sc = new Scanner(System.in);
       int number = 0;

       System.out.println("\n Welcome to E-SHOP menu. Please, select a user first.");

       while (number != 9){
           printHelpControls();
           number = sc.nextInt();

           try {
               switch (number){
                   case 1:
                       setActiveCustomer();
                       break;
                   case 2:
                        addItemToBasket();
                        break;
                   case 3:
                       removeItemFromBasket();
                       break;
                   case 4:
                       printBasket();
                       break;
                   case 5:
                       if (getActiveUser() == null)
                           throw new NullPointerException("You must select a user first!");
                       if (getActiveUser().getCurrentOrder().size() != 0){
                           getActiveUser().completeCurrentOrder();
                           printString("Your current order was marked as completed and added to your order history.");
                       }
                       else
                           printString("You have no items in your shopping cart!");
                       break;
                   case 6:
                       if (getActiveUser() == null)
                           throw new NullPointerException("You must select a user first!");
                       if (getActiveUser().size() > 0){
                           for (int i = 0; i < getActiveUser().size(); i++){
                              //Print items list
                              printString("Order #" + i + " :");
                              printOrdersItems(getActiveUser().getOrder(i));
                           }
                       }
                       else
                           printString("Your order history is empty.");
                       break;
                   case 9:
                       break;
                   default:
                       printString("Option unrecognized: " + number);
                       break;
               }
           }
           catch (NullPointerException e){
               printString(e.getMessage());
           }
       }
       printString("\tE-SHOP Console menu v0.2");
       printString("\t Tumas Bajoras PS3");
   }
}