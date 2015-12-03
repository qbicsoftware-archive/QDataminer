package com.example.qdataminer;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.shared.ui.grid.HeightMode;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.*;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.*;

import java.util.Arrays;
import java.util.Iterator;
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
		
		GetResponse response = esClient.prepareGet("1000genomes_variants", "variants", "Y_14553755_C_G")
		        .execute()
		        .actionGet();
		Map<String, Object> result = response.getSource();
		JSONObject json_result = new JSONObject(result);
		
		/*
		 * Visualize resulting variant
		 */
		String variant_id = json_result.getString("_id");
		String chromosome = json_result.getString("Chromosome");
		Long position = json_result.getLong("Position");
		String ext_id = json_result.getString("ID");
		String ref_base = json_result.getString("Reference_bases");
		
		Table variant_info_table = new Table("Additional Information");
		variant_info_table.addContainerProperty("Field", String.class, null);
		variant_info_table.addContainerProperty("Value",  String.class, null);
		
		// Iterate dynamically over the root level of the result JSON
		int i = 1;
		String[] exclude = {"Alternates", "_id", "Chromosome", "Position", "ID", "Reference_bases"}; 
		for (Iterator<String> it = json_result.keySet().iterator(); it.hasNext();){
			String key = (String) it.next();
			if (!Arrays.asList(exclude).contains(key)){
				variant_info_table.addItem(new Object[]{key, json_result.get(key).toString()}, i);
				i++;
			}
		}
		variant_info_table.setPageLength(variant_info_table.size());
		variant_info_table.setSortContainerPropertyId("Field");
		
		Grid alt_table = new Grid("Alternatives");
		alt_table.addColumn("Alternate bases", String.class);
		alt_table.addColumn("Allele frequency (AF)",  Double.class);
		alt_table.addColumn("AF SAS",  Double.class);
		alt_table.addColumn("AF EUR",  Double.class);
		alt_table.addColumn("AF AFR",  Double.class);
		alt_table.addColumn("AF AMR",  Double.class);
		alt_table.addColumn("AF EAS",  Double.class);
		alt_table.addColumn("Allele count",  Long.class);

		// Iterate through the alternatives
		if (json_result.has("Alternates")){
			JSONArray json_alts = json_result.getJSONArray("Alternates");
			for(int a = 0; a < json_alts.length(); a++) {
				JSONObject alt = json_alts.getJSONObject(a);
				alt_table.addRow(alt.getString("Alternate_bases"), 
						         alt.getDouble("Allele_frequency"), 
						         alt.getDouble("Allele_frequency_SAS"), 
						         alt.getDouble("Allele_frequency_EUR"), 
						         alt.getDouble("Allele_frequency_AFR"), 
						         alt.getDouble("Allele_frequency_AMR"),
						         alt.getDouble("Allele_frequency_EAS"),
						         alt.getLong("Allele_count_in_gt"));
			}
			alt_table.setHeightByRows(json_alts.length());
			alt_table.setHeightMode(HeightMode.ROW);
		}
		
		final VerticalLayout result_main = new VerticalLayout();
			result_main.setMargin(true);
			setContent(result_main);
			
			final VerticalLayout variant_basis = new VerticalLayout();
				variant_basis.addComponent(new Label("Variant ID: " + variant_id));
				variant_basis.addComponent(new Label("Chromosome: " + chromosome));
				variant_basis.addComponent(new Label("Position: " + position));
				variant_basis.addComponent(new Label("External ID: " + ext_id));
				variant_basis.addComponent(new Label("Reference base: " + ref_base));
				//variant_basis.setMargin(true);
			
			final HorizontalLayout variant_info = new HorizontalLayout();
				variant_info.setWidth("90%");
				variant_info.addComponent(variant_basis);
				variant_info.addComponent(variant_info_table);
				variant_info.addComponent(alt_table);
				variant_info.setCaption("Variant Details");
			
			result_main.addComponent(variant_info);
		
		//Close node when finished
		esClient.close();
		esNode.close();
	}
	
    private void getElasticsearchConnection() {
        Settings settings = ImmutableSettings.settingsBuilder()
		.put("http.enabled", "false")
		.put("transport.tcp.port", "9306-9400")
		.put("discovery.zen.ping.multicast.enabled", "false")
		.putArray("discovery.zen.ping.unicast.hosts", "localhost:9300", "localhost:9301", "localhost:9302", "localhost:9303", "localhost:9304", "localhost:9305").build();

		esNode = nodeBuilder().client(true).settings(settings).clusterName("qsearch").node();
		esClient = esNode.client();
    }
	
	
    //  Deployed as a Servlet or Portlet.
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = QdataminerUI.class)
	public static class Servlet extends VaadinServlet {
	}

}