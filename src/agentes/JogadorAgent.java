/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.stream.IntStream;
import pojo.Jogador;
import util.Util;

/**
 *
 * @author mhayk
 */
public class JogadorAgent extends Agent {

    private Logger myLogger = Logger.getMyLogger(getClass().getName());
    private Jogador jogador;
    private Boolean isRegistered = false;

    private class RefreshBehaviour extends TickerBehaviour {

        public RefreshBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class JogadorBehaviour extends CyclicBehaviour {

        public JogadorBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = null;
            if (!isRegistered) {
                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                acl.addReceiver(new AID("Juiz", AID.ISLOCALNAME));
                acl.setContent("QUERO_JOGAR:" + jogador.getNome());
                myAgent.send(acl);
                msg = myAgent.blockingReceive();
            } else {
                msg = myAgent.receive();
            }
            try {
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    switch (msg.getPerformative()) {
                        case ACLMessage.INFORM:
                            if (msg.getContent().startsWith("JOGADOR_ADICIONADO")) {
                                isRegistered = true;
                                myLogger.log(Logger.INFO, "Adicionado no Jogo!");
                            } else if (msg.getContent().startsWith("AGUARDE_SUA_VEZ")) {
                                jogador.setSituacao(msg.getContent());
                                myLogger.log(Logger.INFO, jogador.getNome() + ":" + msg.getContent());
                            }
                            break;
                        case ACLMessage.REQUEST:
                            if (msg.getContent().startsWith("JOGUE")) {
                                jogador.setSituacao("JOGUEI");
                                ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
                                acl.addReceiver(new AID("Juiz", AID.ISLOCALNAME));
                                
                                Random rn = new Random();
                                int randonNumber = rn.ints(1, 0, 3).findFirst().getAsInt();
                                myLogger.log(Logger.INFO, jogador.getNome() + ": Valor: " + randonNumber);
                                acl.setContent(jogador.getNome()+ "," + randonNumber);
                                myAgent.send(acl);
                            }
                            break;
                        default:
                            myLogger.log(Logger.WARNING, "Agent " + getLocalName() + " - Mensagem inesperada ["
                                    + ACLMessage.getPerformative(msg.getPerformative()) + "] recebida de "
                                    + msg.getSender().getLocalName());
//                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
//                            myAgent.send(reply);
                            break;
                    }
                } else {
                    block();
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

        jogador = new Jogador(
                getLocalName()
        );

        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("JogadorAgent");
        sd.setName(getName());
        sd.setOwnership("mhayk");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
//            RefreshBehaviour comportamento = new RefreshBehaviour(this, 2250);
            JogadorBehaviour comportamento2 = new JogadorBehaviour(this);
//            addBehaviour(comportamento);
            addBehaviour(comportamento2);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
    }

}
