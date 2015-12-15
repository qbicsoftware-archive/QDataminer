package com.qbic.qdataminer.backend;

import org.json.JSONObject;

public class Alternative {
	
	/*
	 * This class represents an alternative base of a certain variant
	 */

	private String alternateBases;
	private Double alleleFrequency;
	private Double frequencySAS;
	private Double frequencyEUR;
	private Double frequencyAFR;
	private Double frequencyAMR;
	private Double frequencyEAS;
	private Long alleleCount;
	private Variant variant;
	
	public Alternative (String alt, Double af, Double af_SAS, Double af_EUR, Double af_AFR, Double af_AMR, Double af_EAS, Long ac, Variant var){
		this.alternateBases = alt;
		this.alleleFrequency = af;
		this.frequencySAS = af_SAS;
		this.frequencyEUR = af_EUR;
		this.frequencyAFR = af_AFR;
		this.frequencyAMR = af_AMR;
		this.frequencyEAS = af_EAS;
		this.alleleCount = ac;
		this.variant = var;
	}
	
	public Alternative (JSONObject alt, Variant var){
		this.alternateBases = alt.getString("Alternate_bases"); 
		this.alleleFrequency = alt.getDouble("Allele_frequency"); 
		this.frequencySAS = alt.getDouble("Allele_frequency_SAS"); 
		this.frequencyEUR = alt.getDouble("Allele_frequency_EUR"); 
		this.frequencyAFR = alt.getDouble("Allele_frequency_AFR"); 
		this.frequencyAMR = alt.getDouble("Allele_frequency_AMR");
		this.frequencyEAS = alt.getDouble("Allele_frequency_EAS");
		this.alleleCount = alt.getLong("Allele_count_in_gt");
		this.variant = var;
	}
	/*
	 * Getter
	 */
	public String getAlternateBases() {
		return alternateBases;
	}

	public Double getAlleleFrequency() {
		return alleleFrequency;
	}

	public Double getFrequencySAS() {
		return frequencySAS;
	}

	public Double getFrequencyEUR() {
		return frequencyEUR;
	}

	public Double getFrequencyAFR() {
		return frequencyAFR;
	}

	public Double getFrequencyAMR() {
		return frequencyAMR;
	}

	public Double getFrequencyEAS() {
		return frequencyEAS;
	}

	public Long getAlleleCount() {
		return alleleCount;
	}

	public Variant getVariant() {
		return variant;
	}
}
