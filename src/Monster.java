import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Monster {
    private Game game;
    private Dungeon dungeon;
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

    public Monster(Game game) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.bfs = new BFS(dungeon);
    }

    // Take a step towards the rogue using BFS to find the shortest path
    public Site move() {
        List<Site> path = bfs.findShortestPath(game.getMonsterSite(), game.getRogueSite());
        if (path != null && path.size() > 1) {
            return path.get(1); // Move to the next step in the path
        }
        return game.getMonsterSite(); // Stay in the same place if no path found
    }
    public Site userMove(){
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
        if(dungeon.isLegalMove(currentSite, userMove)==true){
            return userMove;
        }
        System.out.println("Oops! You Hit A Wall");
        return currentSite;
        
    }
}
