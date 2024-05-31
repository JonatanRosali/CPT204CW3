import java.util.*;

public abstract class Characters {
    protected Game game;
    protected Dungeon dungeon;
    protected boolean isEasy;
    protected int N;
    protected static Map<String, int[]> directionMap = new HashMap<>();
    protected Scanner kb = new Scanner(System.in);

    static {
        directionMap.put("Q", new int[]{-1, -1}); // North West
        directionMap.put("W", new int[]{-1, 0});  // North
        directionMap.put("E", new int[]{-1, 1});  // North East
        directionMap.put("A", new int[]{0, -1});  // West
        directionMap.put("S", new int[]{0, 0});   // Stay
        directionMap.put("D", new int[]{0, 1});   // East
        directionMap.put("Z", new int[]{1, -1});  // South West
        directionMap.put("X", new int[]{1, 0});   // South
        directionMap.put("C", new int[]{1, 1});   // South East
    }

    public Characters(Game game, boolean isEasy) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.isEasy = isEasy;
        this.N = dungeon.size();
    }


    public Site userMove() {
        System.out.println("Enter your move (Q, W, E, A, S, D, Z, X, C):");
        String move;
        int[] directions;

        while (true) {
            move = kb.nextLine().toUpperCase();
            directions = directionMap.get(move);
            if (directions != null) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid move (Q, W, E, A, S, D, Z, X, C):");
            }
        }

        Site currentSite = getCurrentSite();
        Site userMove = new Site(currentSite.i() + directions[0], currentSite.j() + directions[1]);
        if (dungeon.isLegalMove(currentSite, userMove)) {
            return userMove;
        }
        System.out.println("Oops! You Hit A Wall");
        return currentSite;
    }

    protected abstract Site getCurrentSite();

    protected List<Site> getNeighbors(Site site) {
        return site.getNeighbors(dungeon);
    }

    // Ensure this method is accessible by subclasses
    protected Site randomMove() {
        Site current = getCurrentSite();
        Site move = null;
        int n = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Site site = new Site(i, j);
                if (dungeon.isLegalMove(current, site)) {
                    n++;
                    if (Math.random() <= 1.0 / n) move = site;
                }
            }
        }
        return move;
    }
}
