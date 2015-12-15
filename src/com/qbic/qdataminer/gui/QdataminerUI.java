package com.qbic.qdataminer.gui;

import javax.servlet.annotation.WebServlet;

import com.qbic.qdataminer.backend.Alternative;
import com.qbic.qdataminer.backend.SnpeffAnnotation;
import com.qbic.qdataminer.backend.Variant;
import com.qbic.qdataminer.backend.Sample;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.shared.ui.grid.HeightMode;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.*;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.json.*;

@SuppressWarnings("serial")
@Theme("qdataminer")
public class QdataminerUI extends UI {
	
	Node esNode;
	Client esClient;

	@Override
	protected void init(VaadinRequest request) {
		
		//Connection to elasticsearch
		
		getElasticsearchConnection();
		
		GetResponse response = esClient.prepareGet("1000genomes_variants", "variants", "Y_2655180_G_A")
		        .execute()
		        .actionGet();
		Map<String, Object> result = response.getSource();
		
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
		addElementToSpecificBranch(variantInfoTree, var.getVariantTypesWithLabel(), "Details");
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

		// Iterate through the alternatives and build alternatives, snpeffannno containers
		Collection<Alternative> alternatives = new ArrayList<Alternative>();
		Collection<SnpeffAnnotation> snpeffAnnotations = new ArrayList<SnpeffAnnotation>();
		Collection<Sample> samplesA1 = new ArrayList<Sample>();
		Integer altCount = 0;
		Integer snpeffCount = 0;
		if (jsonResult.has("Alternates")){
			JSONArray jsonAlts = jsonResult.getJSONArray("Alternates");
			altCount = jsonAlts.length();
			for(int a = 0; a < jsonAlts.length(); a++) {
				JSONObject jsonAlt = jsonAlts.getJSONObject(a);
				Alternative alt = new Alternative(jsonAlt, var);
				alternatives.add(alt);
				
				// Parse annotations
				if (jsonAlt.has("SnpEff_annotation")){
					snpeffCount = 0;
					JSONArray jsonSnpeffAnnos = jsonAlt.getJSONArray("SnpEff_annotation");
					snpeffCount = jsonSnpeffAnnos.length();
					for(int s = 0; s < jsonSnpeffAnnos.length(); s++) {
						JSONObject jsonSnpeffAnno = jsonSnpeffAnnos.getJSONObject(s);
						snpeffAnnotations.add(new SnpeffAnnotation(jsonSnpeffAnno, alt)); 
					}
				}
				// Parse samples
				if (jsonAlt.has("Samples_Allele1")){
					JSONArray jsonSamplesA1 = jsonAlt.getJSONArray("Samples_Allele1");
					for(int a1 = 0; a1 < jsonSamplesA1.length(); a1++) {
						String sid = jsonSamplesA1.getString(a1);
						
						GetResponse samplequery = esClient.prepareGet("1000genomes_variants", "samples", sid)
						        .execute()
						        .actionGet();
						Map<String, Object> sampleResult = samplequery.getSource();	
						samplesA1.add(new Sample(new JSONObject(sampleResult), alt));
					}
				}
			}
		}
		//List<T> intersect = list1.stream().filter(list2::contains).collect(Collectors.toList());
		
		BeanItemContainer<Alternative> altBean = new BeanItemContainer<Alternative>(Alternative.class, alternatives);
		BeanItemContainer<SnpeffAnnotation> saBean = new BeanItemContainer<SnpeffAnnotation>(SnpeffAnnotation.class, snpeffAnnotations);
		BeanItemContainer<Sample> sampleA1Bean = new BeanItemContainer<Sample>(Sample.class, samplesA1);
		
		Grid altTable = new Grid(altBean);
		altTable.setCaption("Variant Alternatives (please choose one)");
		altTable.setHeightByRows(altCount);
		altTable.setHeightMode(HeightMode.ROW);
		altTable.setColumnOrder("alternateBases", "alleleCount");
		altTable.removeColumn("variant");
		altTable.setSelectionMode(SelectionMode.SINGLE);
		altTable.setWidth("100%");
		
	    Grid saTable = new Grid(saBean);
	    saTable.setVisible(false);
	    saTable.setCaption("SnpEff Annotations");
	    saTable.setHeightByRows(snpeffCount);
	    saTable.setHeightMode(HeightMode.ROW);
	    saTable.setColumnOrder("annotation","annotationImpact","mutation","geneName", "geneID", "featureType", "featureID", "transcriptBiotype");
	    saTable.getColumn("mutation").setWidth(100);
	    saTable.getColumn("featureID").setWidth(200);
	    saTable.removeColumn("alternativeBases");
	    saTable.removeColumn("alternative");
	    saTable.setSelectionMode(SelectionMode.NONE);
	    saTable.setWidth("100%");
	    saTable.getColumn("featureID").setRenderer(new HtmlRenderer());
	    saTable.getColumn("geneID").setRenderer(new HtmlRenderer());
	    
	    Grid sampleA1Table = new Grid(sampleA1Bean);
	    sampleA1Table.setCaption("Samples Allele 1");
	    sampleA1Table.setWidth("100%");
	    sampleA1Table.removeColumn("alternativeBases");
	    sampleA1Table.removeColumn("alternative");
	    sampleA1Table.setColumnOrder("sampleID","familyID","gender","population", "superPopulation",
	    						     "relationship", "maternalID", "paternalID", "children", "siblings", 
	    						     "secondOrders", "thirdOrders", "comments");
		
		TabSheet annoSampleTabs = new TabSheet();
		annoSampleTabs.setVisible(false);
		// This is shit... there must be another way
		annoSampleTabs.setHeight("1000px");
		/*
		 * Build layout
		 */
		final VerticalLayout resultMain = new VerticalLayout();
		resultMain.setMargin(true);
		setContent(resultMain);
			
			final HorizontalLayout InfoAltAnnoBox = new HorizontalLayout();
			
				InfoAltAnnoBox.setWidth("100%");
				InfoAltAnnoBox.addComponent(variantInfoTree);
				
				final VerticalLayout altSnpeffBox = new VerticalLayout();
				altSnpeffBox.setWidth("100%");
				InfoAltAnnoBox.addComponent(altSnpeffBox);
				altSnpeffBox.addComponent(altTable);
				altSnpeffBox.setExpandRatio(altTable,1);
				annoSampleTabs.addTab(saTable);
				annoSampleTabs.addTab(sampleA1Table);
				
				InfoAltAnnoBox.setExpandRatio(variantInfoTree, 2);
				InfoAltAnnoBox.setExpandRatio(altSnpeffBox, 8);
				
				altSnpeffBox.addComponent(annoSampleTabs);
			
			resultMain.addComponent(InfoAltAnnoBox);
		
		//Close node when finished
		
		esClient.close();
		esNode.close();
		
	
		altTable.addSelectionListener(selectionEvent -> {
			/*
			 * Display snpEff-annotations when an alternative is selected
			 */
		    Alternative selectedAlt = (Alternative)(((SingleSelectionModel) altTable.getSelectionModel()).getSelectedRow());	
		    
		    Filter altFilter = new SimpleStringFilter("alternativeBases", selectedAlt.getAlternateBases(), true, false);
		    saBean.removeAllContainerFilters();
		    saBean.addContainerFilter(altFilter);
		    sampleA1Bean.addContainerFilter(altFilter);
		    saTable.setVisible(true);
		    
			annoSampleTabs.setVisible(true);
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