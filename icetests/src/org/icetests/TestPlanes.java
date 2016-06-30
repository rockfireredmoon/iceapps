package org.icetests;

import java.util.logging.Logger;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;

/**
 * @author Nick Wiggill
 */
public class TestPlanes {

    public static final Logger logger = Logger.getLogger(TestPlanes.class.getName());

    public static void main(String[] args) throws Exception {
        //***Outline.
        //This example shows how to construct a plane representation using
        //com.jme.math.Plane.
        //We will create a very simple, easily-imagined 3D plane. It will
        //be perpendicular to the x axis (it's facing). It's "centre" (if
        //such a thing exists in an infinite plane) will be positioned 1
        //unit along the positive x axis.

        //***Step 1.
        //The vector that represents the normal to the plane, in 3D space.
        //Imagine a vector coming out of the origin in this direction.
        //There is no displacement yet (see Step 2, below).
        Vector3f normal = new Vector3f(.1f, 0, 0);

        //***Step 2.
        //This is our displacement vector. The plane remains facing in the
        //direction we've specified using the normal above, but now we are
        //are actually giving it a position other than the origin.
        //We will use this displacement to define the variable "constant"
        //needed to construct the plane. (see step 3)
        Vector3f displacement = Vector3f.UNIT_X;
        //or
        //Vector3f displacement = new Vector3f(1f, 0, 0);

        //***Step 3.
        //Here we generate the constant needed to define any plane. This
        //is semi-arcane, don't let it worry you. All you need to
        //do is use this same formula every time.
        float constant = displacement.dot(normal);

        //***Step 4.
        //Finally, construct the plane using the data you have assembled.
        Plane plane = new Plane(normal, constant);

        //***Some tests.
        logger.info("Plane info: " + plane.toString()); //trace our plane's information

        Vector3f p1 = new Vector3f(1.1f, 0, 0); //beyond the plane (further from origin than plane)
        Vector3f p2 = new Vector3f(0.9f, 0, 0); //before the plane (closer to origin than plane)
        Vector3f p3 = new Vector3f(1f, 0, 0); //on the plane

        logger.info("p1 position relative to plane is " + plane.whichSide(p1) + " " + plane.pseudoDistance(p1)); //outputs NEGATIVE
        logger.info("p2 position relative to plane is " + plane.whichSide(p2) + " " + plane.pseudoDistance(p2)); //outputs POSITIVE
        logger.info("p3 position relative to plane is " + plane.whichSide(p3) + " " + plane.pseudoDistance(p3)); //outputs NONE
        
    }
}