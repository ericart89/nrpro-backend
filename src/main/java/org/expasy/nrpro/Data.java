package org.expasy.nrpro;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Data {

    public double mz;
    public double i;
    public boolean isIsotope;
    public double perci;
    public List<Annot> annot;
    public List<SpectraAnnot> spectraAnnot;
    public boolean hasAnnot;


    public double getMz() {
        return mz;
    }

    public double getI() {
        return i;
    }

    public boolean isIsotope() {
        return isIsotope;
    }

    public double getPerci() {
        return perci;
    }

    public List<Annot> getAnnot() {
        return annot;
    }

    public List<SpectraAnnot> getSpectraAnnot() {
        return spectraAnnot;
    }

    public boolean isHasAnnot() {
        return hasAnnot;
    }


}
