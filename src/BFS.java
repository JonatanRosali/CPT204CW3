import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BFS{
    private Dungeon dungeon;
    private Game game;

    public BFS(Dungeon dungeon){
        this.dungeon = dungeon;
        this.game = game;
    }
    public void bfsMonster(){
        Site monsterSite = game.getMonsterSite();
        int N = dungeon.size();
        boolean[][] visited = new boolean[N][N];
        Queue<Site> queue = new LinkedList<>();
        queue.offer(monsterSite);
        visited[monsterSite.i()][monsterSite.j()] = true;
        while (!queue.isEmpty()) {
            Site current = queue.poll();
            // Process current vertex
            
            // Example: Print current vertex coordinates
            System.out.println("Visiting vertex at: (" + current.i() + ", " + current.j() + ")");

            // Explore neighbors
            for (Site neighbor : getNeighbors(current)) {
                if (!visited[neighbor.i()][neighbor.j()] && dungeon.isLegalMove(current, neighbor)) {
                    // Mark neighbor as visited and enqueue
                    visited[neighbor.i()][neighbor.j()] = true;
                    queue.offer(neighbor);
                }
            }
        }
    }
    private List<Site> getNeighbors(Site site) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; //N, S, W, E, NW, NE, SW, SE
        List<Site> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int i = site.i() + dir[0];
            int j = site.j() + dir[1];
            if (i >= 0 && i < dungeon.size() && j >= 0 && j < dungeon.size()) {
                neighbors.add(new Site(i, j));
            }
        }
        return neighbors; 
    }
    public List<Site> shortestPathFromMonsterToRogue() {
        Site monsterSite = game.getMonsterSite();
        Site rogueSite = game.getRogueSite(); 

        int N = dungeon.size();
        boolean[][] visited = new boolean[N][N];
        Queue<Site> queue = new LinkedList<>();
        Map<Site, Site> parentMap = new HashMap<>();

        // Start BFS from monster's position
        queue.offer(monsterSite);
        visited[monsterSite.i()][monsterSite.j()] = true;

        while (!queue.isEmpty()) {
            Site current = queue.poll();

            // Check if rogue's position is reached
            if (current.equals(rogueSite)) {
                return reconstructPath(parentMap, monsterSite, rogueSite);
            }

            // Explore neighbors
            for (Site neighbor : getNeighbors(current)) {
                if (!visited[neighbor.i()][neighbor.j()] && dungeon.isLegalMove(current, neighbor)) {
                    visited[neighbor.i()][neighbor.j()] = true;
                    queue.offer(neighbor);
                    parentMap.put(neighbor, current); // Keep track of parent to reconstruct the path
                }
            }
        }

        // If rogue's position is unreachable
        return Collections.emptyList();
    }

    private List<Site> reconstructPath(Map<Site, Site> parentMap, Site start, Site end) {
        List<Site> path = new ArrayList<>();
        Site current = end;
        while (!current.equals(start)) {
            path.add(current);
            current = parentMap.get(current);
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }
}