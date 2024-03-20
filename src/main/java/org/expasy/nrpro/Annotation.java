package org.expasy.nrpro;

import java.util.List;

public class Annotation {

    public String condensed;
    public String full;
    public String ionAnnot;
    public List<Integer> monomerIdxs;

    public Annotation() {
    }

    public Annotation(String condensed, String full, String ionAnnot, List<Integer> monomerIdxs) {
        this.condensed = condensed;
        this.full = full;
        this.ionAnnot = ionAnnot;
        this.monomerIdxs = monomerIdxs;
    }

    public String getCondensed() {
        return condensed;
    }

    public String getFull() {
        return full;
    }

    public String getIonAnnot() {
        return ionAnnot;
    }

    public List<Integer> getMonomerIdxs() {
        return monomerIdxs;
    }
}
