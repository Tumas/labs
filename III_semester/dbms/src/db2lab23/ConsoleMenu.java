package db2lab23;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ConsoleMenu
{
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    private PlayerManager pl;
    private TeamManager team;
    private GameManager game;
     //magic constant = tiek simboliu skiriama stulpelio pavadinimui
    private final int WIDTH_VAR = 15;

    private String mainMenu = "*** Duomenu Paieska\n\t" +
            "1) Ieskoti informacijos apie zaideja\n" +
            "*** Duomenu Atnaujinimas\n\t" +
            "2) Atnaujinti zaidejo duomenis\n" +
            "*** Duomenu salinimas\n\t" +
            "3) Isregistruoti zaideja\n" +
            "*** Transakcija ir duomenu iterpimas\n\t" +
            "4) Uzregistruoti rungtynes\n" +
            "*** Pabaiga\n\t" +
            "9) Iseiti\n";
            


    public ConsoleMenu(PlayerManager pl, TeamManager team, GameManager game)
    {
        this.pl = pl;
        this.team = team;
        this.game = game;
    }

    public void printString(String msg)
    {
        System.out.println(msg);
    }

    public String getString(String msg) throws IOException
    {
        printString(msg);
        return in.readLine();
    }

    public int getInt(String msg) throws IOException
    {
        String num = getString(msg);
        return Integer.parseInt(num);
    }

    public int getInt() throws IOException
    {
        return Integer.parseInt(in.readLine());
    }


    public float getFloat(String msg) throws IOException
    {
        String num = getString(msg);
        return Float.parseFloat(num);
    }

     public String formDelimiter(char ch, int num)
    {
        String result = "";
        if (num > 0){
            for (int i = 0; i < num; i++)
                result += ch;
        }
        return result;
    }

    public void printResultsHeader(ResultSet rs) throws SQLException
    {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        int width = 0;
        String temp;

        width = WIDTH_VAR * columnCount;

        System.out.println(formDelimiter('-', width));
        for (int i = 1; i <= columnCount; i++){
            //Uzrasom stulpelio pavadinima
            temp = meta.getColumnName(i);
            //Uzpildom likusia stulpeliui skirta vieta tarpais
            //System.out.print(temp + formDelimiter(' ', meta.getColumnDisplaySize(i) - temp.length()));
            System.out.print(temp + formDelimiter(' ', WIDTH_VAR - temp.length()));
        }
        System.out.println("\n" + formDelimiter('-', width));
    }

    public void printPlayers()
    {
         printString("\t *** ZAIDEJAI IR JU ASMENS KODAI ***");

         try {
            ResultSet results = pl.getAllPlayers();
            printResultsHeader(results);
            while (results.next()){
                printString(results.getString("AK") + " " + results.getString("Vardas") +
                " " + results.getString("Pavarde") + " " + results.getString("Pavadinimas"));
            }
         } catch (Exception e){
            printString(e.getMessage());
         }
    }

    public void enterPlayerStats(ResultSet results, int gameNumber) throws SQLException, IOException
    {
        String name, surname, ak;
        short rebounds, points, assists, fouls, turnOvers, steals, blocks;

        while (results.next())
        {
            ak = results.getString("AK");
            name = results.getString("Vardas");
            surname = results.getString("Pavarde");

            printString("\t *** Zaidejo " + name + " " + surname + " statistikos ivedimas ***");
            rebounds = (short) getInt("Atkovojo kamuoliu:");
            points = (short) getInt("Pelne tasku:");
            assists = (short) getInt("Atliko rezultatyviu perdavimu:");
            fouls = (short) getInt("Prasizenge kartu:");
            blocks = (short) getInt("Blokavo metimu:");
            steals = (short) getInt("Pereme kamuoliu:");
            turnOvers = (short) getInt("Suklydo kartu:");
            game.insertPlayerGameStats(ak, gameNumber, rebounds, assists, fouls, turnOvers, blocks, steals, points);
        }
    }

    public void printTeams()
    {
         printString("\t *** KOMANDOS IR JU NUMERIAI ***");

         try {
            ResultSet results = team.getAllTeams();
            while (results.next()){
                 printString(results.getString("Pavadinimas") + " - " +
                    results.getInt("Nr"));
            }
         } catch (Exception e){
            printString(e.getMessage());
         }
    }

    public void run() throws IOException
    {
        printString("* ---------------------- KREPSINIO TURNYRAS ---------------------- *");
        printString(" ------------------------------------------------------------------ ");
        printString(" Pasirinktite Veiksma");

        int selection = 0;
        Player player = new Player();
        ResultSet result = null;

        while (selection != 9){
            printString(mainMenu);
            selection = getInt();
            switch(selection){
                case 1:
                    //ieskoti info apie zaideja
                    printString("***** INFORMACIJA APIE ZAIDEJA *****");
                    player.name = getString("Ieskomo zaidejo vardas:");
                    player.surName = getString("Ieskomo zaidejo pavarde:");

                    try {
                        result = pl.getFullPlayer(player.name, player.surName);

                        if (result.next()){
                            printResultsHeader(result);
                            do {
                                String teamTitle  = result.getString("Pavadinimas");
                                float stats = result.getFloat("Rezultatyvumas");

                                printString(player.name +  "   " + player.surName + "   " + teamTitle + "   " + stats + "\n");
                            } while (result.next());
                        } else
                            printString("\n\t *** NIEKO NERASTA ***");
                        
                    } catch(SQLException e){
                        printString(e.getMessage());
                    }
                    break;
                case 2:
                    //update player data
                    printString("***** ZAIDEJO DUOMENU ATNAUJINIMAS *****");
                    printPlayers();

                    String updateMenu = "1) Keisti varda\n" +
                            "2) Keisti pavarde\n" +
                            "3) Keisti komanda\n" +
                            "4) Keisti marskineliu numeri\n" +
                            "5) Keisti ugi\n" +
                            "6) Keisti svori\n" +
                            "9) Irasyti pakeitimus ir iseiti";

                    try {
                        player.ak = getString("Pasirinkite zaideja ivesdami jo AK:");
                        result = pl.getPlainPlayerAK(player.ak);
                        if (!result.next())
                            throw new Exception("Blogai ivestas AK!");

                        player.map(result);
                        int choice = 0;
                        int count = 0;
                        while (choice != 9){
                            printString(updateMenu);
                            choice = getInt();
                            
                            switch(choice){
                                case 1:
                                    player.name = getString("Naujas vardas:");
                                    break;
                                case 2:
                                    player.surName = getString("Nauja pavarde:");
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    player.number = getInt("Naujas marskineliu numeris:");
                                    break;
                                case 5:
                                    player.height = getFloat("Naujas ugis formatu X.YY:");
                                    break;
                                case 6:
                                    player.weight = getInt("Naujas svoris:");
                                    break;
                                case 9:
                                    count = pl.updatePlayer(player.ak, player.name, player.surName, player.number, player.height,
                                                player.weight, player.teamNumber);
                                default:
                                    break;
                            }

                            if (count > 0)
                                printString("\t *** Duomenys SEKMINGAI atnaujinti! *** ");
                        }

                    } catch (Exception e){
                        printString(e.getMessage());
                    }
                    break;
                case 3:
                    //delete player
                    printString("***** ZAIDEJO ISREGISTRAVIMAS *****");
                    printPlayers();

                      try {
                        player.ak = getString("Pasirinkite zaideja ivesdami jo AK:");
                        if (pl.deletePlayer(player.ak) > 0)
                            printString("\t *** Zaidejas SEKMINGAI isregistruotas! ***");
                        else
                            throw new Exception("Nepavyko pasalinti zaidejo: greiciausiai blogai suvestas ak.");
                        
                      } catch (Exception e){
                        printString(e.getMessage());
                      }

                    break;
                case 4:
                    //register game
                    printString("***** NAUJU VARZYBU UZREGISTRAVIMAS *****");
                    printTeams();

                    try {
                        int homeNumber = getInt("Namie zaidusios komandos numeris:");
                        int homeScore = getInt("Namie zaidusios komandos imestu tasku skaicius:");
                        int awayNumber = getInt("Varzovu komandos numeris:");

                        if (homeNumber == awayNumber)
                            throw new Exception("Komanda su savimi zaisti negali");

                        int awayScore = getInt("Varzovu imestu tasku skaicius:");
                        String data = getString("Iveskite varzybu data formatu: YYYY-MM-DD");
                    
                        try {
                            game.getConnection().setAutoCommit(false);
                            game.insertGame(homeNumber, awayNumber, homeScore, awayScore, data);
                            
                            ResultSet rs = game.guessGameNumber(homeNumber, awayNumber, homeScore, awayScore, data);
                            int gameNumber = -1;
                            if (rs.next())
                                gameNumber = rs.getInt("Nr");
                            else
                                throw new SQLException("Nepavyko gauti rungtyniu numerio");

                            ResultSet homePlayers = pl.getPlayersByTeam(homeNumber);
                            ResultSet awayPlayers = pl.getPlayersByTeam(awayNumber);
                            enterPlayerStats(homePlayers, gameNumber);
                            enterPlayerStats(awayPlayers, gameNumber);

                            game.getConnection().setAutoCommit(true);
                            printString("\t**** Rungtynes ir ju statistika sekmingai uzregistruota ****");
                            
                        } catch(SQLException e){
                            printString("SQLException: " + e.getMessage());
                            game.getConnection().rollback();
                            game.getConnection().setAutoCommit(true);
                        }
                    } catch (Exception e){
                        printString(e.getMessage());
                    }

                    break;
                case 9:
                    break;
                default:
                    printString("Neatpazintas pasirinkimas!");
            }
        }
    }
}

class Player
{
    String name, surName, ak;
    int weight, number, teamNumber;
    float height;

    public Player(){}

    public Player(String ak, int number, String name, String surName,
            float height, int weight, int teamNumber)
    {
        this.ak = ak;
        this.number = number;
        this.name = name;
        this.surName = surName;
        this.weight = weight;
        this.height = height;
        this.teamNumber = teamNumber;
    }

    public boolean map(ResultSet result)
    {
        try {
            ak = result.getString("AK");
            name = result.getString("Vardas");
            surName = result.getString("Pavarde");
            weight = result.getInt("Svoris");
            teamNumber = result.getInt("Komanda");
            height = result.getFloat("Ugis");
            number = result.getInt("Nr");
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}