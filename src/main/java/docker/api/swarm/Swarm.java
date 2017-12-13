package docker.api.swarm;

import java.util.ArrayList;
import docker.api.node.Node;

public class Swarm {
    private final String name;
    private ArrayList<Node> nodes;
    private String errorMessage;

    public Swarm(String name) {
        this.name = name;
        this.nodes = new ArrayList<Node>();
        this.errorMessage = "";
    }

    public String swarmName() {
        return this.name;
    }

    public String getMessage(){
        if(this.name.equals("") || this.nodes.size() == 0){
            if(this.errorMessage.equals(""))
            {
                return "Error retrieving Docker information";
            }
            else
            {
                return this.errorMessage;
            }
        }
        else {
            return "";
        }
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
}