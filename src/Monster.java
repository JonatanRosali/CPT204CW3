import java.util.List;

public class Monster {
    private Game game;
    private Dungeon dungeon;
    private BFS bfs;

    public Monster(Game game) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.bfs = new BFS(dungeon);
    }

    // Take a step towards the rogue using BFS to find the shortest path
    public Site move() {
        List<Site> path = bfs.findShortestPath(game.getMonsterSite(), game.getRogueSite());
        if (path != null && path.size() > 1) {
            return path.get(1); // Move to the next step in the path
        }
        return game.getMonsterSite(); // Stay in the same place if no path found
    }
}
