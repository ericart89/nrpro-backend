package org.expasy.nrpro;

import java.util.Collections;
import java.util.List;

public class EditedData2MongoAnnotation {
    public static MongoAnnotation convert(Data editedData, String condensed,List<Integer> monomerIdxs,List<Integer> monomerNodeIdxs){

       Annot editedAnnotations=editedData.getAnnot().get(0);
       Annotation annotation = new Annotation(condensed, editedAnnotations.getAnnot().getFull(), editedAnnotations.getAnnot().getIonAnnot(), monomerIdxs);
       return new MongoAnnotation(0,Double.parseDouble(editedAnnotations.getMass()),editedAnnotations.getMzdiff(),Integer.parseInt(editedAnnotations.getCharge()),editedAnnotations.getNl(),editedAnnotations.getComposition(),annotation,monomerNodeIdxs);

    }

    public static MongoAnnotation convert(Data editedData){
        Annot editedAnnotations=editedData.getAnnot().get(0);
        Annotation annotation = new Annotation("", editedAnnotations.getAnnot().getFull(), editedAnnotations.getAnnot().getIonAnnot(), Collections.emptyList());
        return new MongoAnnotation(0,Double.parseDouble(editedAnnotations.getMass()),editedAnnotations.getMzdiff(),Integer.parseInt(editedAnnotations.getCharge()),editedAnnotations.getNl(),editedAnnotations.getComposition(),annotation,Collections.emptyList());
    }
}
