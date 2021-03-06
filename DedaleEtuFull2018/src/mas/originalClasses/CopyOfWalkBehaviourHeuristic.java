package mas.originalClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mas.agents.AK_Agent;
import tools.GraphAK;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import env.Attribute;
import env.Couple;
import jade.core.behaviours.SimpleBehaviour;

public class CopyOfWalkBehaviourHeuristic extends SimpleBehaviour {

	private static final long serialVersionUID = -5308185370371990470L;
	private Set<String> fermes,ouverts;
	private GraphAK G;
	private int onEndValue=0;
	private boolean finished = false;
	private int nb_no_pb_collision = 0;
	
	public CopyOfWalkBehaviourHeuristic(final mas.abstractAgent myagent, GraphAK g) {
		super(myagent);
		G = g;
		this.fermes = G.getFermes();
		this.ouverts = G.getOuverts();
	}


	/***
	 * @param src position courante 
	 * @param adjacents : liste de couple contenant le nom du noeud et les informations sur ce noeud
	 * @return List<String> nom des noeuds adjacents
	 */
	public List<String> m_a_j_graphe(String src, List<Couple<String, List<Attribute>>> adjacents){
		List<String> ladj_node = new ArrayList<String>();

		if(!((AK_Agent)myAgent).isExplorationDone()) {
			G.addVertex(src);
			for(Couple<String, List<Attribute>> adjacent: adjacents){
				String adj_name = adjacent.getLeft();
				ladj_node.add(adjacent.getLeft());
				G.addVertex(adj_name,adjacent.getRight());
				G.addEdge(src,adj_name);
			}
		}else {
			for(Couple<String, List<Attribute>> adjacent: adjacents){
				G.updateNode(adjacent.getLeft(), adjacent.getRight());
				ladj_node.add(adjacent.getLeft());
			}
		}
		return ladj_node;
	}
	
	
	/***
	 * @param src position courante 
	 * @return le prochain deplacement vers le noeud ouvert non exploré le plus proche(DIJKSTRA)
	 */
//	public String getNextPositionNearestOpenVertex(String src){
//		DijkstraShortestPath<String, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<String, DefaultEdge>(G);
//		int dist_min = G.vertexSet().size();
//		String next_node = src;
//		
//		for(String dst: ouverts){
//			List<String> shortestPath = dijkstraShortestPath.getPath(src,dst).getVertexList();
//			if(shortestPath.size() < dist_min){
//				dist_min = shortestPath.size();
//				next_node = shortestPath.get(1);
//			}
//		}
//		return next_node;
//	}
	
