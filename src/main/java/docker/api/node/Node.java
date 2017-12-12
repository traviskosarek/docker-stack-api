package docker.api.node;

import java.util.ArrayList;
import docker.api.container.Container;

public class Node {

    private final String id;
    private final String hostName;
    private NodeStatus status;
    private NodeAvailability availability;
    private NodeManagerStatus managerStatus;
    private ArrayList<Container> containers;

    public Node(String[] tokens) {
        if (tokens != null && tokens.length == 4) {
            this.id = tokens[0];
            this.hostName = tokens[1];
            this.status = parseNodeStatus(tokens[2]);
            this.availability = parseNodeAvailability((tokens[3]));
            this.managerStatus = NodeManagerStatus.Unavailable;
        }
        else if (tokens != null && tokens.length == 5) {
            this.id = tokens[0];
            this.hostName = tokens[1];
            this.status = parseNodeStatus(tokens[2]);
            this.availability = parseNodeAvailability(tokens[3]);
            this.managerStatus = parseNodeManagerStatus(tokens[4]);
        } else {
            this.id = "";
            this.hostName = "";
            this.status = NodeStatus.Unknown;
            this.availability = NodeAvailability.Unknown;
            this.managerStatus = NodeManagerStatus.Unavailable;
        }
    }

    public String ID() {
        return this.id;
    }

    public String getHostName() {
        return this.hostName;
    }

    public NodeStatus getStatus() {
        return this.status;
    }

    public NodeAvailability getAvailability() {
        return this.availability;
    }

    public NodeManagerStatus getManagerStatus() {
        return this.managerStatus;
    }

    public ArrayList<Container> getContainers() {
        return this.containers;
    }

    public void setContainers(ArrayList<Container> containers){
        this.containers = containers;
    }

    private NodeStatus parseNodeStatus(String status) {
        try {
            NodeStatus nodeStatus = NodeStatus.valueOf(status);
            return nodeStatus;
        } catch (Exception e) {
            return NodeStatus.Unknown;
        }
    }

    private NodeAvailability parseNodeAvailability(String availability) {
        try {
            NodeAvailability nodeAvailability = NodeAvailability.valueOf(availability);
            return nodeAvailability;
        } catch (Exception e) {
            return NodeAvailability.Unknown;
        }
    }

    private NodeManagerStatus parseNodeManagerStatus(String status) {
        try {
            NodeManagerStatus nodeManagerStatus = NodeManagerStatus.valueOf(status);
            return nodeManagerStatus;
        } catch (Exception e) {
            return NodeManagerStatus.Unavailable;
        }
    }
}