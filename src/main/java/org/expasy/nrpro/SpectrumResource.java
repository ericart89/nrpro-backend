package org.expasy.nrpro;

import analysis.Scores;
import analysis.SpectrumMatcher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import db.NRProCompound;
import decoy.DecoySpectrumMatcherHelper;
import decoy.DecoyTree;
import io.ExampleFilesStore;
import io.NRProCompoundMapper;
import io.SpectrumReader;

import ion.DissociationTechnique;
import ion.Tolerance;

import misc.*;

import org.apache.commons.math3.distribution.WeibullDistribution;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.expasy.mzjava.core.mol.PeriodicTable;
import org.expasy.mzjava.core.ms.consensus.ConsensusSpectrum;
import org.expasy.mzjava.core.ms.peaklist.peakfilter.AbstractMergePeakFilter;
import org.expasy.mzjava.core.ms.peaklist.peaktransformer.IdentityPeakProcessor;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import stats.DistributionCalculator;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Emma on 29/12/2016.
 */
@Path("/Spectrum")
public class SpectrumResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
            @FormDataParam("file") List<FormDataBodyPart> bodyParts,
            @FormDataParam("parentTolerance") double parentTolerance,
            @FormDataParam("parentMassUnit") String parentMassUnit,
            @FormDataParam("chargeState") String chargeState,
            @FormDataParam("tolerance") double tolerance,
            @FormDataParam("msmsMassUnit") String msmsMassUnit,
            @FormDataParam("deisotoping") boolean deisotoping,
            @FormDataParam("useDecoy") boolean useDecoy,
            @FormDataParam("hasZCions") boolean hasZCions,
            @FormDataParam("protonAdduct") String protonAdduct,
            @FormDataParam("neutrallosses") List<FormDataBodyPart> neutrallosses,
            @FormDataParam("adducts") List<FormDataBodyPart> adducts,
            @FormDataParam("isExample") boolean isExample

    )  {
        try {

            MongoDBConnection mongoConnection=MongoDBConnection.getInstance() ;
            MongoCollection structuresCollection = mongoConnection.getCollection("structures");
            DissociationTechnique dissociationTechnique= DissociationTechnique.CID;
            if(hasZCions){
                dissociationTechnique=DissociationTechnique.ETD;
            }

            Adduct adductProt=Adduct.valueOf(protonAdduct);
            int charge=Integer.parseInt(chargeState);
            boolean userDefinedCharge=false;
            if(charge!=0){
                userDefinedCharge=true;
            }

            String decoyCollectName="decoy_"+dissociationTechnique.toString()+"_"+adductProt.toString();
            MongoCollection decoyCollection = mongoConnection.getCollection(decoyCollectName);
            MongoCollection spectraCollection=mongoConnection.getCollection("spectra");
            MongoCollection requestsCollection=mongoConnection.getCollection("requests");

            NRProCompoundMapper mapper = new NRProCompoundMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            ObjectWriter writer=mapper.writer();

            SpectrumReader spectrumReader = new SpectrumReader();
            List<Result> results= new ArrayList<>();
            List<NeutralLoss> nlosses= new ArrayList<>();
            List<Adduct> addcts= new ArrayList<>();

            String nlossesString="";
            if(neutrallosses!=null){
                for(FormDataBodyPart nl: neutrallosses){
                    nlossesString=nlossesString+","+nl.getValueAs(String.class).toUpperCase();
                    nlosses.add(NeutralLoss.valueOf(nl.getValueAs(String.class).toUpperCase()));
                }
            }
            if(!nlossesString.equals("")){
                nlossesString=nlossesString.substring(1);
            }else{
                nlossesString="-";
            }

            String adductsString="";
            if(adducts!=null){
                for(FormDataBodyPart adduct: adducts){
                    adductsString=adductsString+","+adduct.getValueAs(String.class).toUpperCase();
                    addcts.add(Adduct.valueOf(adduct.getValueAs(String.class).toUpperCase()));
                }
            }
            if(!adductsString.equals("")){
                adductsString=adductsString.substring(1);
            }else{
                adductsString="-";
            }

            Tolerance.UnitTolerance unitToleranceParent= Tolerance.UnitTolerance.valueOf(parentMassUnit);
            Tolerance.UnitTolerance unitToleranceChild= Tolerance.UnitTolerance.valueOf(msmsMassUnit);

            Tolerance ionTolerance= new Tolerance(tolerance, unitToleranceChild);
            SpectrumMatcher spectralMatcher = new SpectrumMatcher(ionTolerance,nlosses,addcts, dissociationTechnique,adductProt,deisotoping);
            DecoySpectrumMatcherHelper decoySpectrumMatcher = new DecoySpectrumMatcherHelper(ionTolerance);

            /* Save multiple files */
            boolean hasPVal= false;
            ExampleFilesStore exampleFilesStore= ExampleFilesStore.getInstance();
            int size=0;
            if(!isExample){
                size= bodyParts.size();
            }else{
                size=exampleFilesStore.getSize();
            }
            if (size>20){

                return Response.status(Response.Status.CONFLICT) // 200 means OK, I want something different
                        .entity("{ \"error\":\"ERROR: Maximum number of files exceeded. Please introduce a maximum of 20 files.\" }")
                        .build();

            }

            for (int i = 0; i < size; i++) {//the last element is not a file (object FileList)
                String fileName = "";
                ConsensusSpectrum consensusSpectrum=null;
                if(!isExample){
                    fileName = bodyParts.get(i).getContentDisposition().getFileName();
                    InputStream inputStream =  bodyParts.get(i).getEntityAs(InputStream.class);
                    List<MsnSpectrum> listSpectrum =null;

                    try {

                        listSpectrum = spectrumReader.readSpectrum(inputStream,fileName);

                    }catch(Exception e){
                        return Response.status(Response.Status.CONFLICT)
                                .entity("{ \"error\":\"ERROR: Unreadable file/s. Please make sure that the format is accepted and specified in the file extension.\" }")
                                .build();
                    }

                   if (listSpectrum.size()>1 ) {
                        return Response.status(Response.Status.CONFLICT)
                                .entity("{ \"error\":\"ERROR: Multiple spectra in the file/s. Please make sure that each file contains a single spectrum.\" }")
                                .build();
                    }

                    double consensusTolerance=0.01;
                    if(ionTolerance.getUnitTolerance()==Tolerance.UnitTolerance.DA){
                        consensusTolerance=ionTolerance.getTolerance();
                    }
                    consensusSpectrum = ConsensusSpectrum.Builder.getBuilder()
                            .spectra(listSpectrum)
                            .setPeakFilterParams(0.5, 5)
                            .fragMzTolerance(consensusTolerance)
                            .intensityCombMethod(AbstractMergePeakFilter.IntensityMode.MEAN_ALL_INTENSITY).
                                    build();
                }else{
                    fileName =exampleFilesStore.getNameExampleFile(i);
                    consensusSpectrum = exampleFilesStore.getExampleFile(i);
                }

                double precursorMz =  consensusSpectrum.getPrecursor().getMz();

                if(!userDefinedCharge){
                    charge =consensusSpectrum.getPrecursor().getCharge();
                }

                if(charge==0){
                    return Response.status(Response.Status.CONFLICT)
                            .entity("{ \"error\":\"ERROR: Unknown charge. Please verify that the precursor charge is specified in the spectral data.\" }")
                            .build();
                }else if (charge>2){
                    return Response.status(Response.Status.CONFLICT)
                            .entity("{ \"error\":\"ERROR: Spectra with more than 2 charges are not allowed.\" }")
                            .build();
                }else if(charge ==2){
                    if(protonAdduct.equals("Na") || protonAdduct.equals("K")){
                        return Response.status(Response.Status.CONFLICT)
                                .entity("{ \"error\":\"ERROR: Multiply charged spectra with K/Na adducts are not allowed.\" }")
                                .build();
                    }

                }

                if(precursorMz == 0.0 ){
                    return Response.status(Response.Status.CONFLICT)
                            .entity("{ \"error\":\"ERROR: Unknown precursor mass. Please verify that the precursor mass is specified in the spectral data.\"}")
                            .build();
                }

                IntensityNormalizer.normalizeSpectrumInt(consensusSpectrum);

                double massCharge=(adductProt.getModMass()*charge)- PeriodicTable.ELECTRON_MASS*charge;
                double mass=(precursorMz*charge)-massCharge;
                double parentToleranceAdjusted=parentTolerance;
                if(unitToleranceParent== Tolerance.UnitTolerance.PPM){
                    parentToleranceAdjusted= Ppm2DaConverter.convert(parentTolerance,mass);
                }
                //********************candidates scoring********************//

                BasicDBObject gtQuery = new BasicDBObject();

                gtQuery.put("monoisotopicMass", (new BasicDBObject("$gt", mass-parentToleranceAdjusted)).append("$lt", mass+parentToleranceAdjusted));

                FindIterable findIterable = structuresCollection.find(gtQuery);

                MongoCursor mongoCursor=findIterable.iterator();
                boolean hasNonZeroCandidates=false;

                if(mongoCursor.hasNext()){

                    while (mongoCursor.hasNext()){

                        Object o = mongoCursor.next();
                        BasicDBObject obj = new BasicDBObject();
                        obj.put("peptide", o);
                        ConsensusSpectrum consensusSpectrumCandidate= consensusSpectrum.copy(new IdentityPeakProcessor<>());
                        String output = mapper.readTree(obj.toString()).path("peptide").toString();
                        NRProCompound nrProCompound=mapper.readValue(output, NRProCompound.class);
                        if (nrProCompound.getName().equals("Cyclosporin-A")){
                            continue;
                        }
                        Scores scores=spectralMatcher.getSpectralMatch(nrProCompound, consensusSpectrumCandidate, charge,false);
                        Candidate candidate=new Candidate(nrProCompound,scores,new URI(fileName),consensusSpectrumCandidate);
                        String spectraString=writer.writeValueAsString(candidate);
                        Document dbObject = Document.parse(spectraString);
                        dbObject.append("createdAt", new Date());
                        spectraCollection.insertOne(dbObject);
                        ObjectId id =  dbObject.getObjectId(( "_id" ));
                        Result result= new Result(id.toString(), nrProCompound, scores, fileName);
                        results.add(result);
                        if(scores.getDotProduct()!=0){
                            hasNonZeroCandidates=true;
                        }
                    }
                }else{

                    Result result= new Result(null,new NRProCompound("Unknown", "Unknown", Collections.emptySet(), Collections.emptyMap(),
                            "", 0, 0, 0, 0, "", null, null, "",
                            Collections.emptySet(), "", Collections.emptySet(), Collections.emptySet(), Collections.emptySet())
                            ,new Scores(0,0,0,0,0),fileName);
                    results.add(result);
                }

                mongoCursor.close();

                if(useDecoy && hasNonZeroCandidates){
                    //********************decoy database scoring********************//

                    BasicDBObject gtQuery2 = new BasicDBObject();
                    gtQuery2.put("precursorMass", (new BasicDBObject("$gt", mass-parentToleranceAdjusted)).append("$lt", mass+parentToleranceAdjusted+100));
                    FindIterable findIterable2 = decoyCollection.find(gtQuery2);
                    MongoCursor<Document> mongoCursor2=findIterable2.iterator();

                    //Do a map of dot products in case there is ovelap to not calculate it again
                    List<Double> dotProducts= new ArrayList<>();

                    Set<Double> dotProductsSet= new HashSet<>();
                    while (mongoCursor2.hasNext()){
                        Document o  = mongoCursor2.next();
                        BasicDBObject obj = new BasicDBObject();
                        obj.put("decoyTree", o);
                        String output = mapper.readTree(obj.toString()).path("decoyTree").toString();
                        DecoyTree decoyTree=mapper.readValue(output, DecoyTree.class);
                        double dotproduct=decoySpectrumMatcher.match(decoyTree,consensusSpectrum,charge,nlosses,addcts);
                        if(dotproduct!=0){
                            dotProducts.add(dotproduct);
                            dotProductsSet.add(dotproduct);
                        }
                    }
                    mongoCursor2.close();

                    if(dotProductsSet.size()>=5){
                        WeibullDistribution distribution= DistributionCalculator.getWeibullDist(dotProducts);
                        hasPVal= true;
                        for(Result result:results){
                            if(result.getFile().equals(fileName)){//not the best way to do it, check it again
                                double cumProb=distribution.cumulativeProbability(result.getDotProduct());
                                if(result.getScoredPeaks()>3){
                                    if(cumProb<1){
                                        result.setpVal(1-cumProb);
                                    }else{
                                        result.setpVal(1E-16);
                                    }
                                }
                            }
                        }
                    }
                }

            }

            Query query = new Query(hasPVal,parentTolerance,parentMassUnit,tolerance,msmsMassUnit,
                    deisotoping,useDecoy,hasZCions,protonAdduct,adductsString,nlossesString,results);

            String resultsString=writer.writeValueAsString(query);

            Document dbObject = Document.parse(resultsString);

            dbObject.append("createdAt", new Date());

            requestsCollection.insertOne(dbObject);

            String jsonId="{\"requestId\": \""+query.getQueryID()+"\"}";

            return Response.ok(jsonId, MediaType.APPLICATION_JSON).build();
        }catch (Exception e){
            return Response.status(Response.Status.CONFLICT) // 200 means OK, I want something different
                    .entity("{ \"error\":\"Unexpected error. The compound/s could not be identified.\" }")
                  .build();

        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/request/{requestId}")
    public String uploadResults(@PathParam("requestId") String requestId) throws IOException {

        MongoDBConnection mongoConnection=MongoDBConnection.getInstance() ;
        MongoCollection requestsCollection=mongoConnection.getCollection("requests");
        Document myDoc = (Document) requestsCollection.find(eq("queryID", requestId)).first();
        return myDoc.toJson();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{spectraId}")
    public String uploadFile(@PathParam("spectraId") String spectraId) throws IOException {
        MongoDBConnection mongoConnection=MongoDBConnection.getInstance() ;
        MongoCollection spectraCollection=mongoConnection.getCollection("spectra");
        Document myDoc = (Document) spectraCollection.find(eq("_id", new ObjectId(spectraId))).first();
        return myDoc.toJson();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{spectraId}")
    public Response manualEdition(@PathParam("spectraId") String spectraId, String editedData) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<Integer>>() {});

        MongoDBConnection mongoConnection=MongoDBConnection.getInstance() ;
        MongoCollection spectraCollection=mongoConnection.getCollection("spectra");
        Document myDoc = (Document) spectraCollection.find(eq("_id", new ObjectId(spectraId))).first();
        ObjectNode parent= (ObjectNode)objectMapper.readTree(myDoc.toJson());
        EditedData editedData1=objectMapper.readValue(editedData, EditedData.class);


        boolean removeOldAnnotation=editedData1.getRemoveOldAnnot();
        int modifiedIdx=editedData1.getPeakIdx();
        Data data = editedData1.getData();

        JsonNode jsonNode= parent.path("consensusSpectrum").path("peaks").get(modifiedIdx).path("annotations").get(0);

        if(jsonNode== null || removeOldAnnotation){
            MongoAnnotation mongoAnnotation=EditedData2MongoAnnotation.convert(data);
            JsonNode node = objectMapper.valueToTree(mongoAnnotation);
            if(jsonNode==null){
                ((ArrayNode)parent.path("consensusSpectrum").path("peaks").get(modifiedIdx).path("annotations")).add(node);
            }else{
                ((ArrayNode)parent.path("consensusSpectrum").path("peaks").get(modifiedIdx).path("annotations")).set(0,node);
            }
        }else{

            JsonNode monomerIdxsNode=jsonNode.path("annotation").get("monomerIdxs");
            List<Integer> monomerIdxs= reader.readValue(monomerIdxsNode);
            String condensed=jsonNode.path("annotation").get("condensed").asText();
            JsonNode monomerNodeIdxsNode=jsonNode.get("monomerNodeIdxs");
            List<Integer> monomerNodeIdxs= reader.readValue(monomerNodeIdxsNode);

            MongoAnnotation mongoAnnotation=EditedData2MongoAnnotation.convert(data,condensed,monomerIdxs,monomerNodeIdxs);
            JsonNode node = objectMapper.valueToTree(mongoAnnotation);
            ((ArrayNode)parent.path("consensusSpectrum").path("peaks").get(modifiedIdx).path("annotations")).set(0,node);
        }

        Document dbObject = Document.parse(parent.toString());
        spectraCollection.replaceOne(myDoc,dbObject);

        return Response.ok().build();
    }

}
