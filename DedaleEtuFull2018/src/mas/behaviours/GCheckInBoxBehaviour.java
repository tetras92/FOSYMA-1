package mas.behaviours;

import mas.agents.AK_Agent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GCheckInBoxBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8844060548399027721L;
	private int onEndValue=-1;
	
	
	public GCheckInBoxBehaviour(final Agent myagent) {
		super(myagent);
	}
	

//	public void action() {
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		ACLMessage msg = this.myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
//		//Priorite 1 aux messages INFORM (MaJ de mes connaissances)
//		if(msg!=null)
//			this.onEndValue = 1;
//		
//		else{
//			//Priorite 2 aux messages REQUEST (un agent demande des informations)
//			msg = this.myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));            //Recu une demande d'information 
//			if(msg!=null)
//				this.onEndValue = 2;
//			//Priorite 3 le reste
//			else
//				msg = this.myAgent.receive();
//		}
//		
//		if(msg != null){
//			((AK_Agent)myAgent).setToread(msg);
//			System.out.println(myAgent.getLocalName()+" : Receive MSG "+this.onEndValue);
//		}else {
//			System.out.println(myAgent.getLocalName()+" : No MSG. ");
//			this.onEndValue = -1;
//		}
//		
////		while(myAgent.receive()!=null); //tout supprimer dans la boite
//	}
	
	
	
	public void action() {
		ACLMessage msg = this.myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		//Priorite 1 aux messages INFORM (MaJ de mes connaissances)
		if(msg!=null)
			this.onEndValue = 1;
		
//		else{
//			//Priorite 2 aux messages REQUEST (un agent demande des informations)
//			msg = this.myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));            //Recu une demande d'information 
//			if(msg!=null)
//				this.onEndValue = 2;
//			//Priorite 3 le reste
		else
			msg = this.myAgent.receive();
//		}
		
		if(msg != null){
			((AK_Agent)myAgent).setToread(msg);
			System.out.println(myAgent.getLocalName()+" : Receive MSG "+this.onEndValue);
		}else {
			System.out.println(myAgent.getLocalName()+" : No MSG. ");
		}

		while(myAgent.receive()!=null);
	}

	
	
	
	public int onEnd() {
		return onEndValue;
	}

}