package org.levigo.jadice.server.converterclient.gui.inspector;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.SwingUtilities;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.activity.ActivityManager;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.display.DisplayLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.tuple.TableNodeItem;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.util.NodeTraversal;
import com.levigo.jadice.server.util.NodeVisitor;

public class WorkflowDisplay extends Display { 

	private static class JobNodeEdge {
	
		public final com.levigo.jadice.server.Node start;
		public final com.levigo.jadice.server.Node end;
	
		public JobNodeEdge(com.levigo.jadice.server.Node start,
				com.levigo.jadice.server.Node end) {
			if (start == null || end == null)
				throw new IllegalArgumentException(
						"Neither start nor end must be null");
	
			this.start = start;
			this.end = end;
		}
	
		@Override
		public int hashCode() {
			return 3 * start.hashCode() + 31 * end.hashCode() / 7;
		}
	
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof JobNodeEdge))
				return false;
	
			JobNodeEdge other = (JobNodeEdge) obj;
			return this.start == other.start && this.end == other.end;
		}
	}

	private static final long serialVersionUID = -766174739726592556L;

	// Constants that prefuse referes to:
	private static final String LABEL = "label"; // Node's label
	private static final String TOOLTIP = "toolTip"; // Node's tooltip text
	private static final String SOURCE = "source";
	public static final String GRAPH = "graph";
	public static final String NODES = "graph.nodes";
	public static final String EDGES = "graph.edges";
	// ---

	private Visualization visualization = new Visualization();

	private Graph graph = new Graph(true);

	static final Schema LABEL_SCHEMA = new Schema();
	static {
		LABEL_SCHEMA.addColumn(LABEL, String.class);
		LABEL_SCHEMA.addColumn(TOOLTIP, String.class);
		LABEL_SCHEMA.addColumn(SOURCE, com.levigo.jadice.server.Node.class);
	};

	static final Schema EDGE_SCHEMA = new Schema();
	
	private WorkflowLayout layout = new WorkflowLayout(this.visualization);
	
	private final Collection<NodeSelectionListener> selectionListeners
		= new HashSet<NodeSelectionListener>();

	public WorkflowDisplay() {
		visualization.addGraph(GRAPH, graph);
		super.setHighQuality(true);
		
		// -- set up renderers --
		LabelRenderer m_nodeRenderer = new LabelRenderer(LABEL);
		m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);

		m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
		m_nodeRenderer.setRoundedCorner(8, 8);
		EdgeRenderer m_edgeRenderer = new EdgeRenderer(
				Constants.EDGE_TYPE_CURVE);

		m_edgeRenderer.setArrowType(Constants.EDGE_ARROW_FORWARD);
		m_edgeRenderer.setArrowHeadSize(5, 10);

		DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
		rf.add(new InGroupPredicate(EDGES), m_edgeRenderer);

		visualization.setRendererFactory(rf);

		rezoom();
		setVisualization(visualization);

//		pan(getSize().height / 2, getSize().height / 2);
		setForeground(Color.GRAY);
		setBackground(Color.WHITE);

		// main display controls
		addControlListener(new PanControl());
		addControlListener(new ZoomControl());

		addControlListener(new DragControl());
		addControlListener(new WheelZoomControl());
		addControlListener(new ZoomToFitControl());
		addControlListener(new ToolTipControl(TOOLTIP));
		addControlListener(new FocusControl(1));
		
		final TupleSet focusGroup = visualization.getGroup(Visualization.FOCUS_ITEMS); 
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
            	try {
	                for (Tuple t : rem) {
	                	
	                	// Was item removed from table?
	                	int row = ((VisualItem)t).getRow();
						if (row < 0) 
							continue;
	                	
	                    ((VisualItem)t).setFixed(false);
	                    ((VisualItem)t).setHighlighted(false);
	                    
	                    if (t instanceof TableNodeItem)
	                    for (NodeSelectionListener l : WorkflowDisplay.this.selectionListeners)
	                    	l.deselected(findJadiceNode((TableNodeItem)t));
	                }
	                
	                for (Tuple t : add) {
	                	if (!(t instanceof TableNodeItem))
	                		continue;
	                	
                		TableNodeItem item = (TableNodeItem)t;
                		item.setFixed(false);
                		item.setFixed(true);
                		item.setHighlighted(true);
                		for (NodeSelectionListener l : WorkflowDisplay.this.selectionListeners)
                			l.selected(findJadiceNode(item));
	                }
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
                visualization.run("draw");
            }
        });

        // Disable animation on double click
        addControlListener(new ControlAdapter(){
            public void itemPressed(VisualItem item, MouseEvent e) {
            }
            
            public void itemClicked(VisualItem item, MouseEvent event){
            }
            
            public void mouseClicked(java.awt.event.MouseEvent e){
                if (e.getClickCount()==2 && SwingUtilities.isLeftMouseButton(e)) {
                	layout.setAnimationEnabled(!layout.isAnimationEnabled());
                }
            }
        });

        visualization.putAction("init", layout);
        visualization.putAction("draw", layout);
        visualization.putAction("repaint", layout);

		visualization.run("init");
		visualization.runAfter("init", "draw");

	}

	/**
	 * waiting till addition running Activities are ready, outer it runs the
	 * ForceDirectedLayout
	 */
	private void waitForRunningActivities() {
//		if ((getGraphLayout() != 3 && !visualization.getAction("init").isEnabled())
//				|| (getGraphLayout() != 1 && !visualization.getAction("init").isEnabled())) {
			while (ActivityManager.activityCount() > 1/* && getGraphLayout() != 3*/) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
			}
