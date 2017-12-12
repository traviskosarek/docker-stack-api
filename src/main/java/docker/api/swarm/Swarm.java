package docker.api.swarm;

import java.util.ArrayList;
import docker.api.node.Node;

public class Swarm {
    private final String name;
    private ArrayList<Node> nodes;

    public Swarm(String name) {
            this.name = name;
    }

    public String swarmName() {
        return this.name;
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
}