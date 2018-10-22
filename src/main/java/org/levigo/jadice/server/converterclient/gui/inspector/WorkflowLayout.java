package org.levigo.jadice.server.converterclient.gui.inspector;

import org.jadice.util.base.Objects;

import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.Layout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.FruchtermanReingoldLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.util.ColorLib;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.VisualItem;

public class WorkflowLayout extends ActionList {

	public enum Type {
		FORCE_DIRECTED("Force directed"), //
		HIERARCHICAL("Hierarchical"), //
		CIRCLE("Circle"), //
		FRUCHTERMAN_REINGOLD("Fruchterman Reingold"), //
		RANDOM("Random"); //

		private final String description;

		Type(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	private static class EdgeColorAction_Fill extends ColorAction {
		public EdgeColorAction_Fill() {
			super(WorkflowDisplay.EDGES, VisualItem.FILLCOLOR);
		}

		public int getColor(VisualItem item) {
			return ColorLib.gray(100);
		}
	}

	private static class EdgeColorAction_Stroke extends ColorAction {
		public EdgeColorAction_Stroke() {
			super(WorkflowDisplay.EDGES, VisualItem.STROKECOLOR);
		}

		public int getColor(VisualItem item) {
			return ColorLib.gray(100);
		}
	}

	private static class NodeColorAction extends ColorAction {
		public NodeColorAction() {
			super(WorkflowDisplay.NODES, //
					VisualItem.FILLCOLOR, //
					ColorLib.rgb(190,255,190));
			add(VisualItem.HIGHLIGHT, ColorLib.rgb( 27,230, 27));
		}
	}

	private Layout layout;
	
	private final Visualization visualization;

	public WorkflowLayout(Visualization visualization) {
		this.visualization = visualization;
		this.add(new QualityControlAnimator());
		this.add(new ColorAction(WorkflowDisplay.NODES, //
				VisualItem.TEXTCOLOR, //
				ColorLib.rgb(0, 0, 0)));
		this.add(new NodeColorAction());
		this.add(new EdgeColorAction_Stroke());
		this.add(new EdgeColorAction_Fill());
		this.add(new RepaintAction());
		this.setType(Type.values()[0]);

	}

	public boolean isAnimationEnabled() {
		return layout.isEnabled();
	}

	public void setAnimationEnabled(boolean animationEnabled) {
		this.layout.setEnabled(animationEnabled);
	}
	
	Type type = null;

	public void setType(Type type) {
		if (type == this.type) {
			return;
		}
		Objects.assertNotNull("type", type);
		
		Layout newLayout = null;
		switch (type) {
		case RANDOM:
			super.setDuration(DEFAULT_STEP_TIME);
			newLayout = new RandomLayout(WorkflowDisplay.GRAPH);
			break;
		case FRUCHTERMAN_REINGOLD:
			super.setDuration(DEFAULT_STEP_TIME);
			newLayout = new FruchtermanReingoldLayout(WorkflowDisplay.GRAPH);
			break;
		case CIRCLE:
			super.setDuration(DEFAULT_STEP_TIME);
			newLayout = new CircleLayout(WorkflowDisplay.GRAPH);
			break;
		case FORCE_DIRECTED:
			super.setDuration(INFINITY);
			ForceDirectedLayout fdl = new ForceDirectedLayout(
					WorkflowDisplay.GRAPH, true);
			ForceSimulator fsim = fdl.getForceSimulator();
			fsim.getForces()[0].setParameter(0, -1.2f);
			newLayout = fdl;
			break;
		case HIERARCHICAL:
			super.setDuration(DEFAULT_STEP_TIME);
			NodeLinkTreeLayout l = new NodeLinkTreeLayout(WorkflowDisplay.GRAPH);
			l.setOrientation(Constants.ORIENT_RIGHT_LEFT); // == left to right
			newLayout = l;
			break;
		default:
			throw new IllegalArgumentException(type + " is unknown");
		}
		assert newLayout != null;
		
		newLayout.setVisualization(this.visualization);

		this.remove(this.layout);
		this.add(newLayout);
		this.layout = newLayout;
		this.type = type;
	}
	
	public Type getType() {
		return this.type;
	}
}
