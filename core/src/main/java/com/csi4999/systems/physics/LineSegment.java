package com.csi4999.systems.physics;


import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public abstract class LineSegment extends Collider {
    /**
     * LineSegment collider. The line parameter specifies the length/direction of the segment (end - start), and
     * the PhysicsObject position vector represents the starting position of the segment.
     */
    protected float lineLength;

    private Vector2 lineStart, lineEnd, temp1;
    private Affine2 lineMat;

    public LineSegment() {}
    public LineSegment(float lineLength) {
        super(new Vector2(0f, 0f), new Vector2(0f, 0f), new Vector2(0f, 0f));
        this.lineLength = lineLength;
        lineStart = new Vector2();
        lineEnd = new Vector2();
        temp1 = new Vector2();
        lineMat = new Affine2();
    }

    public LineSegment(Vector2 pos, float lineLength) {
        super(pos, new Vector2(0f, 0f), new Vector2(0f, 0f));
        this.lineLength = lineLength;
        lineStart = new Vector2();
        lineEnd = new Vector2();
        temp1 = new Vector2();
        lineMat = new Affine2();
    }

    @Override
    public boolean collidesWith(Collider other) {
        if (other instanceof Circle) {
            return circleCheck((Circle) other);
        } else {
            return false;
        }
    }

    private boolean circleCheck(Circle o) {
        return Intersector.nearestSegmentPoint(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y, o.transformedPos.x, o.transformedPos.y, temp1).dst2(o.transformedPos.x, o.transformedPos.y) < o.transformedRadius * o.transformedRadius;
    }

    @Override
    public void computeBounds() {
        // TODO: remove redundant computation between this and circleCheck
        lineMat.setToTranslation(lineLength, 0f);
        lineMat.preMul(worldTransform);
        lineStart.set(transformedPos.x, transformedPos.y);
        lineMat.getTranslation(lineEnd);

        bounds.setPosition(lineStart);
        bounds.setSize(0f);
        bounds.merge(lineEnd);
    }

}
