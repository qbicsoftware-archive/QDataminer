package com.example.qdataminer;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.*;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.*;

@SuppressWarnings("serial")
@Theme("qdataminer")
public class QdataminerUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = QdataminerUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		//Connection to elasticsearch
		Node node =
			    nodeBuilder()
			        .settings(ImmutableSettings.settingsBuilder().put("http.enabled", false))
			        .client(true)
			    .clusterName("qsearch")    
			    .node();

		Client client = node.client();
		
		GetResponse response = client.prepareGet("1000genomes_variants", "variants", "Y_14553755_C_G")
		        .execute()
		        .actionGet();
		String result = response.toString();
		layout.addComponent(new Label(result));

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		layout.addComponent(button);
		
		//Close node when finished
		node.close();
	}

}