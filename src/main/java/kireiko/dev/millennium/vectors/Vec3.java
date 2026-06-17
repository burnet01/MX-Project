package kireiko.dev.millennium.vectors;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class Vec3 {
    public double xCoord, yCoord, zCoord;

    public Vec3(double x, double y, double z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public Vec3(Vec vector) {
        this.xCoord = vector.x();
        this.yCoord = vector.y();
        this.zCoord = vector.z();
    }

    public Vec3(Pos pos) {
        this.xCoord = pos.x();
        this.yCoord = pos.y();
        this.zCoord = pos.z();
    }

    public double getX() { return xCoord; }
    public double getY() { return yCoord; }
    public double getZ() { return zCoord; }

    public void setX(double x) { this.xCoord = x; }
    public void setY(double y) { this.yCoord = y; }
    public void setZ(double z) { this.zCoord = z; }

    public Vec toVector() {
        return new Vec(xCoord, yCoord, zCoord);
    }

    public Vec3 add(double x, double y, double z) {
        return new Vec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(this.xCoord + other.xCoord, this.yCoord + other.yCoord, this.zCoord + other.zCoord);
    }

    public Vec3 subtract(double x, double y, double z) {
        return new Vec3(this.xCoord - x, this.yCoord - y, this.zCoord - z);
    }

    public Vec3 subtract(Vec3 other) {
        return new Vec3(this.xCoord - other.xCoord, this.yCoord - other.yCoord, this.zCoord - other.zCoord);
    }

    public Vec3 multiply(double scalar) {
        return new Vec3(this.xCoord * scalar, this.yCoord * scalar, this.zCoord * scalar);
    }

    public double length() {
        return Math.sqrt(xCoord * xCoord + yCoord * yCoord + zCoord * zCoord);
    }

    public double distance(Vec3 other) {
        double dx = this.xCoord - other.xCoord;
        double dy = this.yCoord - other.yCoord;
        double dz = this.zCoord - other.zCoord;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public Vec3 normalize() {
        double len = length();
        if (len == 0) return new Vec3(0, 0, 0);
        return new Vec3(xCoord / len, yCoord / len, zCoord / len);
    }

    public double squareDistanceTo(Vec3 other) {
        double dx = this.xCoord - other.xCoord;
        double dy = this.yCoord - other.yCoord;
        double dz = this.zCoord - other.zCoord;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vec3 getIntermediateWithXValue(Vec3 other, double x) {
        double d = other.xCoord - this.xCoord;
        if (d * d < 1.0E-7D) return null;
        double f = (x - this.xCoord) / d;
        return f < 0.0D || f > 1.0D ? null :
                new Vec3(this.xCoord + (other.xCoord - this.xCoord) * f,
                        this.yCoord + (other.yCoord - this.yCoord) * f,
                        this.zCoord + (other.zCoord - this.zCoord) * f);
    }

    public Vec3 getIntermediateWithYValue(Vec3 other, double y) {
        double d = other.yCoord - this.yCoord;
        if (d * d < 1.0E-7D) return null;
        double f = (y - this.yCoord) / d;
        return f < 0.0D || f > 1.0D ? null :
                new Vec3(this.xCoord + (other.xCoord - this.xCoord) * f,
                        this.yCoord + (other.yCoord - this.yCoord) * f,
                        this.zCoord + (other.zCoord - this.zCoord) * f);
    }

    public Vec3 getIntermediateWithZValue(Vec3 other, double z) {
        double d = other.zCoord - this.zCoord;
        if (d * d < 1.0E-7D) return null;
        double f = (z - this.zCoord) / d;
        return f < 0.0D || f > 1.0D ? null :
                new Vec3(this.xCoord + (other.xCoord - this.xCoord) * f,
                        this.yCoord + (other.yCoord - this.yCoord) * f,
                        this.zCoord + (other.zCoord - this.zCoord) * f);
    }
}
