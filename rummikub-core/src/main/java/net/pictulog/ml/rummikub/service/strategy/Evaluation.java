package net.pictulog.ml.rummikub.service.strategy;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Evaluation {

    /**
     * Example of an Evaluation function expect minimax
     * 
     * @param node
     * @param depth
     * @return
     */
    public double expectiminimax(Node node, int depth) {
        double alpha = 0;
        if (node.getChildNodes().getLength() == 0 || depth == 0) {
            // this should be learned. but how.
            return the_heuristic_value_of(node);
        }
        NodeList children = node.getChildNodes();
        if (the_adversary_is_to_play_at(node)) {
            // Return value of minimum-valued child node
            alpha = Integer.MAX_VALUE;
            for (int i = 0; i < children.getLength(); i++) {
                alpha = Math.min(alpha, expectiminimax(children.item(i), depth - 1));
            }
        } else if (we_are_to_play_at(node)) {
            // Return value of maximum-valued child node
            alpha = Integer.MIN_VALUE;
            for (int i = 0; i < children.getLength(); i++) {
                alpha = Math.max(alpha, expectiminimax(children.item(i), depth - 1));
            }
        } else if (random_event_at(node)) {
            // Return weighted average of all child nodes' values
            alpha = 0;
            for (int i = 0; i < children.getLength(); i++) {
                alpha = alpha + (get_probability(children.item(i)) * expectiminimax(children.item(i), depth - 1));
            }
        }
        return alpha;
    }

    private double the_heuristic_value_of(Node node) {
        return node.getChildNodes().getLength() * .5D;
    }

    private boolean the_adversary_is_to_play_at(Node node) {
        return node.getChildNodes().getLength() == 0;
    }

    private boolean we_are_to_play_at(Node node) {
        return node.getChildNodes().getLength() == 0;
    }

    private boolean random_event_at(Node node) {
        return node.getChildNodes().getLength() == 0;
    }

    private double get_probability(Node node) {
        return node.getChildNodes().getLength() * .5D;
    }
}
