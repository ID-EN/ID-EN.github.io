package knu.lsy.shapes;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

public class Circle extends Shape {

    public Circle(Point center, double radius) {
        super(center, radius);
    }

    @Override
    public boolean overlaps(Shape other) {
        if (other instanceof Circle) {
            Circle c = (Circle) other;
            double dx = this.center.getX() - c.center.getX();
            double dy = this.center.getY() - c.center.getY();
            double distanceSquared = dx * dx + dy * dy;
            double radiusSum = this.radius + c.radius;
            return distanceSquared < radiusSum * radiusSum;
        } else {
            // 다른 도형의 모든 변이 원과 교차하거나,
            // 다각형 정점 중 하나라도 원 안에 포함되면 겹침
            List<Point> vertices = other.getVertices();
            for (Point vertex : vertices) {
                double dx = this.center.getX() - vertex.getX();
                double dy = this.center.getY() - vertex.getY();
                if (dx * dx + dy * dy <= this.radius * this.radius) {
                    return true;
                }
            }

            // 다각형의 각 변과 원의 교차 검사
            List<Point> poly = other.getVertices();
            int n = poly.size();
            for (int i = 0; i < n; i++) {
                Point a = poly.get(i);
                Point b = poly.get((i + 1) % n);
                if (circleIntersectsSegment(center, radius, a, b)) {
                    return true;
                }
            }

            return false;
        }
    }

    // 선분과 원의 교차 여부 확인
    private boolean circleIntersectsSegment(Point center, double radius, Point a, Point b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double fx = a.getX() - center.getX();
        double fy = a.getY() - center.getY();

        double aVal = dx * dx + dy * dy;
        double bVal = 2 * (fx * dx + fy * dy);
        double cVal = fx * fx + fy * fy - radius * radius;

        double discriminant = bVal * bVal - 4 * aVal * cVal;

        if (discriminant < 0) {
            return false;
        }

        discriminant = Math.sqrt(discriminant);
        double t1 = (-bVal - discriminant) / (2 * aVal);
        double t2 = (-bVal + discriminant) / (2 * aVal);

        return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1);
    }


    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", "circle");
        json.put("id", id);
        json.put("center", center.toJSON());
        json.put("radius", radius);
        json.put("color", color);
        return json;
    }

    @Override
    public String getShapeType() {
        return "circle";
    }

    @Override
    public List<Point> getVertices() {
        // 원의 경계를 근사하는 점들 생성
        List<Point> vertices = new ArrayList<>();
        int numPoints = 32;
        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            vertices.add(new Point(x, y));
        }
        return vertices;
    }
}