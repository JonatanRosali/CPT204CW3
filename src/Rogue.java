import java.util.*;

public class Rogue {
    private Game game;
    private Dungeon dungeon;
    private int N;

    public Rogue(Game game) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.N = dungeon.size();
    }

    // Return the next move for the rogue
    public Site move() {
        Site rogue = game.getRogueSite();
        Site monster = game.getMonsterSite();

        // Prioritize moving to a corridor if not already on one
        if (!dungeon.isCorridor(rogue)) {
            List<Site> pathToCorridor = bfsToCorridor(rogue, monster);
            if (pathToCorridor != null && pathToCorridor.size() > 1) {
                Site nextMove = pathToCorridor.get(1);
                if (dungeon.isLegalMove(rogue, nextMove)) {
                    return nextMove;
                }
            }
        }

        // If already on a corridor or no corridor found, move within the corridors or rooms
        return moveWithinCorridorOrRoom(rogue, monster);
    }

    // BFS to find the shortest path to the nearest corridor
    private List<Site> bfsToCorridor(Site start, Site monster) {
        boolean[][] visited = new boolean[N][N];
        Queue<Site> queue = new LinkedList<>();
        Map<Site, Site> parentMap = new HashMap<>();

        queue.offer(start);
        visited[start.i()][start.j()] = true;

        List<Site> bestPath = null;
        int maxDistance = -1;

        while (!queue.isEmpty()) {
            Site current = queue.poll();

            // Check if this site is a corridor
            if (dungeon.isCorridor(current)) {
                int distanceToMonster = current.manhattanTo(monster);
                if (distanceToMonster > maxDistance) {
                    maxDistance = distanceToMonster;
                    bestPath = reconstructPath(parentMap, start, current);
                }
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

        return bestPath; // Return the best path found to a different corridor entrance
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

    // Move within the corridor or room while keeping away from the monster
    private Site moveWithinCorridorOrRoom(Site rogue, Site monster) {
        List<Site> neighbors = getNeighbors(rogue);
        Site bestMove = null;
        int maxDistance = -1;

        for (Site neighbor : neighbors) {
            if (dungeon.isLegalMove(rogue, neighbor)) {
                int distance = neighbor.manhattanTo(monster);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    bestMove = neighbor;
                }
            }
        }

        return bestMove != null ? bestMove : randomMove(rogue);
    }

    // Choose a random legal move
    private Site randomMove(Site rogue) {
        List<Site> neighbors = getNeighbors(rogue);
        List<Site> legalMoves = new ArrayList<>();
        for (Site neighbor : neighbors) {
            if (dungeon.isLegalMove(rogue, neighbor)) {
                legalMoves.add(neighbor);
            }
        }
        if (legalMoves.isEmpty()) {
            return rogue; // Stay in place if no move is possible
        }
        return legalMoves.get((int) (Math.random() * legalMoves.size()));
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
