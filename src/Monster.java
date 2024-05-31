import java.util.*;

public class Monster extends Characters implements Movable{
    private BFS bfs;

    public Monster(Game game, boolean isEasy) {
        super(game, isEasy);
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
        return game.getMonsterSite();
    }

    private Site intelligentMove() {
        Site monsterSite = getCurrentSite();
        Site rogueSite = game.getRogueSite();

        List<Site> monsterNeighbors = getNeighbors(monsterSite);

        Site bestNeighbor = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Site neighbor : monsterNeighbors) {
            int distance = neighbor.manhattanTo(rogueSite);
            if (dungeon.isLegalMove(monsterSite, neighbor) && distance < bestDistance) {
                bestDistance = distance;
                bestNeighbor = neighbor;
            }
        }

        List<Site> bfsPath = bfs.findShortestPath(monsterSite, rogueSite);

        int bfsDistance = Integer.MAX_VALUE;
        if (bfsPath != null && bfsPath.size() > 1) {
            Site bfsNextStep = bfsPath.get(1);
            bfsDistance = bfsNextStep.manhattanTo(rogueSite);
        }

        if (bestNeighbor != null && bestDistance < bfsDistance) {
            return bestNeighbor;
        } else if (bfsPath != null && bfsPath.size() > 1) {
            return bfsPath.get(1);
        }

        return monsterSite;
    }
}
