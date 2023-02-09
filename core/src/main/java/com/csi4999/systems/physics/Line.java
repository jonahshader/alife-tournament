package com.csi4999.systems.physics;


import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

public abstract class Line extends Collider {
    protected Vector2 line;

    private Vector2 lineStart, lineEnd, circleCenter, temp1, temp2;
    private Affine2 lineMat;

    public Line() {}
    public Line(Vector2 line) {
        super(new Vector2(0f, 0f), new Vector2(0f, 0f), new Vector2(0f, 0f));
        this.line = line;
        lineStart = new Vector2();
        lineEnd = new Vector2();
        circleCenter = new Vector2();
        temp1 = new Vector2();
        temp2 = new Vector2();
        lineMat = new Affine2();
    }

    public Line(Vector2 pos, Vector2 line) {
        super(pos, new Vector2(0f, 0f), new Vector2(0f, 0f));
        this.line = line;
        lineStart = new Vector2();
        lineEnd = new Vector2();
        circleCenter = new Vector2();
        temp1 = new Vector2();
        temp2 = new Vector2();
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
        lineMat.setToTranslation(line);
        lineMat.preMul(worldTransform); // TODO: might be postMul or somethin
        lineStart.set(transformedPos.x, transformedPos.y);
        lineMat.getTranslation(lineEnd);


        temp1.set(lineEnd).sub(lineStart);
        float denom = temp1.len2();
        circleCenter.set(o.transformedPos.x, o.transformedPos.y);
        temp1.set(circleCenter).sub(lineStart);
        temp2.set(lineEnd).sub(lineStart);
        float numer = temp1.dot(temp2);
//        bTemp.set
        float h = Math.min(1f, Math.max(0f, numer/denom));

        temp2.scl(h);
        temp1.sub(temp2);
        float dist2 = temp2.len2();
        return dist2 < o.transformedRadius * o.transformedRadius;
    }

    @Override
    public void computeBounds() {
        // TODO: remove redundant computation between this and circleCheck
        lineMat.setToTranslation(line);
        lineMat.preMul(worldTransform); // TODO: might be postMul or somethin
        lineStart.set(transformedPos.x, transformedPos.y);
        lineMat.getTranslation(lineEnd);

        bounds.setPosition(lineStart);
        bounds.setSize(0f);
        bounds.merge(lineEnd);
        System.out.println(bounds);
    }

}
