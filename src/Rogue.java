import java.util.ArrayList;
import java.util.List;

public class Rogue {
    private Game game;
    private Dungeon dungeon;
    private DFS dfs;

    public Rogue(Game game) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.dfs = new DFS(dungeon, game);
    }

    // Take a step to maximize the distance from the monster or move to a neighboring room if no cycle exists
    public Site move() {
        Site currentSite = game.getRogueSite();
        System.out.println("Rogue current position: (" + currentSite.i() + ", " + currentSite.j() + ")");
        
        // Check if there's a corridor cycle the rogue can use
        if (dfs.canFindCorridorCycle()) {
            System.out.println("Corridor cycle found, trying to move in cycle.");
            List<Site> path = farthestPathInCycle(currentSite);
            if (path != null && path.size() > 1) {
                System.out.println("Rogue moving in cycle to: (" + path.get(1).i() + ", " + path.get(1).j() + ")");
                return path.get(1); // Move to the next step in the path
            } else {
                System.out.println("No valid path found in cycle.");
            }
        } else {
            System.out.println("No corridor cycle found. Moving to neighboring room farthest from the monster.");
            // Move to the neighboring room farthest from the monster
            List<Site> neighbors = getNeighbors(currentSite);
            Site farthestNeighbor = null;
            int maxDistance = -1;
            for (Site neighbor : neighbors) {
                int distance = neighbor.manhattanTo(game.getMonsterSite());
                if (distance > maxDistance && dungeon.isRoom(neighbor)) {
                    maxDistance = distance;
                    farthestNeighbor = neighbor;
                }
            }
            if (farthestNeighbor != null) {
                System.out.println("Rogue moving to farthest neighboring room: (" + farthestNeighbor.i() + ", " + farthestNeighbor.j() + ")");
                return farthestNeighbor;
            } else {
                System.out.println("No neighboring room found.");
            }
        }
        
        System.out.println("Rogue stays in the same place.");
        return currentSite; // Stay in the same place if no path found
    }

    private List<Site> farthestPathInCycle(Site currentSite) {
        boolean[][] visited = new boolean[dungeon.size()][dungeon.size()];
        List<Site> path = new ArrayList<>();
        List<Site> longestPath = new ArrayList<>();
        dfs.dfsLongestPathInCycle(currentSite, currentSite, visited, path, longestPath);
        return longestPath;
    }

    private List<Site> getNeighbors(Site site) {
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } }; // N, S, W, E, NW, NE, SW, SE
        List<Site> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int i = site.i() + dir[0];
            int j = site.j() + dir[1];
            if (i >= 0 && i < dungeon.size() && j >= 0 && j < dungeon.size() && !dungeon.isWall(new Site(i, j))) {
                neighbors.add(new Site(i, j));
            }
        }
        return neighbors;
    }
}
