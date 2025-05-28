package knu.lsy.shapes;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

public class RegularPolygon extends Shape {
    private int sides;
    private double rotationAngle;
    private List<Point> vertices;

    public RegularPolygon(Point center, double radius, int sides, double rotationAngle) {
        super(center, radius);
        this.sides = sides;
        this.rotationAngle = rotationAngle;
        this.vertices = generateVertices();
    }

    private List<Point> generateVertices() {
        List<Point> points = new ArrayList<>();
        double angleStep = 2 * Math.PI / sides;

        for (int i = 0; i < sides; i++) {
            double angle = angleStep * i + rotationAngle;
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            points.add(new Point(x, y));
        }

        return points;
    }

    @Override
    public boolean overlaps(Shape other) {
        List<Point> thisPoly = this.getVertices();
        List<Point> otherPoly = other.getVertices();
        return polygonsOverlapSAT(thisPoly, otherPoly);
    }

    private boolean polygonsOverlapSAT(List<Point> poly1, List<Point> poly2) {
        List<Point> allEdges = new ArrayList<>();
        allEdges.addAll(poly1);
        allEdges.addAll(poly2);

        for (int i = 0; i < allEdges.size(); i++) {
            Point p1 = allEdges.get(i);
            Point p2 = allEdges.get((i + 1) % allEdges.size());
            double dx = p2.getX() - p1.getX();
            double dy = p2.getY() - p1.getY();

            // 법선 벡터 구하기 (dy, -dx)
            double nx = dy;
            double ny = -dx;

            double[] projections1 = projectPolygon(poly1, nx, ny);
            double[] projections2 = projectPolygon(poly2, nx, ny);

            // Projection이 겹치지 않으면 분리축 존재
            if (projections1[1] < projections2[0] || projections2[1] < projections1[0]) {
                return false; // 겹치지 않음
            }
        }
        return true; // 모든 축에서 겹침 → 충돌
    }

    private double[] projectPolygon(List<Point> poly, double ax, double ay) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Point p : poly) {
            double dot = p.getX() * ax + p.getY() * ay;
            min = Math.min(min, dot);
            max = Math.max(max, dot);
        }
        return new double[]{min, max};
    }


    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", "regularPolygon");
        json.put("id", id);
        json.put("center", center.toJSON());
        json.put("radius", radius);
        json.put("sides", sides);
        json.put("rotationAngle", rotationAngle);
        json.put("color", color);

        JSONArray verticesArray = new JSONArray();
        for (Point vertex : vertices) {
            verticesArray.put(vertex.toJSON());
        }
        json.put("vertices", verticesArray);

        return json;
    }

    @Override
    public String getShapeType() {
        return "regularPolygon";
    }

    @Override
    public List<Point> getVertices() {
        return new ArrayList<>(vertices);
    }
}