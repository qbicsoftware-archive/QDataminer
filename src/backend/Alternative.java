package backend;

import org.json.JSONObject;

public class Alternative {
	
	/*
	 * This class represents an alternative base of a certain variant
	 */

	private String alternate_bases;
	private Double allele_frequency;
	private Double allele_frequency_SAS;
	private Double allele_frequency_EUR;
	private Double allele_frequency_AFR;
	private Double allele_frequency_AMR;
	private Double allele_frequency_EAS;
	private Long allele_count;
	private String variant_id;
	
	public Alternative (String alt, Double af, Double af_SAS, Double af_EUR, Double af_AFR, Double af_AMR, Double af_EAS, Long ac, String vid){
		this.alternate_bases = alt;
		this.allele_frequency = af;
		this.allele_frequency_SAS = af_SAS;
		this.allele_frequency_EUR = af_EUR;
		this.allele_frequency_AFR = af_AFR;
		this.allele_frequency_AMR = af_AMR;
		this.allele_frequency_EAS = af_EAS;
		this.allele_count = ac;
		this.variant_id = vid;
	}
	
	public Alternative (JSONObject alt, String vid){
		this.alternate_bases = alt.getString("Alternate_bases"); 
		this.allele_frequency = alt.getDouble("Allele_frequency"); 
		this.allele_frequency_SAS = alt.getDouble("Allele_frequency_SAS"); 
		this.allele_frequency_EUR = alt.getDouble("Allele_frequency_EUR"); 
		this.allele_frequency_AFR = alt.getDouble("Allele_frequency_AFR"); 
		this.allele_frequency_AMR = alt.getDouble("Allele_frequency_AMR");
		this.allele_frequency_EAS = alt.getDouble("Allele_frequency_EAS");
		this.allele_count = alt.getLong("Allele_count_in_gt");
		this.variant_id = vid;
	}
	
	/*
	 * Getter
	 */
	public String getAlternate_bases() {
		return alternate_bases;
	}
	public Double getAllele_frequency() {
		return allele_frequency;
	}
	public Double getAllele_frequency_SAS() {
		return allele_frequency_SAS;
	}
	public Double getAllele_frequency_EUR() {
		return allele_frequency_EUR;
	}
	public Double getAllele_frequency_AFR() {
		return allele_frequency_AFR;
	}
	public Double getAllele_frequency_AMR() {
		return allele_frequency_AMR;
	}
	public Double getAllele_frequency_EAS() {
		return allele_frequency_EAS;
	}
	public Long getAllele_count() {
		return allele_count;
	}
	public String getVariant_id() {
		return variant_id;
	}
	
	/*
	 * Setter
	 */
	
	public void setAlternate_bases(String alternate_bases) {
		this.alternate_bases = alternate_bases;
	}
	public void setAllele_frequency(Double allele_frequency) {
		this.allele_frequency = allele_frequency;
	}
	public void setAllele_frequency_SAS(Double allele_frequency_SAS) {
		this.allele_frequency_SAS = allele_frequency_SAS;
	}
	public void setAllele_frequency_EUR(Double allele_frequency_EUR) {
		this.allele_frequency_EUR = allele_frequency_EUR;
	}
	public void setAllele_frequency_AFR(Double allele_frequency_AFR) {
		this.allele_frequency_AFR = allele_frequency_AFR;
	}
	public void setAllele_frequency_AMR(Double allele_frequency_AMR) {
		this.allele_frequency_AMR = allele_frequency_AMR;
	}
	public void setAllele_frequency_EAS(Double allele_frequency_EAS) {
		this.allele_frequency_EAS = allele_frequency_EAS;
	}
	public void setAllele_count(Long allele_count) {
		this.allele_count = allele_count;
	}
	public void setVariant_id(String variant_id) {
		this.variant_id = variant_id;
	}

}