	/***
	 * @param src 
	 * @return prochain noeud qui mène vers le noeud ouvert le plus prochain de la src
	 * 		   chaîne vide s'il n'existe pas 
	 */
	public String nextPositionNearestOpenVertex(String src){
		DijkstraShortestPath<String, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<String, DefaultEdge>(G);
		int dist_min = G.vertexSet().size();
		String next_node = "";

		for(String dst: ouverts){
			try {
				List<String> shortestPath = dijkstraShortestPath.getPath(src,dst).getVertexList();
				if(shortestPath.size() < dist_min  ){
					dist_min = shortestPath.size();
					next_node = shortestPath.get(1);
				}
			}catch(Exception e) {
				continue;
//				System.out.println(" chemin non trouve de "+src+" vers "+dst);
			}
		}
		return next_node;
	}
	
	
	/***
	 * @param successors Liste de noms des noeuds adjacents
	 * @return nom du prochain noeud deplacement
	 */
	public String getBestOpenNeighborVertice(List<String> successors){
		String next_node;
		next_node = successors.get(0);
		int max_value = G.getNbOpenNeighborVertex(next_node);
		for(String succ : successors) {
			int value_tmp_node = G.getNbOpenNeighborVertex(succ);
			if(value_tmp_node > max_value) {
				max_value = value_tmp_node;
				next_node = succ;
			}
		}
		return next_node;
	}
	
	
	/***
	 * DEUX POSSIBILITES :
	 *  	Priorite au deplacement vers les adjacents ouverts
	 *  	Sinon vers la recherche d'un deplacement vers le plus proche noeud ouvert non voisin
	 * @param successeurs_non_visites Liste de nom des adjacents NON VISITE
	 * @return Nom du prochain noeud deplacement
	 * 		   chaîne vide s'il n y a aucun deplacement possible
	 */
	public String getNextPosition(List<String> successeurs_non_visites ){
		String next_pos;
		if(!successeurs_non_visites.isEmpty()){
			next_pos = getBestOpenNeighborVertice(successeurs_non_visites);
		}else{
			String myPosition = ((mas.abstractAgent)this.myAgent).getCurrentPosition();
			next_pos = nextPositionNearestOpenVertex(myPosition);
		}
		return next_pos;
	}
	
	
	/***
	 * @param adj_names Liste de noms des noeuds adjacents
	 * @return Liste de noms des noeuds adjancets OUVERTS
	 */
	public List<String> get_open_neighbors(List<String> adj_names){
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

			try {
//				System.in.read();
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
			
			if(!G.containsVertex(myPosition))
				G.addVertex(myPosition, lobs.get(0).getRight());
			ouverts.remove(myPosition);
			fermes.add(myPosition);
			
			
			List<Couple<String, List<Attribute>>> adjacents = lobs;
			Couple<String, List<Attribute>> curr_observation = adjacents.remove(0);
			G.updateNode(myPosition, curr_observation.getRight());                      //MaJ des informations sur les noeuds
			
			List<String> adj_names = m_a_j_graphe(myPosition, adjacents);
			Set<String> detect_golem = G.isGolemAround(myPosition);						     //ensemble de noeuds ou le Golem a ete detecte
		
			
		
			//Selection des voisins ouverts
			List<String> voisins_ouverts = get_open_neighbors(adj_names);
			Set<String> removedVertices = ((AK_Agent)myAgent).getSetRemovedVertices();

			//Si tout a ete explore
			if(this.ouverts.isEmpty() && removedVertices.isEmpty()){
				((AK_Agent)myAgent).exploration_is_done();									 //Utilise pour les MaJ des informations (True il connait completement la topo de l'env)
//				((AK_Agent)myAgent).resetVerticesToGraph();
				G.clearFermes();    					 									 //Repeter l'operation d'exploration
				((AK_Agent)myAgent).setNombreDeCollision(0);
				G.addAllOuverts(myPosition);
				System.out.println(myAgent.getLocalName()+" : Exploration DONE ("+((AK_Agent)myAgent).getCpt()+"). Restart !");
				((AK_Agent)myAgent).RAZCpt();
				voisins_ouverts = get_open_neighbors(adj_names);
			}
			
			removedVertices = ((AK_Agent)myAgent).getSetRemovedVertices();
			
			
			//Selection du prochain deplacement
			String next_pos = getNextPosition(voisins_ouverts);
			//aucune issue, il faut que je passe par un noeud qui a ete supprime du graphe
//			if(next_pos.equals("") && removedVertices.size()==1) {
//				G.clearFermes();
//				((AK_Agent)myAgent).resetVerticesToGraph();
//				System.out.println(myAgent.getLocalName()+" : No choice but to reset closed&removed1 vertices after "+((AK_Agent)myAgent).getNombreDeCollision()+" collisions.");
//				next_pos = getNextPosition(voisins_ouverts);
//			}
			if(next_pos.equals("") && !removedVertices.isEmpty()) {
//				G.clearFermes();
//				((AK_Agent)myAgent).resetVerticesToGraph();
				System.out.println(myAgent.getLocalName()+" : No choice but to reset removed vertices after "+((AK_Agent)myAgent).getNombreDeCollision()+" collisions.");
				next_pos = getNextPosition(voisins_ouverts);
			}
			else if(next_pos.equals("") ) {
				G.clearFermes();
//				((AK_Agent)myAgent).resetVerticesToGraph();
				System.out.println(myAgent.getLocalName()+" : No choice but to reset closed&removed vertices after "+((AK_Agent)myAgent).getNombreDeCollision()+" collisions.");
				next_pos = getNextPosition(voisins_ouverts);
			}
			
			
			
			//Test si le deplacement s'est bien acoompli
			boolean has_moved = ((mas.abstractAgent)this.myAgent).moveTo(next_pos);
			if(has_moved){
				this.finished=false;														 //Je continue a me deplacer
//				((AK_Agent)myAgent).setNombreDeCollision(0);
				((AK_Agent)myAgent).CptPlus();
				
				//Apres 5 deplacement reussi, on vide les noeuds supprimes lors des collisions
//				nb_no_pb_collision++;
//				if(nb_no_pb_collision >5 || nb_no_pb_collision==0)
//					((AK_Agent)myAgent).resetVerticesToGraph();
			}
			else{
				
//				nb_no_pb_collision = 0;														 //remet a zero le compteur qui vide la liste des noeuds supprimes
				
				
				int nb_collision = ((AK_Agent)myAgent).getNombreDeCollision() + 1;
				((AK_Agent)myAgent).setNombreDeCollision(nb_collision);
				
				System.out.println("= = = = = = = = = = >BLOCUS : "+next_pos);
				//Dans le cas ou on echange les Ouverts, Fermes
				ouverts.remove(next_pos);
				fermes.add(next_pos);
			
				/***
				 * 				COLLISION AVEC AGENT
				 * Si premiere collision avec un agent : envoie un message d'information
				 * Si deux collision : rechecker sa boite
				 * Si plus d'une collision : enlever le noeud a probleme
				 */
//				if(detect_golem.isEmpty()) {
//					System.out.println(myAgent.getLocalName()+ " : Collision with an Agent ("+nb_collision+" collisions)");
//					//Premiere collision avec un agent, behaviour de deplacement s'arrete pour envoyer un msg info
//					if(nb_collision==1 ) 
//						this.finished=true;													 			
////					else if (nb_collision==2){            //Si on veut qu'il va rechecker sa boite au lettre 
////						this.finished=true;
////						this.onEndValue = 1;
////					}
////					if(nb_collision>1) {
////						//Enlever le noeud a collision entre agents apres 2 collisions (Pas sur que c'est le mm agent qu'on a eu interblocage
////						((AK_Agent)myAgent).removeVertexCollision(next_pos);				 
////						System.out.println(myAgent.getLocalName()+" :(A) removed vertex "+next_pos);
////					}
//				}
				
				/***
				 * 				COLLISION AVEC LE GOLEM
				 * Enlever le noeud a problem
				 * On anticipie pas le deplacement sur le fait qu'on a pu sentir le GOLEM bien avant
				 */
				if(detect_golem.contains(next_pos)){
					((AK_Agent)myAgent).removeVertexCollision(next_pos);
					System.out.println(myAgent.getLocalName()+" : Collision with Golem. Removed vertex ("+next_pos+")");
				}
			}
			
		}
	}

	
	public boolean done() {
		return this.finished;
	}
	
	 // le Behavior s'arrête
    public int onEnd() {
      return this.onEndValue;
    } 
	
}