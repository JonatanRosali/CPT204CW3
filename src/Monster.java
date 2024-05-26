import java.util.*;

public class Monster {
    private Game game;
    private Dungeon dungeon;
    private boolean isEasy;
    private int N;
    private BFS bfs;
    private Scanner kb = new Scanner(System.in);
    private static Map<String, int[]> directionMap = new HashMap<>();

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

    public Monster(Game game, boolean isEasy) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.isEasy = isEasy;
        this.bfs = new BFS(dungeon);
        this.N = dungeon.size();
    }

    // Take a step towards the rogue using BFS to find the shortest path
    public Site move() {
        if (isEasy) {
            return randomMove();
        } else {
            return intelligentMove();
        }
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

        Site currentSite = game.getMonsterSite();  // Assuming you are moving a monster
        Site userMove = new Site(currentSite.i() + directions[0], currentSite.j() + directions[1]);
        if (dungeon.isLegalMove(currentSite, userMove)) {
            return userMove;
        }
        System.out.println("Oops! You Hit A Wall");
        return currentSite;
    }

    // Choose a random legal move
    private Site randomMove() {
        Site monsterSite = game.getMonsterSite();
        List<Site> neighbors = getNeighbors(monsterSite);
        List<Site> legalMoves = new ArrayList<>();
        for (Site neighbor : neighbors) {
            if (dungeon.isLegalMove(monsterSite, neighbor)) {
                legalMoves.add(neighbor);
            }
        }
        if (legalMoves.isEmpty()) {
            return monsterSite; // Stay in place if no move is possible
        }
        return legalMoves.get((int) (Math.random() * legalMoves.size()));
    }

    // Intelligent move logic for difficult difficulty
    private Site intelligentMove() {
        List<Site> path = bfs.findShortestPath(game.getMonsterSite(), game.getRogueSite());
        if (path != null && path.size() > 1) {
            return path.get(1); // Move to the next step in the path
        }
        return game.getMonsterSite(); // Stay in the same place if no path found
    }

    private List<Site> getNeighbors(Site site) {
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } }; // N, S, W, E, NW, NE, SW, SE
        List<Site> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int i = site.i() + dir[0];
            int j = site.j() + dir[1];
            if (i >= 0 && i < N && j >= 0 && j < N && !dungeon.isWall(new Site(i, j))) {
                neighbors.add(new Site(i, j));
            }
        }
        return neighbors;
    }
}
