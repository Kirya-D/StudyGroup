package kirya.view;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.Node;

/**
 * Manages a group of nodes where only 1 should be visible at a time
 */
public class NodeGroup {
    private final Collection<Node> nodes;

    /**
     * Initializes an empty NodeGroup.
     */
    public NodeGroup() {
        this.nodes = new ArrayList<>();
    }

    /**
     * Adds a new node to the node group, does nothing if the node is already in this group.
     * @param node The new node to add
     */
    public void addNode(Node node) {
        if (!this.nodes.contains(node)) {
            this.nodes.add(node);
            node.visibleProperty().addListener((_, _, newValue) -> {
                if (newValue) {
                    this.disableOtherNodes(node);
                }
            });
        }
    }

    /**
     * Adds all the nodes in the given collection to the group, duplicate elements are not added again.
     * @param nodes The new nodes to add
     */
    public void addNodes(Collection<Node> nodes) {
        for (var node : nodes) {
            this.addNode(node);
        }
    }

    private void disableOtherNodes(Node exceptionNode) {
        for (var node : this.nodes) {
            if (node == exceptionNode) {
                continue;
            }
            node.setVisible(false);
        }
    }

}
