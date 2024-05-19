import java.util.ArrayList;
import java.util.List;

public class DFS {
    private Dungeon dungeon;
    private Game game;

    public DFS(Dungeon dungeon, Game game) {
        this.dungeon = dungeon;
        this.game = game;
    }

    // Method to check if there's a cycle in the corridor graph starting from a given site
    public boolean canFindCorridorCycle(Site start) {
        boolean[][] visited = new boolean[dungeon.size()][dungeon.size()];
        return dfsCycle(start, null, visited);
    }

    private boolean dfsCycle(Site current, Site parent, boolean[][] visited) {
        visited[current.i()][current.j()] = true;

        for (Site neighbor : getNeighbors(current)) {
            if (dungeon.isCorridor(neighbor)) {
                if (!visited[neighbor.i()][neighbor.j()]) {
                    if (dfsCycle(neighbor, current, visited)) {
                        System.out.println("Cycle detected at: " + neighbor);
                        return true;
                    }
                } else if (!neighbor.equals(parent)) {
                    System.out.println("Cycle detected at: " + neighbor);
                    return true; // Found a cycle
                }
            }
        }
        return false;
    }

    // Method to find the longest path within a cycle
    public List<Site> farthestPathInCycle(Site start) {
        boolean[][] visited = new boolean[dungeon.size()][dungeon.size()];
        List<Site> path = new ArrayList<>();
        List<Site> longestPath = new ArrayList<>();
        dfsLongestPathInCycle(start, start, visited, path, longestPath);
        return longestPath;
    }

    private void dfsLongestPathInCycle(Site current, Site start, boolean[][] visited, List<Site> path, List<Site> longestPath) {
        visited[current.i()][current.j()] = true;
        path.add(current);

        if (current.equals(start) && path.size() > 1) {
            if (path.size() > longestPath.size()) {
                longestPath.clear();
                longestPath.addAll(new ArrayList<>(path));
            }
        } else {
            for (Site neighbor : getNeighbors(current)) {
                if (!visited[neighbor.i()][neighbor.j()] && dungeon.isCorridor(neighbor)) {
                    dfsLongestPathInCycle(neighbor, start, visited, path, longestPath);
                }
            }
        }

        path.remove(path.size() - 1);
        visited[current.i()][current.j()] = false;
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
