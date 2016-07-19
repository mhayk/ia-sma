/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.util.ArrayList;
import java.util.StringTokenizer;
import pojo.Jogador;

/**
 *
 * @author mhayk
 */
public class JuizAgente extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    public static ArrayList<Jogador> jogo = new ArrayList();

    private class JuizBehaviour extends CyclicBehaviour {

        public JuizBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            try {
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    switch (msg.getPerformative()) {
                        case ACLMessage.REQUEST:
                            if (msg.getContent().startsWith("QUERO_JOGAR")) {
                                // adicionar o jogador ao jogo !
                                StringTokenizer stok = new StringTokenizer(msg.getSender().getLocalName(), ":", false);

                                Jogador jogador = new Jogador(
                                        stok.nextToken()
                                );

                                jogo.add(jogador);
                                //registra no log
                                myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - QUERO_JOGAR ["
                                        + ACLMessage.getPerformative(msg.getPerformative())
                                        + "] recebida de " + msg.getSender().getLocalName());
                                //resposta para o jogador
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setContent("JOGADOR_ADICIONADO");
                                myAgent.send(reply);

                                myLogger.log(Logger.WARNING, "Já temos 4 jogadores...!!");

                                if (jogo.size() == 4) {
                                    msg = null;
                                    ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                                
                                    myLogger.log(Logger.WARNING, "Nome: " + jogador.getNome() + " Palitos: " + jogador.getPalitosNaMao() );
                                                                            
                                    if( ( jogador.getPalitosNaMao() == -1) && ( jogador.getNome().contains("A") ) ) 
                                    {
                                        
                                        acl.addReceiver(new AID("A", AID.ISLOCALNAME));
                                        acl.setContent("JOGUE");
                                        myAgent.send(acl);
                                    }
                                    
                                    if( (jogador.getPalitosNaMao() ==  -1) && ( jogador.getNome().contains("B") ) ) 
                                    {
                                    acl.addReceiver(new AID("B", AID.ISLOCALNAME));
                                    acl.setContent("JOGUE");
                                    myAgent.send(acl);
                                    }
                                    
                                    if( (jogador.getPalitosNaMao() ==  -1) && ( jogador.getNome().contains("C") ) ) 
                                    {
                                    acl.addReceiver(new AID("C", AID.ISLOCALNAME));
                                    acl.setContent("JOGUE");
                                    myAgent.send(acl);
                                    }
                                    
                                    if( (jogador.getPalitosNaMao() ==  -1) && ( jogador.getNome().contains("D") ) ) 
                                    {
                                    acl.addReceiver(new AID("D", AID.ISLOCALNAME));
                                    acl.setContent("JOGUE");
                                    myAgent.send(acl);
                                    }
                                    //msg = myAgent.receive();
//                                    if (msg != null) {
//                                        ACLMessage replyA = msg.createReply();
//                                        switch (msg.getPerformative()) {
//                                            case ACLMessage.INFORM:
//                                                myLogger.log(Logger.INFO, "Jogada: " + msg.getContent());
//                                                break;
//                                        }
//
//                                    }
                                }
                            }
                            break;
                        case ACLMessage.INFORM:
                            myLogger.log(Logger.INFO, "Jogada: " + msg.getContent() + "Origem: " + msg.getSender());
                            
                            break;
                        default:
                            myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                    + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                    + msg.getSender().getLocalName());
                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                            myAgent.send(reply);
                            break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("JuizAgente");
        sd.setName(getName());
        sd.setOwnership("mhayk");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            JuizAgente.JuizBehaviour comportamento = new JuizAgente.JuizBehaviour(this);
            addBehaviour(comportamento);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

}
