package org.expasy.nrpro;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EditedData {
    public Data data;
    public int peakIdx;
    public boolean removeOldAnnot;

    public EditedData() {
    }

    public Data getData() {
        return data;
    }

    public int getPeakIdx() {
        return peakIdx;
    }

    public boolean getRemoveOldAnnot() {
        return removeOldAnnot;
    }
}
