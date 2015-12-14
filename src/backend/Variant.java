package backend;

import org.json.JSONArray;
import org.json.JSONObject;

public class Variant {
	
	/*
	 * This class represents a variant
	 */
	
	// Common fields --> Details
	private String variantID = null;
	private String chromosome = null;
	private Long position = null;
	private String externalId = null;
	private String referenceBases = null;
	private JSONArray variantTypes = null;
	
	// Additional information
	private Double quality = null;
	private String filter = null;
	private String ancestralAllele = null;
	private Long totalNrOfAlleles = null;
	private Long numberOfSamples = null;
	private Long depthAcrossSamples = null;
	private Long targetSiteDublication = null;
	private String structuralVariantType = null;
	private Long structuralVariantLength = null;
	private String mergedCalls = null;
	private String sourceCallset = null;
	private Long endCoordinateVariant = null;
	private Boolean exonPulldownTarget = null;
	private Boolean multiAllelic = null;
	private Boolean imperciseStruturalVariation = null;
	private String cofIntervEndImpercise = null;
	private String cofIntervPosImpercise = null;
	private String source = null;
	
	public Variant (JSONObject var){
		this.variantID = var.getString("_id");
		this.chromosome = var.getString("Chromosome");
		this.position = var.getLong("Position");
		if (var.has("ID")){
			this.externalId = var.getString("ID");
		}
		this.referenceBases = var.getString("Reference_bases");
		if (var.has("Variant_type")){
			this.variantTypes = var.getJSONArray("Variant_type");
		}	
		if (var.has("Quality")){
			this.quality = var.getDouble("Quality");
		}
		if (var.has("Filter")){
			this.filter = var.getString("Filter");
		}
		if (var.has("Ancestral_allele")){
			this.ancestralAllele = var.getString("Ancestral_allele");
		}
		if (var.has("Total_nr_of_alleles")){
			this.totalNrOfAlleles = var.getLong("Total_nr_of_alleles");
		}
		if (var.has("Number_of_samples")){
			this.numberOfSamples = var.getLong("Number_of_samples");
		}
		if (var.has("Depth_across_samples")){
			this.depthAcrossSamples = var.getLong("Depth_across_samples");
		}
		if (var.has("Target_Site_Duplication")){
			this.targetSiteDublication = var.getLong("Target_Site_Duplication");
		}
		if (var.has("Structural_Variant_Type")){
			this.structuralVariantType = var.getString("Structural_Variant_Type");
		}
		if (var.has("Structural_Variant_Length")){
			this.structuralVariantLength = var.getLong("Structural_Variant_Length");
		}
		if (var.has("Merged_Calls")){
			this.mergedCalls = var.getString("Merged_Calls");
		}
		if (var.has("Source_Callset")){
			this.sourceCallset = var.getString("Source_Callset");
		}
		if (var.has("End_Coordinate_Variant")){
			this.endCoordinateVariant = var.getLong("End_Coordinate_Variant");
		}
		if (var.has("Exon_Pulldown_Target")){
			this.exonPulldownTarget = var.getBoolean("Exon_Pulldown_Target");
		}
		if (var.has("Multi_Allelic")){
			this.multiAllelic = var.getBoolean("Multi_Allelic");
		}
		if (var.has("Imprecise_Structural_Variation")){
			this.imperciseStruturalVariation = var.getBoolean("Imprecise_Structural_Variation");
		}
		if (var.has("Cof_Interv_End_Impercice")){
			this.cofIntervEndImpercise = var.getString("Cof_Interv_End_Impercice");
		}
		if (var.has("Cof_Interv_Pos_Impercice")){
			this.cofIntervPosImpercise = var.getString("Cof_Interv_Pos_Impercice");
		}
		if (var.has("Source")){
			this.source = var.getString("Source");
		}		
	}
	
	/*
	 * Getter
	 */

	public String getVariantID() {
		return variantID;
	}
	
	public String getVariantIDWithLabel() {
		return "Variant ID: " + variantID;
	}

	public String getChromosome() {
		return chromosome;
	}
	
	public String getChromosomeWithLabel() {
		return "Chromosome: " + chromosome;
	}

