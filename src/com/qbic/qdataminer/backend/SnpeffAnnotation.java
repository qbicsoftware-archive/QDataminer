/*******************************************************************************
 *     Copyright (C) 2016  Sebastian Goerges
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.qbic.qdataminer.backend;

import org.json.JSONObject;

public class SnpeffAnnotation {
	/*
	 * This class represents an snpEff annotation of a certain alternative
	 */
	
    private String annotation = "";
    private String annotationImpact = "";
    private String geneName = "";
    private String geneID = "";
    private String featureType = "";
    private String featureID = "";
    private String transcriptBiotype = "";
    private String exonIntronRankCount = "";
    private String HGVSc = "";
    private String HGVSp = "";
    private String mutation = "";
    private String cdnaPosLength = "";
    private String cdsPosLength = "";
    private String aaPosLength = "";
    private Long distance = null;
    private String errors = "";
    private String alternativeBases;
    private Alternative alternative;
    
    
    public SnpeffAnnotation (JSONObject sa, Alternative alt){
		this.annotation = sa.optString("Annotation");	
		this.annotationImpact = sa.optString("Annotation_Impact");
		this.geneName = sa.optString("Gene_Name");
		this.geneID = sa.optString("Gene_ID");
		this.featureType = sa.optString("Feature_Type");
		this.featureID = sa.optString("Feature_ID");
		this.transcriptBiotype = sa.optString("Transcript_BioType");
		this.exonIntronRankCount = sa.optString("Rank");
		this.HGVSc = sa.optString("HGVS_c");
		this.HGVSp = sa.optString("HGVS_p");
		this.mutation = sa.optString("Mutation");
		this.cdnaPosLength = sa.optString("cDNA_pos_cDNA_length");
		this.cdsPosLength = sa.optString("CDS_pos_CDS_length");
		this.aaPosLength = sa.optString("AA_pos_AA_length");
		if (sa.has("Distance")){
			this.distance = sa.getLong("Distance");	
		}
		this.errors = sa.optString("ERRORS_WARNINGS_INFO");
    	this.alternative = alt;
    	this.alternativeBases = alt.getAlternateBases();    	
    }
    
    /*
     * Getter
     */
    
	public String getAnnotation() {
		return annotation;
	}
	public String getAnnotationImpact() {
		return annotationImpact;
	}
	public String getGeneName() {
		return geneName;
	}
	public String getGeneID() {
		return "<a href='http://www.ensembl.org/id/" + geneID + "' target='_blank'>"+ geneID + "</a>";
	}
	public String getFeatureType() {
		return featureType;
	}
	public String getFeatureID() {
		return "<a href='http://www.ensembl.org/id/" + featureID + "' target='_blank'>"+ featureID + "</a>";
	}
	public String getTranscriptBiotype() {
		return transcriptBiotype;
	}
	public String getExonIntronRankCount() {
		return exonIntronRankCount;
	}
	public String getHGVSc() {
		return HGVSc;
	}
	public String getHGVSp() {
		return HGVSp;
	}
	public String getMutation() {
		return mutation;
	}
	public String getCdnaPosLength() {
		return cdnaPosLength;
	}
	public String getCdsPposLength() {
		return cdsPosLength;
	}
	public String getAaPosLength() {
		return aaPosLength;
	}
	public Long getDistance() {
		return distance;
	}
	public String getErrors() {
		return errors;
	}
	public Alternative getAlternative() {
		return alternative;
	}
	public String getAlternativeBases() {
		return alternativeBases;
	}

}
