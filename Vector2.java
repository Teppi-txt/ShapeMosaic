public class Vector2 {
    int x; 
    int y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void print() {
        System.out.print("(" + this.x + ", " + this.y + ")");
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public void normalise() {
        //get length of vector, multiply components by 1/length
        double length = this.length();
        this.x = (int) (this.x / length);
        this.y = (int) (this.y / length);
    }

    public int dot(Vector2 v2) {
        return (int) (this.x * v2.x +  this.y * v2.y);
    }

    public Vector2 copy() {
        return new Vector2(this.x, this.y);
    }

    public void increment_x(int x) {
        this.x += x;
    }

    public void increment_y(int y) {
        this.y += y;
    }

    public void add(Vector2 v2) {
        this.x += v2.x;
        this.y += v2.y;
    }
    
    public Vector2 added(Vector2 v2) {
        return new Vector2(this.x + v2.x, this.y + v2.y);
    }

    public Vector2 subtracted(Vector2 v2) {
        return new Vector2(this.x - v2.x, this.y - v2.y);
    }

    public void floor(Vector2 v2) {
        this.x = Math.max(this.x, v2.x);
        this.y = Math.max(this.y, v2.y);
    }

    public void ceil(Vector2 v2) {
        this.x = Math.min(this.x, v2.x);
        this.y = Math.min(this.y, v2.y);
    }
}