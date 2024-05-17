import java.util.ArrayList;
import java.util.List;

public class DFS {
    private Dungeon dungeon;
    private Game game;

    public DFS(Dungeon dungeon, Game game) {
        this.dungeon = dungeon;
        this.game = game;
    }

    // Method to check if there's a cycle in the corridor graph
    public boolean canFindCorridorCycle() {
        Site rogueSite = game.getRogueSite();
        boolean[][] visited = new boolean[dungeon.size()][dungeon.size()];
        return dfsCycle(rogueSite, rogueSite, visited, 0);
    }

    private boolean dfsCycle(Site current, Site parent, boolean[][] visited, int depth) {
        visited[current.i()][current.j()] = true;

        for (Site neighbor : getNeighbors(current)) {
            if (dungeon.isCorridor(neighbor)) {
                if (!visited[neighbor.i()][neighbor.j()]) {
                    if (dfsCycle(neighbor, current, visited, depth + 1)) {
                        return true;
                    }
                } else if (!neighbor.equals(parent) && depth > 1) {
                    System.out.println("Corridor cycle detected at: (" + neighbor.i() + ", " + neighbor.j() + ")");
                    return true; // Found a cycle
                }
            }
        }
        visited[current.i()][current.j()] = false;
        return false;
    }

    // Method to find the farthest path from rogue to monster
    public List<Site> farthestPathFromRogueToMonster() {
        Site rogueSite = game.getRogueSite();
        Site monsterSite = game.getMonsterSite();
        boolean[][] visited = new boolean[dungeon.size()][dungeon.size()];
        List<Site> path = new ArrayList<>();
        List<Site> longestPath = new ArrayList<>();
        dfsLongestPath(rogueSite, monsterSite, visited, path, longestPath);
        return longestPath;
    }

    private void dfsLongestPath(Site current, Site target, boolean[][] visited, List<Site> path, List<Site> longestPath) {
        visited[current.i()][current.j()] = true;
        path.add(current);

        if (current.equals(target)) {
            if (path.size() > longestPath.size()) {
                longestPath.clear();
                longestPath.addAll(new ArrayList<>(path));
            }
        } else {
            for (Site neighbor : getNeighbors(current)) {
                if (!visited[neighbor.i()][neighbor.j()] && dungeon.isLegalMove(current, neighbor)) {
                    dfsLongestPath(neighbor, target, visited, path, longestPath);
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

    // Method to find the longest path within a cycle
    public void dfsLongestPathInCycle(Site current, Site start, boolean[][] visited, List<Site> path, List<Site> longestPath) {
        visited[current.i()][current.j()] = true;
        path.add(current);

        if (current.equals(start) && path.size() > 1) {
            if (path.size() > longestPath.size()) {
                longestPath.clear();
                longestPath.addAll(new ArrayList<>(path));
            }
        } else {
            for (Site neighbor : getNeighbors(current)) {
                if (!visited[neighbor.i()][neighbor.j()] && dungeon.isLegalMove(current, neighbor)) {
                    dfsLongestPathInCycle(neighbor, start, visited, path, longestPath);
                }
            }
        }

        path.remove(path.size() - 1);
        visited[current.i()][current.j()] = false;
    }
}