//		}
	}

	private final Map<com.levigo.jadice.server.Node, Node> nodes = new HashMap<com.levigo.jadice.server.Node, Node>();

	private Node findOrCreateNode(com.levigo.jadice.server.Node node) {
		Node vNode = nodes.get(node);
		if (vNode == null) {
			Class<? extends com.levigo.jadice.server.Node> clazz = node
					.getClass();

			waitForRunningActivities();
			vNode = graph.addNode();
			vNode.setString(LABEL, clazz.getSimpleName());
			vNode.setString(TOOLTIP, clazz.getCanonicalName());
			vNode.set(SOURCE, node);
			nodes.put(node, vNode);
		}
		return vNode;
	}
	
	private final Map<JobNodeEdge, Edge> edges = new HashMap<JobNodeEdge, Edge>();
	
	private Edge findOrCreateEdge(JobNodeEdge edge) {
		Edge vEdge = edges.get(edge);
		if (vEdge == null) {
			Node startNode = findOrCreateNode(edge.start);
			Node endNode = findOrCreateNode(edge.end);
	
			waitForRunningActivities();
			vEdge = graph.addEdge(startNode, endNode);
			edges.put(edge, vEdge);
		}
		return vEdge;
	}

	private Job lastJob;
	
	public void showJob(Job job) {
		this.lastJob = job;
		this.clear();

		if (job == null)
			return;
		
		graph.getNodeTable().addColumns(LABEL_SCHEMA);
		graph.getEdgeTable().addColumns(EDGE_SCHEMA);

		
		doTraversal(job.getNode(), null);

		// update graph
		layout.setAnimationEnabled(true);
		visualization.run("init");
		visualization.run("repaint");
	}
	
	public Job getJob() {
	  return lastJob;
	}

  private void doTraversal(com.levigo.jadice.server.Node node, final com.levigo.jadice.server.Node parent) {
    NodeTraversal.traverse(node,
				new NodeVisitor<RuntimeException>() {
					public void visit(com.levigo.jadice.server.Node node) {
						findOrCreateNode(node);
						for (com.levigo.jadice.server.Node succ : node.getSuccessors())
							findOrCreateEdge(new JobNodeEdge(node, succ));
						
						if (parent != null) {
						  // It's a subsidiary node...
						  if (node.getPredecessors().isEmpty()) {
						    // .. at the beginning of the sub pipeline
						    findOrCreateEdge(new JobNodeEdge(parent, node));
						  }
						  if (node.getSuccessors().isEmpty())
						    // .. at the end of the sub pipeline
						    findOrCreateEdge(new JobNodeEdge(node, parent));
						}
						
						for (com.levigo.jadice.server.Node sub : node.getSubsidiaryNodes()) {
						  doTraversal(sub, node);
						}
					}
				}, false);
  }

	public void clear() {
		waitForRunningActivities();
		graph.clear();
		graph.getNodeTable().clear();
		graph.getEdgeTable().clear();
		
		visualization.getGroup(Visualization.FOCUS_ITEMS).clear();

		nodes.clear();
		edges.clear();

	}

	public WorkflowLayout.Type getGraphLayout() {
		return this.layout.getType();
	}
	
  public void setGraphLayout(WorkflowLayout.Type type) {
    if (type == this.layout.getType())
      return;

    this.clear();
    visualization.cancel("animate");
    visualization.removeGroup("animate");
    visualization.cancel("init");
    visualization.removeGroup("init");
    rezoom();
    layout.setType(type);
    showJob(lastJob);
  }
    
    private com.levigo.jadice.server.Node findJadiceNode(Node vNode) {
    	Object o = vNode.get(SOURCE);
    	if (o != null && o instanceof com.levigo.jadice.server.Node)
    		return (com.levigo.jadice.server.Node) o;
    	return null;
    }
    
	private void rezoom() {
		DisplayLib.fitViewToBounds(this, this.getBounds(), 0);
	}
	
	public void addSelectionListener(NodeSelectionListener l) {
		this.selectionListeners.add(l);
	}
	
	public void removeSelectionListener(NodeSelectionListener l) {
		this.selectionListeners.remove(l);
	}
}
