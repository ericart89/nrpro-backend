package org.expasy.nrpro;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import db.Database;
import db.NRProCompound;

import java.io.IOException;

import java.util.Map;
import java.util.Set;

public class ResultSerializer extends JsonSerializer<Result> {

    @Override
    public void serialize(Result result, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("spectraId",result.getSpectraId());
        NRProCompound nrProCompound= result.getCandidate();
        jsonGenerator.writeObjectFieldStart("candidate");
            jsonGenerator.writeStringField("id",nrProCompound.getId());
            jsonGenerator.writeStringField("name",nrProCompound.getName());
            //jsonGenerator.externalprivate Map<Database, Set<String>> externalIds;
            Map<Database, Set<String>> externalIds=nrProCompound.getExternalIds();
            jsonGenerator.writeObjectFieldStart("externalIds");
                for(Database db : externalIds.keySet()){
                    jsonGenerator.writeArrayFieldStart(db.toString());
                    for(String id:externalIds.get(db)){
                        jsonGenerator.writeString(id);
                    }
                    jsonGenerator.writeEndArray();
                }
            jsonGenerator.writeEndObject();
            jsonGenerator.writeNumberField("monoisotopicMass",nrProCompound.getMonoisotopicMass());
            jsonGenerator.writeStringField("formula",nrProCompound.getFormula());
        jsonGenerator.writeEndObject();
        jsonGenerator.writeNumberField("annotPeaks",result.getAnnotPeaks());
        jsonGenerator.writeNumberField("scoredPeaks",result.getScoredPeaks());
        jsonGenerator.writeNumberField("isotopePeaks",result.getIsotopePeaks());
        jsonGenerator.writeNumberField("pVal",result.getpVal());
        jsonGenerator.writeStringField("strPVal",result.getStrPVal());
        jsonGenerator.writeNumberField("score",result.getScore());
        jsonGenerator.writeStringField("file",result.getFile());
        jsonGenerator.writeEndObject();
    }
}

