package docker.api.swarm;

import java.util.ArrayList;
import docker.api.node.Node;

public class Swarm {
    private final String name;
    private ArrayList<Node> nodes;

    public Swarm(String name) {
        this.name = name;
        this.nodes = new ArrayList<Node>();
    }

    public String swarmName() {
        return this.name;
    }

    public String getMessage(){
        if(this.name.equals("") || this.nodes.size() == 0){
            return "Error retrieving Docker information";
        }
        else {
            return "";
        }
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
}