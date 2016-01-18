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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import static org.elasticsearch.index.query.QueryBuilders.*;
import org.json.*;

import java.util.*;

@SuppressWarnings("serial")
public class SearchForm extends FormLayout{
	
	// Variant specific search fields
	private ListSelect chromosome = new ListSelect("Chromosome:");
	private TextField posFrom = new TextField("Position from:");
	private TextField posTo = new TextField("Position to:");
	private TextField mutation = new TextField("Mutation:");
	
    private Button search = new Button("Search", this::search);
    private Button clear = new Button("Clear", this::clear);
    
    private Grid searchResultTable = new Grid();
    
	private Client esClient;

	public SearchForm() {

        setSpacing(true);
        setSizeFull();
        setMargin(true);
        chromosome.addItems("1","2","3","4","5","6","7","8","9","10","11","12",
        		            "13","14","15","16","17","18","19","20","21","22","X","Y");
        chromosome.setRows(1);
        
        // Add the fields
        addComponent(chromosome);
        addComponent(posFrom);
        addComponent(posTo);
        addComponent(mutation);
        
        search.setStyleName(ValoTheme.BUTTON_PRIMARY);
        search.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        
        HorizontalLayout actions = new HorizontalLayout(search, clear);
        
        searchResultTable.setVisible(false);
        
        addComponent(actions);
	}
	
    public void search(Button.ClickEvent event) {
    	
    	getElasticsearchConnection();
    	
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
    	
    	SearchResponse searchRequest = esClient.prepareSearch("1000genomes_variants")
    	        .setTypes("variants")
    	        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		    	.setQuery(bq)
		    	.setFrom(0).setSize(10)
		        .execute()
		        .actionGet();
    	
    	Long found = searchRequest.getHits().getTotalHits();
    	String resultCaption = "Found " + found + " variants (make your selection)";
    	
    	Collection<Variant> queriedVariants = new ArrayList<Variant>();
    	for (SearchHit hit : searchRequest.getHits().getHits()){
    		Map<String, Object> result = hit.getSource();
    		if (!result.containsKey("_id")){
    			result.put("_id", hit.getId());
    		}
    		JSONObject jsonResult = new JSONObject(result);
    		Variant var = new Variant(jsonResult);
    		queriedVariants.add(var);
    	}
    	BeanItemContainer<Variant> qVarBean = new BeanItemContainer<Variant>(Variant.class, queriedVariants);
    	
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
    	
		//Close node when finished
		closeElasticsearchConnection();	
    }

    public void clear(Button.ClickEvent event) {
        // Remove entries from form 
       	chromosome.clear();
       	posFrom.clear();
       	posTo.clear();
       	mutation.clear();
    }
	

    @Override
    public QdataminerUI getUI() {
        return (QdataminerUI) super.getUI();
    }
    
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

}
