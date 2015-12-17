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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		
		GetResponse response = esClient.prepareGet("1000genomes_variants", "variants", "21_45719908_G_A")
		        .execute()
		        .actionGet();
		Map<String, Object> result = response.getSource();
		
		JSONObject jsonResult = new JSONObject(result);
		Variant var = new Variant(jsonResult);
		
		/*
		 * Visualize resulting variant
		 */		
		final VerticalLayout resultVariantLayout = new VerticalLayout();
		
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

		// Iterate through the alternatives and build alternatives, snpeffannno, samples containers
		Collection<Alternative> alternatives = new ArrayList<Alternative>();
		Collection<SnpeffAnnotation> snpeffAnnotations = new ArrayList<SnpeffAnnotation>();
		Collection<Sample> samplesA1 = new ArrayList<Sample>();
		Collection<Sample> samplesA2 = new ArrayList<Sample>();
		
		Map<String, List<String>> intersectionPerAlt = new HashMap<String, List<String>>();
		if (jsonResult.has("Alternates")){
			JSONArray jsonAlts = jsonResult.getJSONArray("Alternates");
			
			// Loop over all alternatives
			for(int a = 0; a < jsonAlts.length(); a++) {
				JSONObject jsonAlt = jsonAlts.getJSONObject(a);
				Alternative alt = new Alternative(jsonAlt, var);
				alternatives.add(alt);
				
				// Parse annotations
				if (jsonAlt.has("SnpEff_annotation")){
					JSONArray jsonSnpeffAnnos = jsonAlt.getJSONArray("SnpEff_annotation");
					for(int s = 0; s < jsonSnpeffAnnos.length(); s++) {
						JSONObject jsonSnpeffAnno = jsonSnpeffAnnos.getJSONObject(s);
						snpeffAnnotations.add(new SnpeffAnnotation(jsonSnpeffAnno, alt)); 
					}
				}
				// Parse samples
				ArrayList<String> sidsA1 = new ArrayList<String>();
				if (jsonAlt.has("Samples_Allele1")){
					JSONArray jsonSamplesA1 = jsonAlt.getJSONArray("Samples_Allele1");
					for(int a1 = 0; a1 < jsonSamplesA1.length(); a1++) {
						String sid = jsonSamplesA1.getString(a1);
						sidsA1.add(sid);
						
						// Query the samples
						GetResponse samplequeryA1 = esClient.prepareGet("1000genomes_variants", "samples", sid)
						        .execute()
						        .actionGet();
						Map<String, Object> sampleResultA1 = samplequeryA1.getSource();
						sampleResultA1.put("_id", samplequeryA1.getId());
						samplesA1.add(new Sample(new JSONObject(sampleResultA1), alt));
					}
				}
				ArrayList<String> sidsA2 = new ArrayList<String>();
				if (jsonAlt.has("Samples_Allele2")){
					JSONArray jsonSamplesA2 = jsonAlt.getJSONArray("Samples_Allele2");
					for(int a2 = 0; a2 < jsonSamplesA2.length(); a2++) {
						String sid = jsonSamplesA2.getString(a2);
						sidsA2.add(sid);
						
						// Query the samples
						GetResponse samplequeryA2 = esClient.prepareGet("1000genomes_variants", "samples", sid)
						        .execute()
						        .actionGet();
						Map<String, Object> sampleResultA2 = samplequeryA2.getSource();
						sampleResultA2.put("_id", samplequeryA2.getId());
						samplesA2.add(new Sample(new JSONObject(sampleResultA2), alt));
					}
				
				//Intersection of sample IDs for 3rd tab
				List<String> intersect = sidsA1.stream().filter(sidsA2::contains).collect(Collectors.toList());
				intersectionPerAlt.put(alt.getAlternateBases(), intersect);
				}
			}
		}
		
		BeanItemContainer<Alternative> altBean = new BeanItemContainer<Alternative>(Alternative.class, alternatives);
		BeanItemContainer<SnpeffAnnotation> saBean = new BeanItemContainer<SnpeffAnnotation>(SnpeffAnnotation.class, snpeffAnnotations);
		BeanItemContainer<Sample> sampleA1Bean = new BeanItemContainer<Sample>(Sample.class, samplesA1);
		BeanItemContainer<Sample> sampleA2Bean = new BeanItemContainer<Sample>(Sample.class, samplesA2);
		
		
		Grid altTable = new Grid(altBean);
		altTable.setCaption("Variant Alternatives (please choose one)");
		altTable.setHeightByRows(altBean.size());
		altTable.setHeightMode(HeightMode.ROW);
		altTable.setColumnOrder("alternateBases", "alleleCount");
		altTable.removeColumn("variant");
		altTable.setSelectionMode(SelectionMode.SINGLE);
		altTable.setWidth("100%");
		altTable.setImmediate(true);
		
	    Grid saTable = new Grid(saBean);
	    saTable.setVisible(false);
	    saTable.setCaption("SnpEff Annotations");
	    if (saBean.size() <= 10){
		    saTable.setHeightByRows(saBean.size());
		    saTable.setHeightMode(HeightMode.ROW);	    	
	    }
	    saTable.setColumnOrder("annotation","annotationImpact","mutation","geneName", "geneID", "featureType", "featureID", "transcriptBiotype");
	    saTable.getColumn("mutation").setWidth(100);
	    saTable.getColumn("featureID").setWidth(200);
	    saTable.removeColumn("alternativeBases");
	    saTable.removeColumn("alternative");
	    saTable.setSelectionMode(SelectionMode.NONE);
	    saTable.setWidth("100%");
	    saTable.getColumn("featureID").setRenderer(new HtmlRenderer());
	    saTable.getColumn("geneID").setRenderer(new HtmlRenderer());
	    
	    Grid sampleA1Table = new Grid();
	    if (sampleA1Bean.size() != 0){
	    	formatSampleGrids(sampleA1Table, sampleA1Bean);
		    sampleA1Table.setCaption("Samples (1st Allele)");
	    }
	    
	    
	    Grid sampleA2Table = new Grid();
	    if (sampleA2Bean.size() != 0){
	    	formatSampleGrids(sampleA2Table, sampleA2Bean);
		    sampleA2Table.setCaption("Samples (2nd Allele)");
	    }
		
		TabSheet annoSampleTabs = new TabSheet();
		annoSampleTabs.setVisible(false);
		// This is shit... there must be another way
		annoSampleTabs.setHeight("600px");
		/*
		 * Build layout
		 */
		
		resultVariantLayout.setMargin(true);
		setContent(resultVariantLayout);
			
			final HorizontalLayout InfoAltAnnoBox = new HorizontalLayout();
			
				InfoAltAnnoBox.setWidth("100%");
				InfoAltAnnoBox.addComponent(variantInfoTree);
				
				final VerticalLayout altSnpeffBox = new VerticalLayout();
				altSnpeffBox.setWidth("100%");
				InfoAltAnnoBox.addComponent(altSnpeffBox);
				altSnpeffBox.addComponent(altTable);
				altSnpeffBox.setExpandRatio(altTable,1);
				annoSampleTabs.addTab(saTable);
				if (sampleA1Bean.size() != 0){
					annoSampleTabs.addTab(sampleA1Table);	
				}
				if (sampleA2Bean.size() != 0){
					annoSampleTabs.addTab(sampleA2Table);	
				}
				InfoAltAnnoBox.setExpandRatio(variantInfoTree, 2);
				InfoAltAnnoBox.setExpandRatio(altSnpeffBox, 8);
				
				altSnpeffBox.addComponent(annoSampleTabs);
			
			resultVariantLayout.addComponent(InfoAltAnnoBox);
		
		//Close node when finished
		closeElasticsearchConnection();
		
		/*
		 * Listeners
		 */
		altTable.addSelectionListener(selectionEvent -> {
			/*
			 * Display snpEff-annotations when an alternative is selected
			 * ==========================================================
			 */
			altTable.markAsDirty();
		    Alternative selectedAlt = (Alternative)(((SingleSelectionModel) altTable.getSelectionModel()).getSelectedRow());
		    
		    Filter altFilter = new SimpleStringFilter("alternativeBases", selectedAlt.getAlternateBases(), true, false);
		    saBean.removeAllContainerFilters();
		    saBean.addContainerFilter(altFilter);
		    sampleA1Bean.addContainerFilter(altFilter);
		    sampleA2Bean.addContainerFilter(altFilter);
		    saTable.setVisible(true);
		   
		    // Generate the intersection bean of allele1 and allele2
		    // =====================================================
		    
		    Collection<Sample> samplesIntersect = new ArrayList<Sample>();
		    for(Iterator<String> sids = intersectionPerAlt.get(selectedAlt.getAlternateBases()).iterator(); sids.hasNext();){
		    	String sid = sids.next();				
		    	Boolean found = false;
				for(Iterator<Sample> s = samplesA1.iterator(); s.hasNext();){
					Sample sample = s.next();
					if (sample.getSampleID().equals(sid)){
						samplesIntersect.add(sample);
						found = true;
					}
				}
				if (!found){
					for(Iterator<Sample> s = samplesA2.iterator(); s.hasNext();){
						Sample sample = s.next();
						if (sample.getSampleID().equals(sid)){
							samplesIntersect.add(sample);
						}
					}					
				}
		    }
		    BeanItemContainer<Sample> sampleIntersectBean = new BeanItemContainer<Sample>(Sample.class, samplesIntersect);
		    
		    Grid sampleIntersectTable = new Grid();
		    if (sampleIntersectBean.size() != 0){
		    	formatSampleGrids(sampleIntersectTable, sampleIntersectBean);
		    	sampleIntersectTable.setCaption("Samples (homogenous)");
				annoSampleTabs.addTab(sampleIntersectTable);
		    }
		    
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
    
    private void closeElasticsearchConnection() {
    	/*
    	 * Closes the connection to elasticsearch
    	 */
		esClient.close();
		esNode.close();
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
    
    private void formatSampleGrids(Grid g, BeanItemContainer<Sample> s){
    	/*
    	 * Helper function for sample table formatting
    	 */
	    g.setContainerDataSource(s);
	    g.setWidth("100%");
	    g.removeColumn("alternativeBases");
	    g.removeColumn("alternative");
	    g.setColumnOrder("sampleID","familyID","gender","population", "superPopulation",
	    						     "relationship", "maternalID", "paternalID", "children", "siblings", 
	    						     "secondOrders", "thirdOrders", "comments");
	    if (s.size() <= 10){
	    	g.setHeightByRows(s.size());
	    	g.setHeightMode(HeightMode.ROW);		    	
	    }
    }
	
	
    //  Deployed as a Servlet or Portlet.
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = QdataminerUI.class)
	public static class Servlet extends VaadinServlet {
	}

}