//Written by Maggie Jiang, Jian0886

import java.util.Queue;
import java.util.Random;

public class Minefield {
    private int row;                     //global variables for rows, columns, number of flags, if game is over, and if debugMode is on
    private int col;
    private int flag;
    private Cell[][] field;
    private boolean gameOver;
    private boolean debugMode = false;

    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";
    public static final String ANSI_GREEN_BRIGHT = "\u001b[32m;1m";

    public Minefield(int rows, int columns, int flags) {    //constructor
        this.row = rows;
        this.col = columns;
        this.flag = flags;
        field = new Cell[row][col];
        for (int i = 0; i< row; i++){
            for (int j =0; j < col; j++){
                field[i][j] = new Cell(false, "0");             //initialize each cell in the field to be not revealed and at 0
            }
        }
    }

    public void evaluateField() {
        for (int i = 0; i < row; i++){          //Iterates through each of the rows and columns
            for (int j = 0; j< col; j++){
                Cell current = field[i][j];         //nice instance variable for the current cell
                int newVal;                         //instance variable for new integer
                if (current.getStatus().equals("M")){           //if current cell is a mine
                    if (i+1 < row && !field[i+1][j].getStatus().equals("M")){           //make sure that adjacent cell is in bounds and isn't also a mine
                        //if status is already a number inclement by 1
                        newVal = Integer.parseInt(field[i+1][j].getStatus()) +1;    //changes string to int and add one
                        field[i + 1][j].setStatus(String.valueOf(newVal));          //assign new value and turning back int to a string
                                                                                     //i+j
                    }
                    if (i-1 >= 0 && !field[i-1][j].getStatus().equals("M")){             //Same comment as above and for the rest of adjacent blocks
                        newVal = Integer.parseInt(field[i-1][j].getStatus()) +1;
                        field[i - 1][j].setStatus(String.valueOf(newVal));

                    }
                    if (j+1 < col && !field[i][j+1].getStatus().equals("M")){                  //row,col+
                        newVal = Integer.parseInt(field[i][j + 1].getStatus()) +1;
                        field[i][j + 1].setStatus(String.valueOf(newVal));
                    }
                    if (j-1 >= 0 && !field[i][j-1].getStatus().equals("M")){                     //row,col-
                        newVal = Integer.parseInt(field[i][j - 1].getStatus()) +1;
                        field[i][j - 1].setStatus(String.valueOf(newVal));

                    }
                    if (i-1 >= 0  && j-1 > 0 && !field[i-1][j-1].getStatus().equals("M")){       //row-,col-
                        newVal = Integer.parseInt(field[i-1][j - 1].getStatus()) +1;
                        field[i-1][j-1].setStatus(String.valueOf(newVal));

                    }
                    if (i+1 < row  && j+1 < col && !field[i+1][j+1].getStatus().equals("M")){       //row+,col+
                        newVal = Integer.parseInt(field[i+1][j+1].getStatus()) +1;
                        field[i+1][j+1].setStatus(String.valueOf(newVal));

                    }
                    if (i-1 >= 0  && j+1 < col && !field[i-1][j+1].getStatus().equals("M")){       //row-,col+
                        newVal = Integer.parseInt(field[i-1][j+1].getStatus()) +1;
                        field[i-1][j+1].setStatus(String.valueOf(newVal));

                    }
                    if (j-1 >= 0  && i+1 < row && !field[i+1][j-1].getStatus().equals("M")){       //row+,col-
                        newVal = Integer.parseInt(field[i+1][j-1].getStatus()) +1;
                        field[i+1][j-1].setStatus(String.valueOf(newVal));
                    }
                }
            }
        }
    }

    public void createMines(int x, int y, int mines) {
        int tempMines = mines;                                                      //instance variable to keep tack on how many mine has been placed
        int tempX = randCords(mines);                                               //helper function the generates random coordinates based on the mines
        int tempY = randCords(mines);
        Cell current = field[tempX][tempY];
        while (tempMines > 0) {                                                     //loop until no more mines left to place
            if ((!current.getRevealed()||debugMode) && current != field[x][y] && !current.getStatus().equals("M")) {
                //Checks if cell is revealed, is equal to starting coordinates, or is already a mine
                current.setStatus("M");
                tempMines--;                //subtracts mines as we place one
            }
            tempX = randCords(mines);       //get new cords
            tempY = randCords(mines);
            current = field[tempX][tempY];
        }
    }

