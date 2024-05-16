import java.util.Objects;

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
        return i == site.i && j == site.j;
    }

    // Override hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

}
