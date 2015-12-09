package com.example.qdataminer;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import backend.Alternative;
import backend.Variant;

import com.vaadin.shared.ui.grid.HeightMode;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.*;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.*;

import java.util.ArrayList;
import java.util.Collection;
import org.json.*;

@SuppressWarnings("serial")
@Theme("qdataminer")
public class QdataminerUI extends UI {
	
	Node esNode;
	Client esClient;

	@Override
	protected void init(VaadinRequest request) {
		
		//Connection to elasticsearch
		/*
		getElasticsearchConnection();
		
		GetResponse response = esClient.prepareGet("1000genomes_variants", "variants", "Y_14553755_C_G")
		        .execute()
		        .actionGet();
		Map<String, Object> result = response.getSource();
		*/
		String result = "{\"_id\":\"Y_2655180_G_A\",\"Total_nr_of_alleles\":1233,\"Number_of_samples\":1233,\"Exon_Pulldown_Target\":true,\"Reference_bases\":\"G\",\"Ancestral_allele\":\"G\",\"Source\":\"1000genomes\",\"Variant_type\":[\"SNP\"],\"Depth_across_samples\":84761,\"Position\":2655180,\"Quality\":100,\"ID\":\"rs11575897\",\"Chromosome\":\"Y\",\"Alternates\":[{\"Samples_Allele1\":[\"HG00530\",\"HG00533\",\"NA18561\",\"NA18563\",\"NA18943\",\"NA18953\",\"NA18962\",\"NA18965\",\"NA18977\",\"NA18985\",\"NA18986\",\"NA18990\",\"NA18994\",\"NA19000\",\"NA19005\",\"NA19007\",\"NA19060\",\"NA19066\",\"NA19067\",\"NA19070\",\"NA19075\",\"NA19088\"],\"Allele_frequency_AFR\":0,\"Allele_frequency_EUR\":0,\"Alternate_bases\":\"A\",\"Allele_count_in_gt\":22,\"SnpEff_annotation\":[{\"CDS_pos_CDS_length\":\"465/615\",\"Mutation\":\"SRY S155S\",\"Feature_Type\":\"transcript\",\"Transcript_BioType\":\"protein_coding\",\"Annotation_Impact\":\"LOW\",\"Gene_ID\":\"ENSG00000184895\",\"HGVS_p\":\"p.Ser155Ser\",\"Feature_ID\":\"ENST00000383070\",\"Rank\":\"1/1\",\"cDNA_pos_cDNA_length\":\"561/845\",\"AA_pos_AA_length\":\"155/204\",\"Annotation\":\"synonymous_variant\",\"Gene_Name\":\"SRY\",\"HGVS_c\":\"c.465C>T\"},{\"CDS_pos_CDS_length\":\"465/591\",\"Mutation\":\"SRY S155S\",\"Feature_Type\":\"transcript\",\"Transcript_BioType\":\"protein_coding\",\"Annotation_Impact\":\"LOW\",\"Gene_ID\":\"ENSG00000184895\",\"HGVS_p\":\"p.Ser155Ser\",\"Feature_ID\":\"ENST00000525526\",\"Rank\":\"1/2\",\"ERRORS_WARNINGS_INFO\":\"WARNING_TRANSCRIPT_NO_STOP_CODON\",\"cDNA_pos_cDNA_length\":\"465/591\",\"AA_pos_AA_length\":\"155/196\",\"Annotation\":\"synonymous_variant\",\"Gene_Name\":\"SRY\",\"HGVS_c\":\"c.465C>T\"},{\"CDS_pos_CDS_length\":\"465/498\",\"Mutation\":\"SRY S155S\",\"Feature_Type\":\"transcript\",\"Transcript_BioType\":\"protein_coding\",\"Annotation_Impact\":\"LOW\",\"Gene_ID\":\"ENSG00000184895\",\"HGVS_p\":\"p.Ser155Ser\",\"Feature_ID\":\"ENST00000534739\",\"Rank\":\"1/2\",\"cDNA_pos_cDNA_length\":\"465/498\",\"AA_pos_AA_length\":\"155/165\",\"Annotation\":\"synonymous_variant\",\"Gene_Name\":\"SRY\",\"HGVS_c\":\"c.465C>T\"},{\"Feature_Type\":\"region_of_interest:Necessary_for_interaction_with_ZNF208_isoform_KRAB-O\",\"Transcript_BioType\":\"protein_coding\",\"Annotation_Impact\":\"LOW\",\"Gene_ID\":\"ENSG00000184895\",\"Feature_ID\":\"ENST00000383070\",\"HGVS_c\":\"c.465C>T\",\"Annotation\":\"sequence_feature\",\"Gene_Name\":\"SRY\"},{\"Distance\":\"2688\",\"Feature_Type\":\"transcript\",\"Transcript_BioType\":\"processed_pseudogene\",\"Annotation_Impact\":\"MODIFIER\",\"Gene_ID\":\"ENSG00000237659\",\"Feature_ID\":\"ENST00000454281\",\"HGVS_c\":\"n.-2688G>A\",\"Annotation\":\"upstream_gene_variant\",\"Gene_Name\":\"RNASEH2CP1\"},{\"Distance\":\"2286\",\"Feature_Type\":\"transcript\",\"Transcript_BioType\":\"snRNA\",\"Annotation_Impact\":\"MODIFIER\",\"Gene_ID\":\"ENSG00000251841\",\"Feature_ID\":\"ENST00000516032\",\"HGVS_c\":\"n.*2286G>A\",\"Annotation\":\"downstream_gene_variant\",\"Gene_Name\":\"RNU6-1334P\"}],\"Allele_frequency_EAS\":0.0902,\"Allele_frequency_AMR\":0,\"Allele_frequency\":0.0178427,\"Allele_frequency_SAS\":0}]}";
		JSONObject jsonResult = new JSONObject(result);
		Variant var = new Variant(jsonResult);
		
		/*
		 * Visualize resulting variant
		 */		
		Tree variantInfoTree = new Tree("Variant Data");
		variantInfoTree.addItem("Details");
		variantInfoTree.setSelectable(false);
		addElementToSpecificBranch(variantInfoTree, var.getVariantIDWithLabel(), "Details");
		addElementToSpecificBranch(variantInfoTree, var.getChromosomeWithLabel(), "Details");
		addElementToSpecificBranch(variantInfoTree, var.getPositionWithLabel(), "Details");
		addElementToSpecificBranch(variantInfoTree, var.getExternalIDWithLabel(), "Details");
		addElementToSpecificBranch(variantInfoTree, var.getReferenceBasesWithLabel(), "Details");
		variantInfoTree.expandItem("Details");
		
		variantInfoTree.addItem("Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getQualityWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getFilterWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getAncestralAlleleWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getTotalNrOfAllelesWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getNumberOfSamplesWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getDepthAcrossSamplesWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getTargetSiteDublicationWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getStructuralVariantTypeWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getStructuralVariantLengthWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getMergedCallsWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getSourceCallsetWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getEndCoordinateVariantWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getExonPulldownTargetWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getMultiAllelicWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getImperciseStruturalVariationWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getCofIntervPosImperciseWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getCofIntervEndImperciseWithLabel(), "Additional Information");
		addElementToSpecificBranch(variantInfoTree, var.getSourceWithLabel(), "Additional Information");

		// Iterate through the alternatives and build container
		Collection<Alternative> alternatives = new ArrayList<Alternative>();
		Integer alt_count = 0;
		if (jsonResult.has("Alternates")){
			JSONArray json_alts = jsonResult.getJSONArray("Alternates");
			alt_count = json_alts.length();
			for(int a = 0; a < json_alts.length(); a++) {
				JSONObject alt = json_alts.getJSONObject(a);
				alternatives.add(new Alternative(alt, var));
			}
		}
		
		BeanItemContainer<Alternative> alt_bean = new BeanItemContainer<Alternative>(Alternative.class, alternatives);
		Grid altTable = new Grid(alt_bean);
		altTable.setCaption("Variant Alternatives");
		altTable.setHeightByRows(alt_count);
		altTable.setHeightMode(HeightMode.ROW);
		altTable.setColumnOrder("alternateBases", "alleleCount");
		altTable.removeColumn("variant");
		altTable.setSelectionMode(SelectionMode.SINGLE);
		altTable.setWidth("100%");
		
		/*
		 * Build layout
		 */
		final VerticalLayout resultMain = new VerticalLayout();
		resultMain.setMargin(true);
		setContent(resultMain);
			
			final HorizontalLayout variantInfoAltBox = new HorizontalLayout();
				variantInfoAltBox.setWidth("95%");
				variantInfoAltBox.addComponent(variantInfoTree);
				variantInfoAltBox.addComponent(altTable);
				variantInfoAltBox.setExpandRatio(altTable,1);
			
			resultMain.addComponent(variantInfoAltBox);
		
		//Close node when finished
		/*
		esClient.close();
		esNode.close();
		*/
	
		altTable.addSelectionListener(selectionEvent -> {
			/*
			 * Display snpEff-annotations when an alternative is selected
			 */
		    Alternative selectedAlt = (Alternative)(((SingleSelectionModel) altTable.getSelectionModel()).getSelectedRow());	
		    resultMain.addComponent(new Label(selectedAlt.getAlternateBases()));
		});
	
	}
	
    private void getElasticsearchConnection() {
    	/*
    	 * Implements the connection to elasticsearch
    	 */
        Settings settings = ImmutableSettings.settingsBuilder()
		.put("http.enabled", "false")
		.put("transport.tcp.port", "9306-9400")
		.put("discovery.zen.ping.multicast.enabled", "false")
		.putArray("discovery.zen.ping.unicast.hosts", "localhost:9300", "localhost:9301", "localhost:9302", "localhost:9303", "localhost:9304", "localhost:9305").build();

		esNode = nodeBuilder().client(true).settings(settings).clusterName("qsearch").node();
		esClient = esNode.client();
    }
    
    private void addElementToSpecificBranch(Tree tree, String element, String branch) {
    	/*
    	 * Helper function to fill the variant tree
    	 */
    	if (element.indexOf("null") == -1){
        	tree.addItem(element);
        	tree.setParent(element, branch);
        	tree.setChildrenAllowed(element, false);   		
    	}
    }
	
	
    //  Deployed as a Servlet or Portlet.
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = QdataminerUI.class)
	public static class Servlet extends VaadinServlet {
	}

}