	public Long getPosition() {
		return position;
	}
	
	public String getPositionWithLabel() {
		return "Position: " + position;
	}

	public String getExternalID() {
		return externalId;
	}
	
	public String getExternalIDWithLabel() {
		return "External ID: " + externalId;
	}

	public String getReferenceBases() {
		return referenceBases;
	}
	
	public String getReferenceBasesWithLabel() {
		return "Reference bases: " + referenceBases;
	}

	public Double getQuality() {
		return quality;
	}
	
	public String getQualityWithLabel() {
		return "Quality: " + quality;
	}

	public String getFilter() {
		return filter;
	}
	
	public String getFilterWithLabel() {
		return "Filter: " + filter;
	}

	public String getAncestralAllele() {
		return ancestralAllele;
	}
	
	public String getAncestralAlleleWithLabel() {
		return "Ancestral allele: " + ancestralAllele;
	}

	public Long getTotalNrOfAlleles() {
		return totalNrOfAlleles;
	}
	
	public String getTotalNrOfAllelesWithLabel() {
		return "Total number of alleles: " + totalNrOfAlleles;
	}

	public Long getNumberOfSamples() {
		return numberOfSamples;
	}
	
	public String getNumberOfSamplesWithLabel() {
		return "Number of samples: " + numberOfSamples;
	}

	public Long getDepthAcrossSamples() {
		return depthAcrossSamples;
	}
	
	public String getDepthAcrossSamplesWithLabel() {
		return "Depth across samples: " + depthAcrossSamples;
	}

	public Long getTargetSiteDublication() {
		return targetSiteDublication;
	}
	
	public String getTargetSiteDublicationWithLabel() {
		return "Target site dublication: " + targetSiteDublication;
	}

	public String getStructuralVariantType() {
		return structuralVariantType;
	}
	
	public String getStructuralVariantTypeWithLabel() {
		return "Structural variant type: " + structuralVariantType;
	}

	public Long getStructuralVariantLength() {
		return structuralVariantLength;
	}
	
	public String getStructuralVariantLengthWithLabel() {
		return "Structural variant length: " + structuralVariantLength;
	}

	public String getMergedCalls() {
		return mergedCalls;
	}
	
	public String getMergedCallsWithLabel() {
		return "Merged calls: " + mergedCalls;
	}

	public String getSourceCallset() {
		return sourceCallset;
	}
	
	public String getSourceCallsetWithLabel() {
		return "Source callset: " + sourceCallset;
	}

	public Long getEndCoordinateVariant() {
		return endCoordinateVariant;
	}
	
	public String getEndCoordinateVariantWithLabel() {
		return "End coordinate variant: " + endCoordinateVariant;
	}

	public Boolean getExonPulldownTarget() {
		return exonPulldownTarget;
	}
	
	public String getExonPulldownTargetWithLabel() {
		return "Exon pulldown target: " + exonPulldownTarget;
	}

	public Boolean getMultiAllelic() {
		return multiAllelic;
	}
	
	public String getMultiAllelicWithLabel() {
		return "Multi-allelic: " + multiAllelic;
	}

	public Boolean getImperciseStruturalVariation() {
		return imperciseStruturalVariation;
	}
	
	public String getImperciseStruturalVariationWithLabel() {
		return "Impercise structural variation: " + imperciseStruturalVariation;
	}

	public String getCofIntervEndImpercise() {
		return cofIntervEndImpercise;
	}
	
	public String getCofIntervEndImperciseWithLabel() {
		return "Confidence interval around END for imprecise variants: " + cofIntervEndImpercise;
	}

	public String getCofIntervPosImpercise() {
		return cofIntervPosImpercise;
	}
	
	public String getCofIntervPosImperciseWithLabel() {
		return "Confidence interval around POS for imprecise variants: " + cofIntervPosImpercise;
	}

	public String getSource() {
		return source;
	}
	
	public String getSourceWithLabel() {
		return "Source: " + source;
	}
	
	public JSONArray getVariantTypes() {
		return variantTypes;
	}
	
	public String getVariantTypesWithLabel() {
		return "Variant type(s): " + variantTypes.join(",");
	}
}
