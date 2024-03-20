package org.expasy.nrpro;

import java.util.List;

public class MongoAnnotation {
    public double theoreticalMz;
    public double theoreticalMass;
    public List<String> massDifference;
    public int charge;
    public String neutralLosses;
    public String composition;
    public Annotation annotation;
    public List<Integer> monomerNodeIdxs;

    public MongoAnnotation() {
    }

    public MongoAnnotation(double theoreticalMz, double theoreticalMass,List<String> massDifference ,int charge, String neutralLosses, String composition, Annotation annotation, List<Integer> monomerNodeIdxs) {
        this.theoreticalMz = theoreticalMz;
        this.theoreticalMass = theoreticalMass;
        this.massDifference= massDifference;
        this.charge = charge;
        this.neutralLosses = neutralLosses;
        this.composition = composition;
        this.annotation = annotation;
        this.monomerNodeIdxs = monomerNodeIdxs;
    }

    public double getTheoreticalMz() {
        return theoreticalMz;
    }

    public double getTheoreticalMass() {
        return theoreticalMass;
    }

    public List<String> getMassDifference() {
        return massDifference;
    }

    public int getCharge() {
        return charge;
    }

    public String getNeutralLosses() {
        return neutralLosses;
    }

    public String getComposition() {
        return composition;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public List<Integer> getMonomerNodeIdxs() {
        return monomerNodeIdxs;
    }
}
