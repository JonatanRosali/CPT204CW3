import java.util.*;

public class Site {
    private int i;
    private int j;

    // initialize board from file
    public Site(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int i() { return i; }
    public int j() { return j; }

    // Manhattan distance between invoking Site and w
    public int manhattanTo(Site w) {
        Site v = this;
        int i1 = v.i();
        int j1 = v.j();
        int i2 = w.i();
        int j2 = w.j();
        return Math.abs(i1 - i2) + Math.abs(j1 - j2);
    }

    // Overriding equals object to compare the object itself not the reference -Jo
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Site site = (Site) o;
        return Objects.equals(i, site.i) && Objects.equals(j, site.j);
    }

    // Override hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

    @Override
    public String toString(){
        return "Site(" + i + ", " + j + ")";
    }

    public List<Site> getNeighbors(Dungeon dungeon) {
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, 
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        }; // N, S, W, E, NW, NE, SW, SE
        List<Site> neighbors = new ArrayList<>();
        int N = dungeon.size();
        for (int[] dir : directions) {
            int i = this.i + dir[0];
            int j = this.j + dir[1];
            if (i >= 0 && i < N && j >= 0 && j < N && !dungeon.isWall(new Site(i, j))) {
                neighbors.add(new Site(i, j));
            }
        }
        return neighbors;
    }
}
