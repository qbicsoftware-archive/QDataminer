package backend;

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
    
    	if (sa.has("Annotation")){
    		this.annotation = sa.getString("Annotation");	
    	}
    	if (sa.has("Annotation_Impact")){
    		this.annotationImpact = sa.getString("Annotation_Impact");
    	}
    	if (sa.has("Gene_Name")){
    		this.geneName = sa.getString("Gene_Name");
    	}
    	if (sa.has("Gene_ID")){
    		this.geneID = sa.getString("Gene_ID");
    	}
    	if (sa.has("Feature_Type")){
    		this.featureType = sa.getString("Feature_Type");
    	}
    	if (sa.has("Feature_ID")){
    		this.featureID = sa.getString("Feature_ID");
    	}
    	if (sa.has("Transcript_BioType")){
    		this.transcriptBiotype = sa.getString("Transcript_BioType");
    	}
    	if (sa.has("Rank")){
    		this.exonIntronRankCount = sa.getString("Rank");
    	}
    	if (sa.has("HGVS_c")){
    		this.HGVSc = sa.getString("HGVS_c");
    	}
    	if (sa.has("HGVS_p")){
    		this.HGVSp = sa.getString("HGVS_p");
    	}
    	if (sa.has("Mutation")){
    		this.mutation = sa.getString("Mutation");
    	}
    	if (sa.has("cDNA_pos_cDNA_length")){
    		this.cdnaPosLength = sa.getString("cDNA_pos_cDNA_length");
    	}
    	if (sa.has("CDS_pos_CDS_length")){
    		this.cdsPosLength = sa.getString("CDS_pos_CDS_length");
    	}
    	if (sa.has("AA_pos_AA_length")){
    		this.aaPosLength = sa.getString("AA_pos_AA_length");
    	}
    	if (sa.has("Distance")){
    		this.distance = sa.getLong("Distance");
    	}
    	if (sa.has("ERRORS_WARNINGS_INFO")){
    		this.errors = sa.getString("ERRORS_WARNINGS_INFO");
    	}
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
