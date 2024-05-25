

//Import Section
import java.util.Random;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        boolean lose = false;           //boolean signaling wether player lost when game is over
        Minefield field = new Minefield(5,5,5); //creates a minefield on the lowest bounds
        Scanner modeScanner = new Scanner(System.in);   //Scanner for mode selection
        Scanner debugScanner = new Scanner(System.in);     //Scanner for debug selection
        String gameMode;
        int bounds = 5;                                  //first initialized to the lowest bounds
        while (true) {                                  //while loop so continues until player enters a valid game mode
            System.out.println("Choose Difficulty Level:");
            System.out.println("[Easy]___[Medium]___[Hard]\n");
            gameMode = modeScanner.nextLine().toLowerCase();            //switch to all lower case in case of uppercase error
            switch (gameMode) {
                case "easy":
                    bounds = 5;                                         //no need to recreate minefield since it first initalized to this bound
                    break;
                case "medium":
                    field = new Minefield(9, 9, 12);        //creates new minefield based on mode
                    bounds = 9;
                    break;
                case "hard":
                    field = new Minefield(20, 20, 40);
                    bounds = 20;
                    break;
                default:
                    System.out.println(gameMode + " is an invalid difficulty.");
                    continue;                       // Ask again for input
            }
            break;                                  // Break the loop if valid input is received
        }
        String debugMode;
        while (true) {                                                   //while loop so continues until player enters a valid debug mode
            System.out.println("Play in Debug mode?");
            System.out.println("[Yes]___[No]\n");
            debugMode = debugScanner.nextLine().toLowerCase();          //switch to all lower case in case of uppercase error
            switch (debugMode) {
                case "yes":
                    field.setDebugMode(true);                           //sets variable debugger to true in minefield, so it would call the debug function
                    break;
                case "no":
                    break;
                default:
                    System.out.println(debugMode + " is an invalid answer.");
                    continue;                          // Ask again for input
            }
            break;                                  // Break the loop if valid input is received
        }
        field.debug();                                      //Prints out only if debugMode is true
        System.out.println(field);                          //Print field
        boolean validFirst = false;
        while (!validFirst) {                           //Check if starting coordinates are valid
            System.out.println("Starting Move");
            System.out.println("format: [y] [x]\n");
            String guessMoves = modeScanner.nextLine();
            String[] guessMovesPart = guessMoves.split(" ");
            if (guessMovesPart.length != 2) {
                System.out.println("Invalid Guess!");
                continue;
            }
            try {                                                           //If player enters in invalid format
                int xRow = Integer.parseInt(guessMovesPart[0]);
                int yCol = Integer.parseInt(guessMovesPart[1]);

                if (xRow < 0 || xRow >= bounds || yCol < 0 || yCol >= bounds) {         //checks bounds for coordinates and loop until we get a valid one
                    System.out.println("Invalid Guess!");
                    continue;
                }
                // Creates mines based on difficulty level
                switch (gameMode.toLowerCase()) {
                    case "easy":
                        field.createMines(xRow, yCol, 5);
                        break;
                    case "medium":
                        field.createMines(xRow, yCol, 12);
                        break;
                    case "hard":
                        field.createMines(xRow, yCol, 40);
                        break;
                    default:
                        break;
                }

                field.evaluateField();                          //place numbers in
                field.revealStartingArea(xRow, yCol);
                validFirst = true;                              //breaks out of loop if it passes all if statements
            } catch (NumberFormatException e) {
                System.out.println("Invalid Guess! Please enter integer values for x and y.");
            }
        }

        while(!field.gameOver()){
            field.debug();                                          //print out debug board if player sets debugMode true
            System.out.println(field);                              //prints out normal board alongside
            System.out.println("Your Move!");
            System.out.println(field.getFlag() + " flags left");
            System.out.println("format: [y] [x] [flag(Yes/No)]\n");
            String guessMoves = modeScanner.nextLine();
            String[] guessMovesPart = guessMoves.split(" ");
            if (guessMovesPart.length != 3){
                System.out.println("Invalid Guess!");
                continue;
            }
            try {                                                       //if player enters in wrong format or numbers
                int xRow = Integer.parseInt((guessMovesPart[0]));
                int yCol = Integer.parseInt((guessMovesPart[1]));
                if (xRow > field.getRow() || xRow < 0 || yCol > field.getCol() || yCol < 0) { //Check if guess is out of bounds and prompt user to enter again
                    System.out.println("Out of bounds!");
                    continue;
                }
                boolean flagPlace = false;                      //boolean if player would like to place a flag
                switch (guessMovesPart[2].toLowerCase()) { //Check if player wants flag placed and sets variable flag to true or false
                    case "yes":
                        flagPlace = true;
                        break;
                    case "no":
                        break;
                    default:
                        System.out.println(guessMovesPart[2] + " is invalid option");
                        continue;
                }
                if (flagPlace && field.getFlag() - 1 < 0) {                //Check if there is enough flags and if player wants to place flag
                    System.out.println("Not enough Flags");
                    continue;
                }
                if (!field.guess(xRow, yCol, flagPlace)){        //If guess returns false end game
                    lose = true;
                    //If returns false player hits mine meaning they lost and no need to break since guess would automatically set gameOver to false thus ending the loop
                }

            } catch (NumberFormatException e){
                System.out.println("Invalid move!");
            }
        }
        System.out.println(field);
        if (lose) {
            System.out.println("You LOST!!!");                          //prints out board and statement based on if the user lost or won
        } else {
            System.out.println("You WON!!!!");
        }
    }
}
