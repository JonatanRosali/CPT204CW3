import java.util.*;

public class DFS {
    private Dungeon dungeon;

    public DFS(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    // Method to check for loops using DFS, restricted to corridors
    public boolean hasCycleInCorridor(Site entrance) {
        Set<Site> visited = new HashSet<>();
        Stack<Site> stack = new Stack<>();

        // Start by adding corridor neighbors of the entrance to the stack
        for (Site neighbor : entrance.getNeighbors(dungeon)) {
            if (dungeon.isCorridor(neighbor)) {
                stack.push(neighbor);
                visited.add(neighbor);
                break; // Only need to start with one corridor neighbor
            }
        }

        while (!stack.isEmpty()) {
            Site current = stack.pop();
            for (Site neighbor : current.getNeighbors(dungeon)) {
                if (dungeon.isCorridor(neighbor) && dungeon.isLegalMove(current, neighbor)) {
                    if (neighbor.equals(entrance) && visited.contains(neighbor)) {
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

    // Method to check for loops using DFS, including rooms
    public boolean hasCycleIncludingRooms(Site entrance) {
        Set<Site> visited = new HashSet<>();
        Stack<Site> stack = new Stack<>();

        // Add only the corridor neighbors of the entrance to the stack
        for (Site neighbor : entrance.getNeighbors(dungeon)) {
            if (dungeon.isCorridor(neighbor)) {
                stack.push(neighbor);
                visited.add(neighbor);
            }
        }

        while (!stack.isEmpty()) {
            Site current = stack.pop();
            for (Site neighbor : current.getNeighbors(dungeon)) {
                // Only traverse corridors and rooms, avoid revisiting the entrance directly from rooms
                if ((dungeon.isCorridor(neighbor) || dungeon.isRoom(neighbor)) && dungeon.isLegalMove(current, neighbor)) {
                    // Ensure we do not traverse directly from the entrance to a room
                    if (current.equals(entrance) && dungeon.isRoom(neighbor)) {
                        continue;
                    }
                    if (neighbor.equals(entrance) && visited.contains(neighbor)) {
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
}
