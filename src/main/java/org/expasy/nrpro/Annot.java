package org.expasy.nrpro;

import java.util.List;

public class Annot {
    public String charge;
    public String composition;
    public String mass;
    public List<String> mzdiff;
    public Annot2 annot;
    public List<Object> fragLabels;
    public List<Object> monLabels;
    public String nl;

    public String getCharge() {
        return charge;
    }

    public String getComposition() {
        return composition;
    }

    public String getMass() {
        return mass;
    }

    public List<String> getMzdiff() {
        return mzdiff;
    }

    public Annot2 getAnnot() {
        return annot;
    }

    public List<Object> getFragLabels() {
        return fragLabels;
    }

    public List<Object> getMonLabels() {
        return monLabels;
    }

    public String getNl() {
        return nl;
    }
}
