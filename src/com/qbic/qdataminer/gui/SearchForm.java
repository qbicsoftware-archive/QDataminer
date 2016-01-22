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
package com.qbic.qdataminer.gui;

import com.qbic.qdataminer.backend.Variant;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import static java.lang.Math.toIntExact;

import static org.elasticsearch.index.query.QueryBuilders.*;
import org.json.*;

import java.util.*;

@SuppressWarnings("serial")
public class SearchForm extends FormLayout{
	
	// Variant specific search fields
	private Label formHeading = new Label("Search for variants with:");
	private ListSelect chromosome = new ListSelect("Chromosome:");
	private TextField posFrom = new TextField("Position from:");
	private TextField posTo = new TextField("Position to:");
	private TextField mutation = new TextField("Mutation:");
	
    private Button search = new Button("Search", this::search);
    private Button clear = new Button("Clear", this::clear);
    
    private Grid searchResultTable = new Grid();
    private ArrayList<JSONObject> jsonResults;
    private Label notFound = new Label("No variants could be found");
    
	private Client esClient;

	public SearchForm() {

        setSpacing(true);
        setSizeFull();
        setMargin(true);
        chromosome.addItems("1","2","3","4","5","6","7","8","9","10","11","12",
        		            "13","14","15","16","17","18","19","20","21","22","X","Y");
        chromosome.setRows(1);
        formHeading.setStyleName("customh2");
        
        // Add the fields
        addComponent(formHeading);
        addComponent(chromosome);
        addComponent(posFrom);
        addComponent(posTo);
        addComponent(mutation);
        
        search.setStyleName(ValoTheme.BUTTON_PRIMARY);
        search.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        
        HorizontalLayout actions = new HorizontalLayout(search, clear);
        
        searchResultTable.setVisible(false);
        notFound.setVisible(false);
        
        addComponent(actions);
        addComponent(notFound);
	}
	
    public void search(Button.ClickEvent event) {
    	
    	getElasticsearchConnection();
    	notFound.setVisible(false);
    	
    	searchResultTable.setSelectionMode(SelectionMode.NONE);
    	
    	jsonResults = new ArrayList<JSONObject>();
    	
    	BoolQueryBuilder bq = QueryBuilders.boolQuery();
 
    	// Build the query dynamically depending on the choices in the GUI
    	if (chromosome.getValue() != null){
    		 bq.must(matchQuery("Chromosome", chromosome.getValue()));
    	}
    	if (!posFrom.getValue().equals("")){
    		Integer posFromHelp = Integer.parseInt(posFrom.getValue());
    		bq.must(rangeQuery("Position").from(posFromHelp));
    	}
    	if (!posTo.getValue().equals("")){
    		Integer posToHelp = Integer.parseInt(posTo.getValue());	
    		bq.must(rangeQuery("Position").to(posToHelp));
    	}
    	String mut = mutation.getValue();
    	if (!mut.equals("")){
    		if (!mut.contains("*")){
    			bq.must(termQuery("variants.Alternates.SnpEff_annotation.Mutation.raw", mut));
    		}else{
    			bq.must(wildcardQuery("variants.Alternates.SnpEff_annotation.Mutation.raw", mut));
    		}
    	}
    	
    	CountResponse countRequest = esClient.prepareCount("1000genomes_variants")
    	        .setTypes("variants")
		    	.setQuery(bq)
		        .execute()
		        .actionGet();
    	Integer found = toIntExact(countRequest.getCount());
    	
    	Integer upperBorder;
    	
    	String resultCaption;
    	if (found <= 100){
    		upperBorder = found;
    		resultCaption = "Found " + found + " variants. Please make your selection.";
    	}else{
    		upperBorder = 100;
    		resultCaption = "Found " + found + " variants(shown " + upperBorder +"). Please make your selection or restrict the parameters.";
    	}
    	
    	SearchResponse searchRequest = esClient.prepareSearch("1000genomes_variants")
    	        .setTypes("variants")
    	        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		    	.setQuery(bq)
		    	.setFrom(0).setSize(upperBorder)
		    	//.addSort("Position", SortOrder.ASC)
		        .execute()
		        .actionGet(); 
    	
    	Collection<Variant> queriedVariants = new ArrayList<Variant>();
    	for (SearchHit hit : searchRequest.getHits().getHits()){
    		Map<String, Object> result = hit.getSource();
    		if (!result.containsKey("_id")){
    			result.put("_id", hit.getId());
    		}
    		JSONObject jsonResult = new JSONObject(result);
    		
    		//Save Search results for later use
    		jsonResults.add(jsonResult);
    		
    		Variant var = new Variant(jsonResult);
    		queriedVariants.add(var);
    	}
    	BeanItemContainer<Variant> qVarBean = new BeanItemContainer<Variant>(Variant.class, queriedVariants);
    	
    	if (qVarBean.size() > 0){
        	searchResultTable.setContainerDataSource(qVarBean);
        	searchResultTable.setColumns("chromosome","position","referenceBases");
    	    if (qVarBean.size() < 8){
    	    	searchResultTable.setHeightByRows(qVarBean.size());
    	    	searchResultTable.setHeightMode(HeightMode.ROW);
    	    }else{
    	    	searchResultTable.setHeightMode(HeightMode.ROW);
    	    	searchResultTable.setHeightByRows(8);
    	    }
    	    searchResultTable.setSelectionMode(SelectionMode.SINGLE);
    	    searchResultTable.setCaption(resultCaption);
    	    searchResultTable.setVisible(true);	
    	}else{
    		// No finds -> Remove previous search results
    		notFound.setVisible(true);
    		searchResultTable.setVisible(false);
    		getUI().clearOnEmptySearch();
    	}
    	
		//Close node when finished
		closeElasticsearchConnection();	
    }

    public void clear(Button.ClickEvent event) {
        // Remove entries from form 
       	chromosome.clear();
       	posFrom.clear();
       	posTo.clear();
       	mutation.clear();
       	searchResultTable.setVisible(false);
       	getUI().clearOnEmptySearch();
    }
	

    @Override
    public QdataminerUI getUI() {
        return (QdataminerUI) super.getUI();
    }
    
    @SuppressWarnings("resource")
	private void getElasticsearchConnection() {
    	/*
    	 * Implements the connection to elasticsearch
    	 */
    	Settings settings = ImmutableSettings.settingsBuilder()
    	        .put("cluster.name", "qsearch").build();
    	this.esClient = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }
    
    private void closeElasticsearchConnection() {
    	/*
    	 * Closes the connection to elasticsearch
    	 */
		this.esClient.close();
    }
    
    /*
     * Getter
     */
    
	public Grid getSearchResultTable() {
		return searchResultTable;
	}
	public ArrayList<JSONObject> getJsonResults() {
		return jsonResults;
	}

}
