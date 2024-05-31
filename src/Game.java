import java.util.Scanner;

public class Game {

    // portable newline
    private final static String NEWLINE = System.getProperty("line.separator");
    private static Scanner kb = new Scanner(System.in);

    private Dungeon dungeon;     // the dungeon
    private char MONSTER;        // name of the monster (A - Z)
    private char ROGUE = '@';    // name of the rogue
    private int N;               // board dimension
    private Site monsterSite;    // location of monster
    private Site rogueSite;      // location of rogue
    private Monster monster;     // the monster
    private Rogue rogue;         // the rogue

    // initialize board from file
    public Game(In in, boolean monsterEasy, boolean rogueEasy) {

        // read in data
        N = Integer.parseInt(in.readLine());
        char[][] board = new char[N][N];
        for (int i = 0; i < N; i++) {
            String s = in.readLine();
            for (int j = 0; j < N; j++) {
                board[i][j] = s.charAt(2 * j);

                // check for monster's location
                if (board[i][j] >= 'A' && board[i][j] <= 'Z') {
                    MONSTER = board[i][j];
                    board[i][j] = '.';
                    monsterSite = new Site(i, j);
                }

                // check for rogue's location
                if (board[i][j] == ROGUE) {
                    board[i][j] = '.';
                    rogueSite = new Site(i, j);
                }
            }
        }
        dungeon = new Dungeon(board);
        monster = new Monster(this, monsterEasy);
        rogue = new Rogue(this, rogueEasy);
    }

    // return position of monster and rogue
    public Site getMonsterSite() {
        return monsterSite;
    }

    public Site getRogueSite() {
        return rogueSite;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    // Clear The Terminal Screen
    public static void clearDisplay() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // play until monster catches the rogue
    public void play(int option, boolean monsterEasy, boolean rogueEasy) {
        boolean gameOver = false;
    
        for (int t = 1; !gameOver; t++) {
            System.out.println("Move " + t);
            System.out.println();
    
            // Monster moves
            if (monsterSite.equals(rogueSite)) break;
            Site next = option == 1 ? monster.userMove() : monster.move();
            if (dungeon.isLegalMove(monsterSite, next)) {
                monsterSite = next;
            } else {
                throw new RuntimeException("Monster caught cheating");
            }
            System.out.println(this);
    
            // Rogue moves
            if (monsterSite.equals(rogueSite)) break;
            next = option == 2 ? rogue.userMove() : rogue.move();
            if (dungeon.isLegalMove(rogueSite, next)) {
                rogueSite = next;
            } else {
                throw new RuntimeException("Rogue caught cheating");
            }
            System.out.println(this);
    
            // Check if game over
            if (monsterSite.equals(rogueSite)) {
                gameOver = true;
            }
        }
    
        System.out.println("Caught by monster");
    }
    

    // string representation of game state (inefficient because of Site and string concat)
    public String toString() {
        String s = "";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Site site = new Site(i, j);
                if (rogueSite.equals(monsterSite) && (rogueSite.equals(site))) s += "* ";
                else if (rogueSite.equals(site)) s += ROGUE + " ";
                else if (monsterSite.equals(site)) s += MONSTER + " ";
                else if (dungeon.isRoom(site)) s += ". ";
                else if (dungeon.isCorridor(site)) s += "+ ";
                else if (dungeon.isRoom(site)) s += ". ";
                else if (dungeon.isWall(site)) s += "  ";
            }
            s += NEWLINE;
        }
        return s;
    }

    //Displays Welcome Messages to User
    private static void welcomeDisplay() {
        clearDisplay();
        System.out.println("++++++++++++++++++++++++++++");
        System.out.println("++       WELCOME TO       ++");
        System.out.println("++      ROGUE-A-LIKE      ++");
        System.out.println("++          GAME          ++");
        System.out.println("++++++++++++++++++++++++++++");
        System.out.println("\nPRESS ENTER TO START!");
        String enter = kb.nextLine();
        if (enter != null) {
            clearDisplay();
        }
    }

    //Interface to Choose Dungeon
    private static char chooseDungeon() {
        System.out.println("Please Choose Dungeon from A to R: ");
        String dungeon = "";
        boolean validInput = false;

        while (!validInput) {
            dungeon = kb.nextLine().toUpperCase();
            if (dungeon.length() == 1 && dungeon.charAt(0) >= 'A' && dungeon.charAt(0) <= 'R') {
                validInput = true;
            } else {
                System.out.println("You inputted an invalid letter, please try again: ");
            }
        }
        clearDisplay();
        return dungeon.charAt(0);
    }

    private static int gameModeSelection() {
        String gameMode = "";
        boolean validInput = false;
        int option = 0;

        System.out.println("Please Select a Game Mode: ");
        System.out.println("1. Play As Monster");
        System.out.println("2. Play As Rogue");
        System.out.println("3. Computer Play Both");

        while (!validInput) {
            System.out.print("Enter a number (1-3): ");
            gameMode = kb.nextLine();
            if (gameMode.equals("1") || gameMode.equals("2") || gameMode.equals("3")) {
                option = Integer.parseInt(gameMode);
                validInput = true;
            } else {
                System.out.println("Invalid option. Please input the correct option (1-3):");
            }
        }

        return option;
    }

    private static boolean difficultySelection() {
        String difficulty = "";
        boolean validInput = false;
        boolean isEasy = false;

        System.out.println("Please Select a Difficulty Level: ");
        System.out.println("1. Easy");
        System.out.println("2. Difficult");

        while (!validInput) {
            System.out.print("Enter a number (1-2): ");
            difficulty = kb.nextLine();
            if (difficulty.equals("1")) {
                isEasy = true;
                validInput = true;
            } else if (difficulty.equals("2")) {
                isEasy = false;
                validInput = true;
            } else {
                System.out.println("Invalid option. Please input the correct option (1-2): ");
            }
        }

        return isEasy;
    }

    public static void moveInstruction() {
        System.out.println("When Playing As Monster or Rogue");
        System.out.println("Please Use Your Keyboard and Beware Of Walls");
        System.out.println("You May Move In Any Directions You Want When You Are In A Room");
        System.out.println("You May Only Move To only 4 Different Directions When You Are In Corridor");
        System.out.println("Directions: ");
        System.out.println("Q = North West, W = North, E = North East");
        System.out.println("A = West, S = South, D = East");
        System.out.println("Z = South West, X = South, C = South East");
        System.out.println("Press Any Key to Continue...");
        String enter = kb.nextLine();
        if (enter != null) {
            clearDisplay();
        }
    }

    public static void main(String[] args) {
        while (true) {
            welcomeDisplay();
            char dungeon = chooseDungeon();
            In stdin = new In("./Dungeons/dungeon" + dungeon + ".txt");
            moveInstruction();
    
            int option = gameModeSelection();
            boolean monsterEasy = false;
            boolean rogueEasy = false;
    
            if (option == 1) {
                rogueEasy = difficultySelection();
            } else if (option == 2) {
                monsterEasy = difficultySelection();
            }
    
            Game game = new Game(stdin, monsterEasy, rogueEasy);
            System.out.println(game);
            game.play(option, monsterEasy, rogueEasy);
            System.out.println("Play again? (Y/N): ");
            
            boolean validInput = false;
            while (!validInput) {
                String choice = kb.nextLine().toUpperCase();
                if (choice.equals("Y")) {
                    validInput = true;
                } else if (choice.equals("N")) {
                    System.out.println("We hope to see you again! Goodbye!");
                    return;
                } else {
                    System.out.println("Invalid option. Please input the correct option (Y/N): ");
                }
            }
        }
    }
}
