package test;

// CLIPS API
import net.sf.clipsrules.jni.*;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class MarketAgent extends Agent {

    Environment clips;
    
    protected void setup() {
        
        try {
            clips = new Environment();
        } catch (Exception e) {
            //TODO: handle exception
        }

        System.out.println("Agent " + getLocalName() + " started.");

        addBehaviour(new TellBehaviour());
        addBehaviour(new AskBehaviour());
    }

    private class TellBehaviour extends Behaviour {

        boolean tellDone = false;
        
        public void action() {
            try {
                clips.eval("(clear)");
                // deftemplates
                clips.build("(deftemplate customer\n"
                                + "(slot customer-id)\n"
                                + "(multislot name)\n"
                                + "(multislot address)\n"
                                + "(slot phone)\n"
                            +")\n");

                clips.build("(deftemplate product\n"
                                + "(slot part-number)\n"
                                + "(slot name)\n"
                                + "(slot category)\n"
                                + "(slot price)\n"
                            + ")\n");

                clips.build("(deftemplate order\n"
                                + "(slot order-number)\n"
                                + "(slot customer-id)\n"
                            + ")\n");

                clips.build("(deftemplate line-item\n"
                                + "(slot order-number)\n"
                                + "(slot part-number)\n"
                                + "(slot customer-id)\n"
                                + "(slot quantity (default 1)))\n");

                // deffacts
                clips.build("(deffacts products \n"
                                + "(product (name USBMem) (category storage) (part-number 1234) (price 199.99))\n"
                                + "(product (name Amplifier) (category electronics) (part-number 2341) (price 399.99))\n"
                                + "(product (name \"Rubber duck\") (category mechanics) (part-number 3412) (price 99.99))\n"
                            + ")\n");

                clips.build("(deffacts customers\n"
                                + "(customer (customer-id 101) (name joe) (address bla bla bla) (phone 3313073905))\n"
                                + "(customer (customer-id 102) (name mary) (address bla bla bla) (phone 333222345))\n"
                                + "(customer (customer-id 103) (name bob) (address bla bla bla) (phone 331567890)) \n"
                            + ")\n");


                clips.build("(deffacts orders \n"
                                + "(order (order-number 300) (customer-id 102))\n"
                                + "(order (order-number 301) (customer-id 103))\n"
                            + ")\n");

                clips.build("(deffacts items-list\n"
                                + "(line-item (order-number 300) (customer-id 102) (part-number 1234))\n"
                                + "(line-item (order-number 301) (customer-id 103) (part-number 2341) (quantity 10))\n"
                            
                            + ")\n");

                // defrules
                // Define a rule for finding those customers who have not bought nothing at all... so far
                clips.build("(defrule cust-not-buying\n"
                                + "(customer (customer-id ?id) (name ?name))\n"
                                + "(not (order (order-number ?order) (customer-id ?id)))\n"
                            + "=>\n"
                            + "(printout t ?name \" no ha comprado... nada!\" crlf))\n");
                // Define a rule for finding which products have been bought
                clips.build("(defrule prods-bought\n"
                                + "(order (order-number ?order))\n"
                                + "(line-item (order-number ?order) (part-number ?part))\n"
                                + "(product (part-number ?part) (name ?pn))\n"
                                + "=>\n"
                                + "(printout t ?pn \" was bought \" crlf))\n");

                // Define a rule for finding which products have been bought AND their quantity
                clips.build("(defrule prods-qty-bgt\n"
                                + "(order (order-number ?order))\n"
                                + "(line-item (order-number ?order) (part-number ?part) (quantity ?q))\n"
                                + "(product (part-number ?part) (name ?p) )\n"
                                + "=>\n"
                                + "(printout t ?q \" \" ?p \" was/were bought \" crlf))\n");
                            
                // Define a rule for finding customers and their shopping info
                clips.build("(defrule customer-shopping\n"
                                + "(customer (customer-id ?id) (name ?cn))\n"
                                + "(order (order-number ?order) (customer-id ?id))\n"
                                + "(line-item (order-number ?order) (part-number ?part))\n"
                                + "(product (part-number ?part) (name ?pn))\n"
                                + "=>\n"
                                + "(printout t ?cn \" bought  \" ?pn crlf))\n");

                            // Define a rule for finding those customers who bought more than 5 products
                clips.build("(defrule cust-5-prods\n"
                                + "(customer (customer-id ?id) (name ?cn))\n"
                                + "(order (order-number ?order) (customer-id ?id))\n"
                                + "(line-item (order-number ?order) (part-number ?part) {quantity > 5})\n"
                                + "(product (part-number ?part) (name ?pn))\n"
                                + "=>\n"
                                + "(printout t ?cn \" bought more than 5 products (\" ?pn \")\" crlf))\n");

                // Define a rule for texting custormers who have not bought ...
                clips.build("(defrule text-cust (customer (customer-id ?cid) (name ?name) (phone ?phone))\n"
                                                + "(not (order (order-number ?order) (customer-id ?cid)))\n"
                                + "=>\n"
                                + "(assert (text-customer ?name ?phone \"tienes 25% desc prox compra\"))\n"
                                + "(printout t ?name \" 3313073905 tienes 25% desc prox compra\" ))\n");

                            // Define a rule for calling  custormers who have not bought ...
                clips.build("(defrule call-cust (customer (customer-id ?cid) (name ?name) (phone ?phone))\n"
                                                + "(not (order (order-number ?order) (customer-id ?cid)))\n"
                                + "=>\n"
                                + "(assert (call-customer ?name ?phone \"tienes 25% desc prox compra\"))\n"
                                + "(printout t ?name \" 3313073905 tienes 25% desc prox compra\" ))\n");
                
            } catch (Exception e) {
                //TODO: handle exception
            }

            tellDone = true;

        }

        public boolean done() {
            if(tellDone)
                return true;
            else
                return false;
        }
    }

    private class AskBehaviour extends Behaviour {

        boolean askDone = false;
        
        public void action() {
            try {
                clips.eval("(reset)");
                clips.eval("(facts)");
                clips.eval("(rules)");
                clips.run();
            } catch (Exception e) {
                //TODO: handle exception
            }

            askDone = true;
        }

        public boolean done() {
            if(askDone)
                return true;
            else
                return false;
        }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }
}
