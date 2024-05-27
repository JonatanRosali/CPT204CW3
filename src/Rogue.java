import java.util.*;

public class Rogue {
    private Game game;
    private Dungeon dungeon;
    private boolean isEasy;
    private int N;
    private Scanner kb = new Scanner(System.in);
    private static Map<String, int[]> directionMap = new HashMap<>();
    private Site bestEntrance = null;
    private List<Site> pathToBestEntrance = new ArrayList<>();

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

    public Rogue(Game game, boolean isEasy) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.isEasy = isEasy;
        this.N = dungeon.size();
    }

    // Return the next move for the rogue
    public Site move() {
        if (isEasy) {
            return randomMove();
        } else {
            return intelligentMove();
        }
    }

    // Choose a random legal move
    private Site randomMove() {
        Site rogueSite = game.getRogueSite();
        List<Site> neighbors = getNeighbors(rogueSite);
        List<Site> legalMoves = new ArrayList<>();
        for (Site neighbor : neighbors) {
            if (dungeon.isLegalMove(rogueSite, neighbor)) {
                legalMoves.add(neighbor);
            }
        }
        if (legalMoves.isEmpty()) {
            return rogueSite; // Stay in place if no move is possible
        }
        return legalMoves.get((int) (Math.random() * legalMoves.size()));
    }

    // Intelligent move for the rogue
    private Site intelligentMove() {
        Site rogueSite = game.getRogueSite();
        Site monsterSite = game.getMonsterSite();

        // If the rogue is in a corridor, check if there's a loop
        if (dungeon.isCorridor(rogueSite)) {
            return moveInCorridor(rogueSite, monsterSite);
        }

        // Find corridor entrances using BFS
        List<Site> corridorEntrances = findCorridorEntrances(rogueSite);
        System.out.println("Corridor Entrances: " + corridorEntrances);

        // Check for loops or cycles at each entrance using DFS
        bestEntrance = null;
        pathToBestEntrance.clear();
        int minDistanceToEntrance = Integer.MAX_VALUE;

        for (Site entrance : corridorEntrances) {
            if (hasCycle(entrance)) {
                int distanceRogueToEntrance = bfsDistance(rogueSite, entrance);
                int distanceMonsterToEntrance = bfsDistance(monsterSite, entrance);

                // Prioritize moving towards the entrance if the rogue is closer to it than the monster
                if (distanceRogueToEntrance < distanceMonsterToEntrance) {
                    if (distanceRogueToEntrance < minDistanceToEntrance) {
                        minDistanceToEntrance = distanceRogueToEntrance;
                        bestEntrance = entrance;
                        pathToBestEntrance = findPath(rogueSite, entrance);
                    }
                }
            }
        }

        // If a valid entrance with a loop is found, move towards it
        if (bestEntrance != null) {
            System.out.println("Moving towards best entrance: " + bestEntrance);
            return pathToBestEntrance.get(1); // Move to the next step along the path
        }

        // If no valid entrance is found, move to maximize distance from the monster
        return moveToMaximizeDistance(rogueSite, monsterSite);
    }

    // Move within the corridor to maximize distance from the monster
    private Site moveInCorridor(Site rogueSite, Site monsterSite) {
        // Prioritize finding loops within the corridor
        if (hasCycle(rogueSite)) {
            List<Site> neighbors = getNeighbors(rogueSite);
            Site bestMove = rogueSite;
            int maxDistance = bfsDistance(rogueSite, monsterSite);

            for (Site neighbor : neighbors) {
                if (dungeon.isLegalMove(rogueSite, neighbor)) {
                    int distance = bfsDistance(neighbor, monsterSite);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        bestMove = neighbor;
                    }
                }
            }
            return bestMove;
        }

        // If no loop is found within the current corridor, look for another corridor with a loop
        return moveToAnotherCorridor(rogueSite, monsterSite);
    }

    // Move to another corridor if no loop is found in the current corridor
    private Site moveToAnotherCorridor(Site rogueSite, Site monsterSite) {
        // Find corridor entrances
        List<Site> corridorEntrances = findCorridorEntrances(rogueSite);
        System.out.println("Corridor Entrances: " + corridorEntrances);

        // Choose the safest corridor entrance with a loop
        bestEntrance = null;
        pathToBestEntrance.clear();
        int minDistanceToEntrance = Integer.MAX_VALUE;

        for (Site entrance : corridorEntrances) {
            if (hasCycle(entrance)) {
                int distanceRogueToEntrance = bfsDistance(rogueSite, entrance);
                int distanceMonsterToEntrance = bfsDistance(monsterSite, entrance);

                // Prioritize moving towards the entrance if the rogue is closer to it than the monster
                if (distanceRogueToEntrance < distanceMonsterToEntrance) {
                    if (distanceRogueToEntrance < minDistanceToEntrance) {
                        minDistanceToEntrance = distanceRogueToEntrance;
                        bestEntrance = entrance;
                        pathToBestEntrance = findPath(rogueSite, entrance);
                    }
                }
            }
        }

        // If a valid entrance with a loop is found, move towards it
        if (bestEntrance != null) {
            System.out.println("Moving towards another corridor with loop: " + bestEntrance);
            return pathToBestEntrance.get(1); // Move to the next step along the path
        }

        // If no valid entrance is found, move to maximize distance from the monster
        return moveToMaximizeDistance(rogueSite, monsterSite);
    }

    // Find corridor entrances using BFS
    private List<Site> findCorridorEntrances(Site start) {
        Queue<Site> queue = new LinkedList<>();
        Set<Site> visited = new HashSet<>();
        List<Site> corridorEntrances = new ArrayList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Site current = queue.poll();
            for (Site neighbor : getNeighbors(current)) {
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
            List<Site> neighbors = getNeighbors(site);
            for (Site neighbor : neighbors) {
                if (dungeon.isRoom(neighbor)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check if a corridor entrance has a cycle using DFS
    private boolean hasCycle(Site entrance) {
        Set<Site> visited = new HashSet<>();
        Stack<Site> stack = new Stack<>();
        stack.push(entrance);
        visited.add(entrance);

        while (!stack.isEmpty()) {
            Site current = stack.pop();
            for (Site neighbor : getNeighbors(current)) {
                if (dungeon.isLegalMove(current, neighbor)) {
                    if (visited.contains(neighbor) && neighbor.equals(entrance)) {
                        return true; // Cycle found
                    }
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        stack.push(neighbor);
                    }
                }
            }
        }

        return false; // No cycle found
    }

    // Move to the farthest node from the monster
    private Site moveToMaximizeDistance(Site rogueSite, Site monsterSite) {
        List<Site> neighbors = getNeighbors(rogueSite);
        Site bestMove = rogueSite;
        int maxDistance = bfsDistance(rogueSite, monsterSite);

        for (Site neighbor : neighbors) {
            if (dungeon.isLegalMove(rogueSite, neighbor)) {
                int distance = bfsDistance(neighbor, monsterSite);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    bestMove = neighbor;
                }
            }
        }

        System.out.println("Move to maximize distance: " + bestMove);
        return bestMove;
    }

    // Get neighbors for a site
    private List<Site> getNeighbors(Site site) {
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1} }; // N, S, W, E, NW, NE, SW, SE
        List<Site> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int i = site.i() + dir[0];
            int j = site.j() + dir[1];
            Site neighbor = new Site(i, j);
            if (i >= 0 && i < N && j >= 0 && j < N && !dungeon.isWall(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    // BFS to calculate distance from a given site to the monster
    private int bfsDistance(Site start, Site goal) {
        Queue<Site> queue = new LinkedList<>();
        Set<Site> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        int distance = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Site current = queue.poll();
                if (current.equals(goal)) {
                    return distance;
                }
                for (Site neighbor : getNeighbors(current)) {
                    if (!visited.contains(neighbor) && dungeon.isLegalMove(current, neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
            distance++;
        }
        return distance;
    }

    // Find path from start to goal using BFS
    private List<Site> findPath(Site start, Site goal) {
        Queue<Site> queue = new LinkedList<>();
        Map<Site, Site> cameFrom = new HashMap<>();
        Set<Site> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            Site current = queue.poll();
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, start, current);
            }
            for (Site neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor) && dungeon.isLegalMove(current, neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }
        return new ArrayList<>(); // No path found
    }

    // Reconstruct path from the cameFrom map
    private List<Site> reconstructPath(Map<Site, Site> cameFrom, Site start, Site end) {
        List<Site> path = new ArrayList<>();
        Site current = end;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(start);
        Collections.reverse(path);
        System.out.println("Reconstructed path: " + path);
        return path;
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

        Site currentSite = game.getRogueSite();
        Site userMove = new Site(currentSite.i() + directions[0], currentSite.j() + directions[1]);
        if (dungeon.isLegalMove(currentSite, userMove)) {
            return userMove;
        }
        System.out.println("Oops! You Hit A Wall");
        return currentSite;
    }
}
