package abyssalmc.clutch;

import java.util.ArrayList;
import java.util.List;

public class LineUtils {
    public static List<int[]> getLine(int x1, int y1, int x2, int y2) {
        List<int[]> line = new ArrayList<>();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            line.add(new int[]{x1, y1});

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }

        return line;
    }

    public static List<int[]> getBezierCurve(int x1, int y1, int x2, int y2, int x3, int y3) {
        List<int[]> bezierCoords = new ArrayList<>();

        // Number of steps for smoothness (higher = smoother)
        int steps = 100;

        for (int i = 0; i <= steps; i++) {
            // Compute t in range [0, 1]
            double t = i / (double) steps;

            // Quadratic Bézier curve formula
            double x = (1 - t) * (1 - t) * x1 + 2 * (1 - t) * t * x2 + t * t * x3;
            double y = (1 - t) * (1 - t) * y1 + 2 * (1 - t) * t * y2 + t * t * y3;

            // Add rounded coordinates to the list
            bezierCoords.add(new int[]{(int) Math.round(x), (int) Math.round(y)});
        }

        return bezierCoords;
    }

}