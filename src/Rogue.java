import java.util.*;

public class Rogue extends Characters implements Movable{
    private DFS dfs;
    private BFS bfs;

    public Rogue(Game game, boolean isEasy) {
        super(game, isEasy);
        this.dfs = new DFS(dungeon);
        this.bfs = new BFS(dungeon);
    }

    @Override
    public Site move() {
        if (isEasy) {
            return randomMove();
        } else {
            return intelligentMove();
        }
    }

    @Override
    protected Site getCurrentSite() {
        return game.getRogueSite();
    }

    private Site intelligentMove() {
        Site rogueSite = getCurrentSite();
        Site monsterSite = game.getMonsterSite();

        System.out.println("Rogue at: " + rogueSite + ", Monster at: " + monsterSite);

        if (dungeon.isCorridor(rogueSite)) {
            List<Site> corridorEntrances = bfs.findCorridorEntrances(rogueSite);

            for (Site entrance : corridorEntrances) {
                boolean hasCycleInCorridor = dfs.hasCycleInCorridor(entrance);
                boolean hasCycleIncludingRooms = dfs.hasCycleIncludingRooms(entrance);

                System.out.println("Checking entrance: " + entrance + ", hasCycleInCorridor: " + hasCycleInCorridor + ", hasCycleIncludingRooms: " + hasCycleIncludingRooms);

                if (hasCycleInCorridor || hasCycleIncludingRooms) {
                    List<Site> pathToCycle = bfs.findSafePath(rogueSite, entrance, monsterSite);
                    if (!pathToCycle.isEmpty() && isSafeEntrance(entrance, monsterSite, rogueSite)) {
                        System.out.println("Safe path found to entrance: " + entrance + ", path: " + pathToCycle);
                        return pathToCycle.get(1);
                    }
                }
            }

            System.out.println("No safe entrance found in corridor, running away.");
            return runAwayFromMonster(rogueSite, monsterSite);
        }

        if (dungeon.isRoom(rogueSite)) {
            List<Site> corridorEntrances = bfs.findCorridorEntrances(rogueSite);

            for (Site entrance : corridorEntrances) {
                boolean hasCycleInCorridor = dfs.hasCycleInCorridor(entrance);
                boolean hasCycleIncludingRooms = dfs.hasCycleIncludingRooms(entrance);

                System.out.println("Checking entrance: " + entrance + ", hasCycleInCorridor: " + hasCycleInCorridor + ", hasCycleIncludingRooms: " + hasCycleIncludingRooms);

                if (hasCycleInCorridor || hasCycleIncludingRooms) {
                    List<Site> pathToCycle = bfs.findSafePath(rogueSite, entrance, monsterSite);
                    if (!pathToCycle.isEmpty() && isSafeEntrance(entrance, monsterSite, rogueSite)) {
                        System.out.println("Safe path found to entrance: " + entrance + ", path: " + pathToCycle);
                        return pathToCycle.get(1);
                    }
                }
            }

            Site nearestEntrance = findNearestSafeCorridorEntrance(rogueSite, corridorEntrances, monsterSite);
            if (nearestEntrance != null && isSafeEntrance(nearestEntrance, monsterSite, rogueSite)) {
                List<Site> pathToNearestEntrance = bfs.findSafePath(rogueSite, nearestEntrance, monsterSite);
                if (!pathToNearestEntrance.isEmpty()) {
                    System.out.println("Nearest safe entrance found: " + nearestEntrance + ", path: " + pathToNearestEntrance);
                    return pathToNearestEntrance.get(1);
                }
            }

            System.out.println("No safe entrance found in room, running away.");
            return runAwayFromMonster(rogueSite, monsterSite);
        }

        return rogueSite;
    }

    private Site runAwayFromMonster(Site rogueSite, Site monsterSite) {
        List<Site> neighbors = getNeighbors(rogueSite);
        Site bestMove = rogueSite;
        int maxDistance = rogueSite.manhattanTo(monsterSite);

        for (Site neighbor : neighbors) {
            if (dungeon.isLegalMove(rogueSite, neighbor)) {
                int distance = neighbor.manhattanTo(monsterSite);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    bestMove = neighbor;
                }
            }
        }

        System.out.println("Running away from monster, best move: " + bestMove);
        return bestMove;
    }

    private Site findNearestSafeCorridorEntrance(Site start, List<Site> corridorEntrances, Site monsterSite) {
        int minDistance = Integer.MAX_VALUE;
        Site nearestEntrance = null;

        for (Site entrance : corridorEntrances) {
            int distance = start.manhattanTo(entrance);
            List<Site> path = bfs.findSafePath(start, entrance, monsterSite);
            if (distance < minDistance && !path.isEmpty()) {
                minDistance = distance;
                nearestEntrance = entrance;
            }
        }

        return nearestEntrance;
    }

    private boolean isSafeEntrance(Site entrance, Site monsterSite, Site rogueSite) {
        int monsterDistance = monsterSite.manhattanTo(entrance);
        int rogueDistance = rogueSite.manhattanTo(entrance);
        return monsterDistance >= rogueDistance;
    }
}