    public int randCords(int mines){        //helper function to generate random coordinates
        Random rand = new Random();
        int cord = 0;
        switch (mines){                     //Depends on the number of mines it matches it to the cell bounds
            case 5:
                cord = rand.nextInt(5);
                break;
            case 12:
                cord = rand.nextInt(9);
                break;
            case 40:
                cord = rand.nextInt(20);
                break;
            default:
                break;
        }
        return cord;
    }


    public boolean guess(int x, int y, boolean flag) {
        Cell current = field[x][y];
        if (flag){              //If player wants to place a flag
            if (current.getStatus().equals("F")){       //Check if there is already flag there
                current.setRevealed(true);
                return true;
            }else {
                current.setStatus("F");
                current.setRevealed(true);
                this.flag--;                    //subtract one flag form our global variable
                return true;
            }
        }else{                                  //If player just wants to check cell
            if (current.getStatus().equals("0")){         //If cell is empty
                revealZeroes(x, y);
            }else if (current.getStatus().equals("M")){     //If player guess on a mine
                current.setRevealed(true);
                gameOver = true;                        //set game to be over
                return false;
            }
        }
        current.setRevealed(true);
        return true;
    }


    public boolean gameOver() {
        if (gameOver) {                         //if gameOver variable is already true then end game
            return gameOver;
        }
        boolean allReveal = true;                  //instance variable for if all non mine cells are revealed
        for (int i = 0; i < row; i++) {          //Iterates through each of the rows and columns
            for (int j = 0; j < col; j++) {
                Cell current = field[i][j];
                if (!current.getRevealed() && !current.getStatus().equals("M")) {               //if one of the cell is not revealed and is not a mine set allReveal false
                    allReveal = false;
                }
            }
        }
        return allReveal;
    }

    public void revealZeroes(int x, int y) {
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        stack.push(new int[]{x,y});                     //push inital cord into stack
        while (!stack.isEmpty()){                       //loops until stack is empty
            int[] current = stack.pop();                //pop first of the stack
            int xRow = current[0];
            int yCol = current[1];
            if (xRow < row && yCol < col && xRow >= 0 && yCol >= 0) {       //check if in bounds
                Cell curCell = field[xRow][yCol];
                if (!curCell.getRevealed() && curCell.getStatus().equals("0")) {    //if cell is not revealed and is 0 then reveal and push its adjacent cells
                    curCell.setRevealed(true);
                    stack.push(new int[]{xRow + 1, yCol});
                    stack.push(new int[]{xRow - 1, yCol});
                    stack.push(new int[]{xRow, yCol + 1});
                    stack.push(new int[]{xRow, yCol - 1});
                }
            }
        }
    }

    public void revealStartingArea(int x, int y) {
        Q1Gen<int[]> queue = new Q1Gen<>();
        queue.add(new int[]{x,y});                  //adds in intial cords into queue
        while(queue.length()!=0) {                  //loop unitl queue is empty
            int[] current = queue.remove();         //remove first from queue
            int xRow = current[0];
            int yCol = current[1];
            Cell curCell = field[xRow][yCol];
            if (curCell.getStatus().equals("M")) {             //if mine is found end method
                curCell.setRevealed(true);
                break;
            }else{
                curCell.setRevealed(true);                     //else reveal and add adjacent to queue if in bounds
            }
            if (xRow - 1 >= 0) {
                queue.add(new int[]{xRow - 1, yCol});
            }
            if (xRow + 1 < field.length) {
                queue.add(new int[]{xRow + 1, yCol});
            }
            if (yCol - 1 >= 0){
                queue.add(new int[]{xRow, yCol - 1});
            }
            if (yCol + 1 < col) {
                queue.add(new int[]{xRow, yCol + 1});
            }
        }
    }

