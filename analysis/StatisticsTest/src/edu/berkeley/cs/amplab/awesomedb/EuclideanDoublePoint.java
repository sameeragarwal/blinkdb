package edu.berkeley.cs.amplab.awesomedb;

import java.util.Collection;

import org.apache.commons.math.stat.clustering.Clusterable;
import org.apache.commons.math.stat.clustering.EuclideanIntegerPoint;
import org.apache.commons.math.util.MathUtils;

public class EuclideanDoublePoint implements Clusterable<EuclideanDoublePoint> {
    double[] point;
    
    public EuclideanDoublePoint(final double[] point) {
        this.point = point;
    }
    @Override
    public double distanceFrom(EuclideanDoublePoint p) {
        return MathUtils.distance(point, p.point);
    }

    @Override
    public EuclideanDoublePoint centroidOf(Collection<EuclideanDoublePoint> points) {
        double[] centroid = new double[point.length];
        for (EuclideanDoublePoint p : points) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] += p.point[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= points.size();
        }
        return new EuclideanDoublePoint(centroid);
    }
    
    public double[] getPoint() {
        return point;
    }

}
