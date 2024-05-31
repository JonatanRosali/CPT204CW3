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
            for (Site neighbor : current.getNeighbors(dungeon)) {
                if (!visited[neighbor.i()][neighbor.j()] && dungeon.isLegalMove(current, neighbor)) {
                    visited[neighbor.i()][neighbor.j()] = true;
                    queue.offer(neighbor);
                    parentMap.put(neighbor, current); // Keep track of parent to reconstruct the path
                }
            }
        }

        return Collections.emptyList(); // Return an empty path if no path found
    }

    // Reconstruct the path from parent map
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

    // Finds a path and ensures safety criteria
    public List<Site> findSafePath(Site start, Site end, Site monsterSite) {
        Map<Site, Site> cameFrom = new HashMap<>();
        Queue<Site> queue = new LinkedList<>();
        Set<Site> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Site current = queue.poll();
            for (Site neighbor : current.getNeighbors(dungeon)) {
                if (!visited.contains(neighbor) && dungeon.isLegalMove(current, neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                    if (neighbor.equals(end)) {
                        List<Site> path = reconstructSafePath(cameFrom, start, end, monsterSite);
                        if (!path.isEmpty()) {
                            return path;
                        }
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Site> reconstructSafePath(Map<Site, Site> cameFrom, Site start, Site end, Site monsterSite) {
        List<Site> path = new ArrayList<>();
        Site current = end;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(start);
        Collections.reverse(path);

        int initialDistanceToMonster = start.manhattanTo(monsterSite);

        for (int i = 0; i < path.size(); i++) {
            Site site = path.get(i);
            int distanceToMonster = site.manhattanTo(monsterSite);
            int threshold;

            if (dungeon.isCorridor(site) || (i < path.size() - 1 && dungeon.isCorridor(path.get(i + 1)))) {
                if (distanceToMonster < initialDistanceToMonster && distanceToMonster < 2) {
                    return new ArrayList<>();
                }
                threshold = 1;
            } else if (initialDistanceToMonster >= 2) {
                threshold = 2;
            } else {
                threshold = 1;
            }

            if (distanceToMonster < threshold) {
                return new ArrayList<>();
            }
        }

        return path;
    }

    // Finds corridor entrances using BFS
    public List<Site> findCorridorEntrances(Site start) {
        Queue<Site> queue = new LinkedList<>();
        Set<Site> visited = new HashSet<>();
        List<Site> corridorEntrances = new ArrayList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Site current = queue.poll();
            for (Site neighbor : current.getNeighbors(dungeon)) {
                if (!visited.contains(neighbor) && dungeon.isLegalMove(current, neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    if (isCorridorEntrance(neighbor)) {
                        corridorEntrances.add(neighbor);
                    }
                }
            }
        }

        return corridorEntrances;
    }

    // Check if a site is a corridor entrance
    private boolean isCorridorEntrance(Site site) {
        if (dungeon.isCorridor(site)) {
            List<Site> neighbors = site.getNeighbors(dungeon);
            for (Site neighbor : neighbors) {
                if (dungeon.isRoom(neighbor)) {
                    return true;
                }
            }
        }
        return false;
    }
}