    public boolean getDebug(){
        return debugMode;
    }       //getter for debugMode
    public void setDebugMode(boolean x){
        debugMode = x;
    }       //setter for debugMode
    public void debug() {
        if (debugMode) {                                        //if debugMode is set true print out this
            StringBuilder out = new StringBuilder();
            out.append("  ");
            for (int i = 0; i < row; i++) {
                if (i < 10) {                                       //if "i" is larger than 10 add another space for better formatting
                    out.append(" ");
                }
                out.append(" ");
                out.append(i);
            }
            out.append('\n');
            for (int i = 0; i < col; i++) {
                if (i < 10) {
                    out.append(" ");
                }
                out.append(i);
                out.append(" ");
                for (int j = 0; j < col; j++) {
                        out.append(" ");
                        switch (field[i][j].getStatus()) {       //Added different color for each case
                            case "M":
                                out.append(ANSI_RED + "M" + ANSI_GREY_BACKGROUND);
                                break;
                            case "F":
                                out.append(ANSI_RED_BRIGHT + "F" + ANSI_GREY_BACKGROUND);
                                break;
                            case "0":
                                out.append(ANSI_YELLOW + "0" + ANSI_GREY_BACKGROUND);
                                break;
                            case "1":
                                out.append(ANSI_BLUE + "1" + ANSI_GREY_BACKGROUND);
                                break;
                            case "2":
                                out.append(ANSI_GREEN + "2" + ANSI_GREY_BACKGROUND);
                                break;
                            case "3":
                                out.append(ANSI_PURPLE + "3" + ANSI_GREY_BACKGROUND);
                                break;
                            case "4":
                                out.append(ANSI_CYAN + "4" + ANSI_GREY_BACKGROUND);
                                break;
                            case "5":
                                out.append(ANSI_WHITE_BACKGROUND + "5" + ANSI_GREY_BACKGROUND);
                                break;
                            case "6":
                                out.append(ANSI_PURPLE_BACKGROUND + "6" + ANSI_GREY_BACKGROUND);
                                break;
                            case "7":
                                out.append(ANSI_BLUE_BRIGHT + "7" + ANSI_GREY_BACKGROUND);
                                break;
                            case "8":
                                out.append(ANSI_YELLOW_BRIGHT + "8" + ANSI_GREY_BACKGROUND);
                                break;
                            case "9":
                                out.append(ANSI_GREEN_BRIGHT + "9" + ANSI_GREY_BACKGROUND);
                            default:
                                out.append(field[i][j].getStatus());
                        }
                        out.append(" ");
                }
                out.append("\n");
            }
            System.out.println(out.toString());     //prints since we it is a void method
        }
    }

    public String toString() {      //same comments as debug
        StringBuilder out = new StringBuilder();
        out.append("  ");
        for(int i = 0; i < row; i++){
            if (i< 10){
                out.append(" ");
            }
            out.append(" ");
            out.append(i);
        }
        out.append('\n');
        for(int i = 0; i < col; i++) {
            if (i< 10){
                out.append(" ");
            }
            out.append(i);
            out.append(" ");
            for(int j = 0; j < col; j++) {
                if (field[i][j].getRevealed() || field[i][j].getStatus().equals("F")){ // if cell is already revealed or if it is a flag
                    out.append(" ");
                    switch (field[i][j].getStatus()){       //Added different color for each case
                        case "M":
                            out.append(ANSI_RED + "M" + ANSI_GREY_BACKGROUND);
                            break;
                        case "F":
                            out.append (ANSI_RED_BRIGHT + "F" + ANSI_GREY_BACKGROUND);
                            break;
                        case "0":
                            out.append(ANSI_YELLOW + "0" + ANSI_GREY_BACKGROUND);
                            break;
                        case "1":
                            out.append(ANSI_BLUE + "1"+ ANSI_GREY_BACKGROUND);
                            break;
                        case "2":
                            out.append(ANSI_GREEN + "2" + ANSI_GREY_BACKGROUND);
                            break;
                        case "3":
                            out.append(ANSI_PURPLE + "3" + ANSI_GREY_BACKGROUND);
                            break;
                        case "4":
                            out.append(ANSI_CYAN + "4" + ANSI_GREY_BACKGROUND);
                            break;
                        case "5":
                            out.append(ANSI_WHITE_BACKGROUND + "5" + ANSI_GREY_BACKGROUND);
                            break;
                        case "6":
                            out.append(ANSI_PURPLE_BACKGROUND + "6" + ANSI_GREY_BACKGROUND);
                            break;
                        case "7":
                            out.append(ANSI_BLUE_BRIGHT + "7" + ANSI_GREY_BACKGROUND);
                            break;
                        case "8":
                            out.append(ANSI_YELLOW_BRIGHT + "8" + ANSI_GREY_BACKGROUND);
                            break;
                        case "9":
                            out.append(ANSI_GREEN_BRIGHT + "9" + ANSI_GREY_BACKGROUND);
                        default:
                            out.append(field[i][j].getStatus());
                    }
                    out.append(" ");
                }else{
                    out.append(" - ");
                }
            }
            out.append("\n");
        }
        return out.toString();
    }

    //Getter for row, column, and flags
    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public int getFlag(){
        return flag;
    }
}
