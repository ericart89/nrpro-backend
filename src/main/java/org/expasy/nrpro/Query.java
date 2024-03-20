package org.expasy.nrpro;

import misc.Adduct;
import misc.NeutralLoss;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Query {
    private String queryID;
    private boolean hasPVal;
    private double parentTolerance;
    private String parentMassUnit;
    private double tolerance;
    private String msmsMassUnit;
    private boolean deisotoping;
    private boolean useDecoy;
    private boolean hasZCions;
    private String protonAdduct;
    private String adducts;
    private String neutrallosses;
    private List<Result> results;


    public Query( boolean hasPVal, double parentTolerance, String parentMassUnit, double tolerance, String msmsMassUnit,
                  boolean deisotoping, boolean useDecoy, boolean hasZCions, String protonAdduct, String adducts,
                  String neutrallosses, List<Result> results) {

        List<NeutralLoss> nlosses= new ArrayList<>();
        List<Adduct> addcts= new ArrayList<>();
        this.queryID = UUID.randomUUID().toString();
        this.hasPVal = hasPVal;
        this.parentTolerance = parentTolerance;
        this.parentMassUnit = parentMassUnit;
        this.tolerance = tolerance;
        this.msmsMassUnit = msmsMassUnit;
        this.deisotoping = deisotoping;
        this.useDecoy = useDecoy;
        this.hasZCions = hasZCions;
        this.protonAdduct = protonAdduct;
        this.adducts = adducts;
        this.neutrallosses = neutrallosses;
        this.results = results;
    }

    public String getQueryID() {
        return queryID;
    }

    public boolean isHasPVal() {
        return hasPVal;
    }

    public double getParentTolerance() {
        return parentTolerance;
    }

    public String getParentMassUnit() {
        return parentMassUnit;
    }

    public double getTolerance() {
        return tolerance;
    }

    public String getMsmsMassUnit() {
        return msmsMassUnit;
    }

    public boolean isDeisotoping() {
        return deisotoping;
    }

    public boolean isUseDecoy() {
        return useDecoy;
    }

    public boolean isHasZCions() {
        return hasZCions;
    }

    public String getProtonAdduct() {
        return protonAdduct;
    }

    public String getAdducts() {
        return adducts;
    }

    public String getNeutrallosses() {
        return neutrallosses;
    }

    public List<Result> getResults() {
        return results;
    }


}
