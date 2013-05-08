package ntu.honda.problem.tsp;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;

import org.apache.commons.collections15.functors.ConstantTransformer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JTextField;

public class Demo extends JFrame {

    /**
	 *
	 */
	private static final long serialVersionUID = -5345319851341875800L;

	private Graph<Number,Number> g = null;

    private VisualizationViewer<Number,Number> vv = null;

    private AbstractLayout<Number,Number> layout = null;

    Timer timer;

    public boolean done;

    protected JButton switchLayout;

    public static final int EDGE_LENGTH = 100;

    public Demo() {
    	//init();
    	//start();
    }
    
    JTextField tf = new JTextField();
    
    //@Override
    public void init() {
    	
    	
    	

        //create a graph
    	Graph<Number,Number> ig = Graphs.<Number,Number>synchronizedDirectedGraph(new DirectedSparseMultigraph<Number,Number>());

        ObservableGraph<Number,Number> og = new ObservableGraph<Number,Number>(ig);
        og.addGraphEventListener(new GraphEventListener<Number,Number>() {

			public void handleGraphEvent(GraphEvent<Number, Number> evt) {
				//System.err.println("got "+evt);

			}});
        this.g = og;
        //create a graphdraw
        layout = new FRLayout<Number,Number>(g);
        layout.setSize(new Dimension(600,600));
		Relaxer relaxer = new VisRunner((IterativeContext)layout);
		relaxer.stop();
		relaxer.prerelax();

		Layout<Number,Number> staticLayout =
			new StaticLayout<Number,Number>(g, layout);

        vv = new VisualizationViewer<Number,Number>(staticLayout, new Dimension(600,600));

        JRootPane rp = this.getRootPane();
        rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(java.awt.Color.lightGray);
        getContentPane().setFont(new Font("Serif", Font.PLAIN, 12));

        vv.setGraphMouse(new DefaultModalGraphMouse<Number,Number>());

        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Number>());
        vv.setForeground(Color.white);

        vv.addComponentListener(new ComponentAdapter() {

			/**
			 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
			 */
			@Override
			public void componentResized(ComponentEvent arg0) {
				super.componentResized(arg0);
				System.err.println("resized");
				layout.setSize(arg0.getComponent().getSize());
			}});

        getContentPane().add(vv);
        switchLayout = new JButton("Switch to SpringLayout");
        switchLayout.addActionListener(new ActionListener() {

            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent ae) {
            	Dimension d = vv.getSize();//new Dimension(600,600);
                if (switchLayout.getText().indexOf("Spring") > 0) {
                    switchLayout.setText("Switch to FRLayout");
                    layout =
                    	new SpringLayout<Number,Number>(g, new ConstantTransformer(EDGE_LENGTH));
                    layout.setSize(d);
            		Relaxer relaxer = new VisRunner((IterativeContext)layout);
            		relaxer.stop();
            		relaxer.prerelax();
            		StaticLayout<Number,Number> staticLayout =
            			new StaticLayout<Number,Number>(g, layout);
    				LayoutTransition<Number,Number> lt =
    					new LayoutTransition<Number,Number>(vv, vv.getGraphLayout(),
    							staticLayout);
    				Animator animator = new Animator(lt);
    				animator.start();
    				vv.repaint();

                } else {
                    switchLayout.setText("Switch to SpringLayout");
                    layout = new FRLayout<Number,Number>(g, d);
                    layout.setSize(d);
            		Relaxer relaxer = new VisRunner((IterativeContext)layout);
            		relaxer.stop();
            		relaxer.prerelax();
            		StaticLayout<Number,Number> staticLayout =
            			new StaticLayout<Number,Number>(g, layout);
    				LayoutTransition<Number,Number> lt =
    					new LayoutTransition<Number,Number>(vv, vv.getGraphLayout(),
    							staticLayout);
    				Animator animator = new Animator(lt);
    				animator.start();
    				vv.repaint();

                }
            }
        });

        getContentPane().add(switchLayout, BorderLayout.SOUTH);
  
    	this.getContentPane().add(tf, BorderLayout.NORTH);
        
        timer = new Timer();
        switchLayout.doClick();
    }

    //@Override
    public void start() {
        validate();
        //set timer so applet will change
        timer.schedule(new RemindTask(), SleepTime, SleepTime); //subsequent rate
        vv.repaint();
        
        
        for(int i=0;i<NodeNum;i++)
        	g.addVertex(i);
        
    }

    Integer v_prev = null;

    public int NodeNum = 1;
    public int SleepTime = 100;
    public Integer[] bestPath;
    public double bestCost;
    public int generation;
    
    public int maxEdgeCount = 0;
    public void process() throws Exception {

    	vv.getRenderContext().getPickedVertexState().clear();
    	vv.getRenderContext().getPickedEdgeState().clear();
        try {

        		tf.setText("Generation: "+generation+" BestCost: " + bestCost);
                
            	
	        	for(int i=0;i<bestPath.length;i++) {
	        		g.removeEdge(i);
	        		g.addEdge(i, bestPath[i], bestPath[(i+1)%bestPath.length]);
	        	}
	        	
            	
            	layout.initialize();

        		Relaxer relaxer = new VisRunner((IterativeContext)layout);
        		relaxer.stop();
        		relaxer.prerelax();
        		StaticLayout<Number,Number> staticLayout =
        			new StaticLayout<Number,Number>(g, layout);
				LayoutTransition<Number,Number> lt =
					new LayoutTransition<Number,Number>(vv, vv.getGraphLayout(),
							staticLayout);
				Animator animator = new Animator(lt);
				animator.start();
				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
				vv.repaint();


        } catch (Exception e) {
            System.out.println(e);

        }
    }


    class RemindTask extends TimerTask {

        @Override
        public void run() {
            try {
				process();
			} catch (Exception e) {
			}
            if(done) cancel();

        }
    }
}