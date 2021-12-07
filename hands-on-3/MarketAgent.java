package test;

import net.sf.clipsrules.jni.*;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class MarketAgent extends Agent {

    Environment clips;
    
    protected void setup() {
        
        try {
            clips = new Environment();
        } catch (Exception e) {
            //TODO: handle exception
        }

        System.out.println("Agent " + getLocalName() + " started.");

        addBehaviour(new ReadClp());
        addBehaviour(new ListAndExecute());
    }

    private class ReadClp extends OneShotBehaviour {

        public void action() {
            try {
                clips.eval("(clear)");
                clips.load("./resources/market/load-templates.clp");
                clips.load("./resources/market/load-facts.clp");
                clips.load("./resources/market/load-rules.clp");
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }

    private class ListAndExecute extends OneShotBehaviour {

        public void action() {
            try {
                clips.eval("(reset)");
                clips.eval("(facts)");
                clips.eval("(rules)");
                clips.run();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }
}
