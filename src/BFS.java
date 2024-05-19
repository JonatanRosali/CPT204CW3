import java.util.*;

public class BFS {
    private Dungeon dungeon;

    public BFS(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    // Finds the shortest path from start to end using BFS
    public List<Site> findShortestPath(Site start, Site end) {
        int N = dungeon.size();
        boolean[][] visited = new boolean[N][N];
        Queue<Site> queue = new LinkedList<>();
        Map<Site, Site> parentMap = new HashMap<>();

        queue.offer(start);
        visited[start.i()][start.j()] = true;

        while (!queue.isEmpty()) {
            Site current = queue.poll();

            // Check if end position is reached
            if (current.equals(end)) {
                return reconstructPath(parentMap, start, end);
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

        return Collections.emptyList(); // Return an empty path if no path found
    }

    private List<Site> getNeighbors(Site site) {
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1} }; // N, S, W, E, NW, NE, SW, SE
        List<Site> neighbors = new ArrayList<>();
        int N = dungeon.size();
        for (int[] dir : directions) {
            int i = site.i() + dir[0];
            int j = site.j() + dir[1];
            if (i >= 0 && i < N && j >= 0 && j < N && !dungeon.isWall(new Site(i, j))) {
                neighbors.add(new Site(i, j));
            }
        }
        return neighbors;
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
