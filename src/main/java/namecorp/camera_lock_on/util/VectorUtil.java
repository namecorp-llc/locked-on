package namecorp.camera_lock_on.util;

import net.minecraft.util.math.Vec3d;

public class VectorUtil {
    private static final Vec3d lineX = new Vec3d(1, 0, 0);
    private static final Vec3d lineY = new Vec3d(0, 1, 0);
    private static final Vec3d lineZ = new Vec3d(0, 0, 1);
    private static final Vec3d planeXZ = new Vec3d(1, 0, 1);
    private static final Vec3d planeYZ = new Vec3d(0, 1, 1);
    private static final Vec3d planeXY = new Vec3d(1, 1, 0);

    /*
     * Calculate the angle of point A of a triangle formed by points A, B, and C.
     * Here, vectors are treated as 3D points in space
     * @param a The point to calculate the angle.
     * @param b The second point.
     * @param c The third point.
     * @return The angle in degrees.
    */
    public static double angle(Vec3d a, Vec3d b, Vec3d c) {
        double bc2 = b.squaredDistanceTo(c);
        double ab2 = b.squaredDistanceTo(a);
        double ac2 = c.squaredDistanceTo(a);
        double cosCamera = (ab2 + ac2 - bc2) / (2 * Math.sqrt(ab2) * Math.sqrt(ac2));
        double cameraAngle = Math.acos(cosCamera);
        return Math.toDegrees(cameraAngle);
    }

    /*
     * Calculate the angle of point A of a triangle formed by points A, B, and C.
     * Here, vectors are treated as 3D points in space, and you also flatten them
     * using the plane vector.
     * @param a The point to calculate the angle.
     * @param b The second point.
     * @param c The third point.
     * @param dimension The dimension vector to flatten the points.
     * @return The angle in degrees.
    */
    private static double flatAngle(Vec3d a, Vec3d b, Vec3d c, Vec3d plane) {
        return angle(a.multiply(plane), b.multiply(plane), c.multiply(plane));
    }

    /*
     * Calculate the angle of point A of a triangle formed by points A, B, and C.
     * Here, vectors are treated as 3D points in space, and you also flatten them
     * using the XZ plane.
     * @param a The point to calculate the angle.
     * @param b The second point.
     * @param c The third point.
     * @return The angle in degrees.
    */
    public static double angleXZ(Vec3d a, Vec3d b, Vec3d c) {
        return flatAngle(a, b, c, planeXZ);
    }

    /*
     * Calculate the angle of point A of a triangle formed by points A, B, and C.
     * Here, vectors are treated as 3D points in space, and you also flatten them
     * using the dimension YZ.
     * @param a The point to calculate the angle.
     * @param b The second point.
     * @param c The third point.
     * @return The angle in degrees.
    */
    public static double angleYZ(Vec3d a, Vec3d b, Vec3d c) {
        return flatAngle(a, b, c, planeYZ);
    }

    /*
     * Calculate the angle of point A of a triangle formed by points A, B, and C.
     * Here, vectors are treated as 3D points in space, and you also flatten them
     * using the dimension XY.
     * @param a The point to calculate the angle.
     * @param b The second point.
     * @param c The third point.
     * @return The angle in degrees.
    */
    public static double angleXY(Vec3d a, Vec3d b, Vec3d c) {
        return flatAngle(a, b, c, planeXY);
    }

    /*
     * Flatten a vector using the X line.
     * @param vec The vector to flatten.
     * @return The flattened vector.
    */
    public static Vec3d flatX(Vec3d vec) {
        return vec.multiply(lineX);
    }

    /*
     * Flatten a vector using the Y line.
     * @param vec The vector to flatten.
     * @return The flattened vector.
    */
    public static Vec3d flatY(Vec3d vec) {
        return vec.multiply(lineY);
    }

    /*
     * Flatten a vector using the Z line.
     * @param vec The vector to flatten.
     * @return The flattened vector.
    */
    public static Vec3d flatZ(Vec3d vec) {
        return vec.multiply(lineZ);
    }

    /*
     * Flatten a vector using the XZ plane.
     * @param vec The vector to flatten.
     * @return The flattened vector.
    */
    public static Vec3d flatXZ(Vec3d vec) {
        return vec.multiply(planeXZ);
    }

    /*
     * Flatten a vector using the YZ plane.
     * @param vec The vector to flatten.
     * @return The flattened vector.
    */
    public static Vec3d flatYZ(Vec3d vec) {
        return vec.multiply(planeYZ);
    }

    /*
     * Flatten a vector using the XY plane.
     * @param vec The vector to flatten.
     * @return The flattened vector.
    */
    public static Vec3d flatXY(Vec3d vec) {
        return vec.multiply(planeXY);
    }
}
