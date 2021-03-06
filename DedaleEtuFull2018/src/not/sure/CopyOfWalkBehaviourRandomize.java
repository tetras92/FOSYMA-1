package not.sure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import mas.agents.AK_Agent;
import tools.GraphAK;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import env.Attribute;
import env.Couple;
import jade.core.behaviours.SimpleBehaviour;

public class CopyOfWalkBehaviourRandomize extends SimpleBehaviour {

	private static final long serialVersionUID = -5308185370371990470L;
	private Set<String> fermes,ouverts;
	private GraphAK G;
	private int onEndValue;
	private boolean finished=false;
	
	public CopyOfWalkBehaviourRandomize(final mas.abstractAgent myagent, GraphAK g) {
		super(myagent);
		G = g;
		this.fermes = G.getFermes();
		this.ouverts = G.getOuverts();
	}


	
	public List<String> majGraph(String src, List<Couple<String, List<Attribute>>> adjacents){
		List<String> ladj_node = new ArrayList<String>();
		G.addVertex(src);
		for(Couple<String, List<Attribute>> adjacent: adjacents){
			String adj_name = adjacent.getLeft();
			ladj_node.add(adjacent.getLeft());
			G.addVertex(adj_name,adjacent.getRight());
			G.addEdge(src,adj_name);
		}
		return ladj_node;
	}
	
	
	public String getNextPositionNearestOpenVertex(String src){
		
		DijkstraShortestPath<String, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<String, DefaultEdge>(G);
		int dist_min = G.vertexSet().size();
		String next_node = src;
		
		for(String dst: ouverts){
			List<String> shortestPath = dijkstraShortestPath.getPath(src,dst).getVertexList();
			if(shortestPath.size() < dist_min){
				dist_min = shortestPath.size();
				next_node = shortestPath.get(1);
			}
		}
		return next_node;
	}
	
	
	public List<String> getSuccessorsOpen(List<String> adj_names){
		//Completer les noeuds ouverts et determiner l'ensemble des successeurs potentiels
		List<String> successeurs_non_visites = new ArrayList<String>();
		for(String adj: adj_names){
			if(!this.fermes.contains(adj)){
				this.ouverts.add(adj);
				successeurs_non_visites.add(adj);
			}
		}
		return successeurs_non_visites;
	}
	
	public void action() {
		//Example to retrieve the current position
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=""){
			//List of observable from the agent's current position
			List<Couple<String,List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();//myPosition
//			System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);

			//Little pause to allow you to follow what is going on
			try {
//				System.out.println("Press Enter in the console to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
//				System.in.read();
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			//repeter la meme action
			if(!G.containsVertex(myPosition))
				G.addVertex(myPosition, lobs.get(0).getRight());
			
			if(ouverts.contains(myPosition))
				ouverts.remove(myPosition);
			
			fermes.add(myPosition);
			
			List<Couple<String, List<Attribute>>> adjacents = lobs;
			adjacents.remove(0);
			List<String> adj_names = majGraph(myPosition, adjacents);
			
			List<String> successeurs_non_visites = getSuccessorsOpen(adj_names);
			
			if(this.ouverts.isEmpty()){
				((AK_Agent)myAgent).exploration_is_done(); //Avertir quil a fini l'exploration, elle sera connu des autres agents
				G.clearFermes();       //Repeter l'operation d'exploration
				G.addAllOuverts(myPosition);
//				this.finished = true;
				System.out.println(myAgent.getLocalName()+" J'ai tout exploré en "+((AK_Agent)myAgent).getCpt()+" pas. Je reprends l'exploration !");
				((AK_Agent)myAgent).RAZCpt();
				successeurs_non_visites = getSuccessorsOpen(adj_names);
			}

			
			
			String next_pos = "";
			if(!successeurs_non_visites.isEmpty()){
				next_pos = successeurs_non_visites.get(new Random().nextInt(successeurs_non_visites.size()));
//				if (((AK_Agent)myAgent).getNombreDeCollision() == 3)
//					System.out.println(next_pos);
			}
			else{
				next_pos = getNextPositionNearestOpenVertex(myPosition);
			}
			
			boolean b = ((mas.abstractAgent)this.myAgent).moveTo(next_pos);

			if (!b)
				((AK_Agent)myAgent).setNombreDeCollision(((AK_Agent)myAgent).getNombreDeCollision()+1);
			else{
				((AK_Agent)myAgent).setNombreDeCollision(0);
				((AK_Agent)myAgent).CptPlus();
			}
			
			if ((((AK_Agent)myAgent).getNombreDeCollision() == 3) && (ouverts.size()==1)){                //N'a qu'un seul noeud ouvert a explorer
				G.clearFermes();
				this.fermes.add(next_pos);
				this.fermes.add(myPosition);
//				this.onEndValue=1;
				System.out.println("COLLISIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOooN");
			}
//			System.out.println(this.myAgent.getLocalName() + " Je Bouge");
		}
	}

	public boolean done() {
		return this.finished;
	}
	
	 // le Behavior s'arrête
    public int onEnd() {
//      myAgent.doDelete();
      return this.onEndValue;
    } 
	
}