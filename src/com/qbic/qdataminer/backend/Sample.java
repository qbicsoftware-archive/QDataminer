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

public class Sample {
	
	/*
	 * This class represents a sample (patient) of a certain alternative
	 */
	private String sampleID = "";
	private String familyID = "";
	private String gender = "";
	private String paternalID = "";
	private String maternalID = "";
	private String population = "";
	private String superPopulation = "";
	private String relationship = "";
	private String comments = "";
	
	//Relations (for now concatenated as string)
	private String children = "";
	private String siblings = "";
	private String secondOrders = "";
	private String thirdOrders = "";
	
	private String alternativeBases;
	private Alternative alternative;

	public Sample(JSONObject s, Alternative alt) {
		this.sampleID = s.getString("_id");
		this.familyID = s.optString("Family_ID");
		this.gender = s.optString("Gender");
		this.paternalID = s.optString("Paternal_ID");
		this.maternalID = s.optString("Maternal_ID");
		this.population = s.optString("Population");
		this.superPopulation = s.optString("Super_Population");
		this.relationship = s.optString("Relationship");
		this.comments = s.optString("Other_comments");
		
		//Relations
		if (s.has("Children")){
			this.children = s.getJSONArray("Children").join(";").replaceAll("\"", "");
		}
		if (s.has("Siblings")){
			this.siblings = s.getJSONArray("Siblings").join(";").replaceAll("\"", "");
		}
		if (s.has("Second_order")){
			this.secondOrders = s.getJSONArray("Second_order").join(";").replaceAll("\"", "");
		}	
		if (s.has("Third_order")){
			this.thirdOrders = s.getJSONArray("Third_order").join(";").replaceAll("\"", "");
		}
    	this.alternative = alt;
    	this.alternativeBases = alt.getAlternateBases();    
	}

	public String getSampleID() {
		return sampleID;
	}

	public String getFamilyID() {
		return familyID;
	}

	public String getGender() {
		return gender;
	}

	public String getPaternalID() {
		return paternalID;
	}

	public String getMaternalID() {
		return maternalID;
	}

	public String getPopulation() {
		return population;
	}

	public String getSuperPopulation() {
		return superPopulation;
	}

	public String getRelationship() {
		return relationship;
	}

	public String getComments() {
		return comments;
	}

	public String getChildren() {
		return children;
	}

	public String getSiblings() {
		return siblings;
	}

	public String getSecondOrders() {
		return secondOrders;
	}

	public String getThirdOrders() {
		return thirdOrders;
	}
	public Alternative getAlternative() {
		return alternative;
	}
	public String getAlternativeBases() {
		return alternativeBases;
	}
}
