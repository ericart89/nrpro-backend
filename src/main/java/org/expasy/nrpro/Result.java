package org.expasy.nrpro;

import analysis.Scores;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import db.NRProCompound;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@JsonSerialize(using = ResultSerializer.class)
public class Result {
    private String spectraId;
    private NRProCompound candidate;
    private int annotPeaks;
    private int scoredPeaks;
    private int isotopePeaks;
    private double dotProduct;
    private double score;
    private double pVal=1;
    private String strPVal="1";
    private String file;

    public Result() {
    }

    public Result(String spectraId, NRProCompound candidate, Scores scores, String file) {
        this.spectraId = spectraId;
        this.candidate = candidate;
        this.annotPeaks=scores.getNumAnnotPeaks();
        this.scoredPeaks=scores.getNumScoredPeaks();
        this.dotProduct= scores.getDotProduct();
        this.isotopePeaks=scores.getNumIsotopes();
        this.file = file;
    }

    public String getSpectraId() {
        return spectraId;
    }

    public NRProCompound getCandidate() {
        return candidate;
    }

    public int getAnnotPeaks() {
        return annotPeaks;
    }

    public int getScoredPeaks() {
        return scoredPeaks;
    }

    public double getDotProduct() {
        return dotProduct;
    }

    public double getpVal() {
        return pVal;
    }

    public void setpVal(double pVal) {
        this.pVal = pVal;
        this.strPVal = roundPVal(pVal);
        this.score=-10*Math.log10(pVal);
    }


    public String getFile() {
        return file;
    }

    public int getIsotopePeaks() {
        return isotopePeaks;
    }

    public double getScore() {
        return score;
    }

    public String getStrPVal() {
        return strPVal;
    }
    public static String roundPVal(double d){
        String str= Double.toString(d);
        if (str.contains("E")){
            String[] array=str.split("E|\\.");
            String upToNCharacters = array[1].substring(0, Math.min(array[1].length(),3));
            String rounded=array[0]+"."+upToNCharacters+"E"+array[2];
            return rounded;
        }else{

            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            return df.format(d);
        }
    }
}
