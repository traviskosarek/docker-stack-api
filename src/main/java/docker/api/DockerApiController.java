package docker.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import docker.api.container.Container;
import docker.api.node.Node;
import docker.api.service.Service;
import docker.api.swarm.Swarm;

@RestController
public class DockerApiController {

    @RequestMapping("/swarm")
    public ResponseEntity<Swarm> swarm() {
        try {
            Swarm swarm = this.getSwarm();

            ArrayList<Node> nodes = this.getNodes();
            ArrayList<Service> services = this.getServices(swarm.swarmName());
            ArrayList<Container> containers = new ArrayList<Container>();

            for (Service service : services) {
                containers.addAll(getContainers(service.getId()));
            }

            for (Node node : nodes) {
                ArrayList<Container> nodeContainers = new ArrayList<Container>();
                for (Container container : containers) {
                    if (container.hostNode().equals(node.getHostName())) {
                        nodeContainers.add(container);
                    }
                }
                node.setContainers(nodeContainers);
            }

            swarm.setNodes(nodes);

            return ResponseEntity.ok(swarm);
        } catch (CommandOutputException e) {
            Swarm errorStack = new Swarm("");
            errorStack.setErrorMessage(e.getMessage());
            return ResponseEntity.status(500).body(errorStack);
        } catch (Exception e) {
            Swarm errorStack = new Swarm("");
            // "Error retrieving Docker information"
            errorStack.setErrorMessage(e.getMessage());
            return ResponseEntity.status(500).body(errorStack);
        }
    }

    protected ArrayList<String> getTerminalOutput(String command) throws Exception {
        ArrayList<String> output = new ArrayList<String>();
        ArrayList<String> errorOutput = new ArrayList<String>();
        final Process p = Runtime.getRuntime().exec(command);

        new Thread(new Runnable() {
            public void run() {
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                try {
                    String line = null;
                    while ((line = input.readLine()) != null) {
                        output.add(line);
                    }
                    while ((line = error.readLine()) != null) {
                        errorOutput.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        p.waitFor();

        if (errorOutput.size() > 0) {
            String message = command;
            
            message = String.format("%s **********", message);
            for (String line : output) {
                message = String.format("%s *** %s", message, line);
            }
            message = String.format("%s **********", message);
            for (String line : errorOutput) {
                message = String.format("%s\n%s", message, line);
            }
            throw new CommandOutputException(message);
        }

        return output;
    }

    protected Swarm getSwarm() throws Exception {
        String name = "";
        ArrayList<String> output = this.getTerminalOutput("docker stack ls --format '{{.Name}}'");

        if (!output.isEmpty()) {
            name = output.get(0).split(" ")[0];
        }

        return new Swarm(name);
    }

    protected ArrayList<Node> getNodes() throws Exception {
        
        ArrayList<String> output = this.getTerminalOutput(
                "docker node ls --format '{{.ID}} {{.Hostname}} {{.Status}} {{.Availability}} {{.ManagerStatus}}'");
        ArrayList<Node> nodes = new ArrayList<Node>();

        if (!output.isEmpty()) {
            for (String line : output) {
                String[] tokens = line.split(" ");
                nodes.add(new Node(tokens));
            }
        }

        return nodes;
    }

    protected ArrayList<Service> getServices(String swarmName) throws Exception
    {
        ArrayList<Service> services = new ArrayList<Service>();

        if (!swarmName.equals("")) {

            String command = String.format("%s %s",
                    "docker stack services --format '{{.ID}} {{.Mode}} {{.Replicas}} {{.Image}}'", swarmName);

            ArrayList<String> output = this.getTerminalOutput(command);

            if (!output.isEmpty()) {
                for (String line : output) {
                    String[] tokens = line.split(" ");
                    services.add(new Service(tokens));
                }
            }
        }

        return services;
    }

    protected ArrayList<Container> getContainers(String serviceID) throws Exception {
        String command = String.format("%s %s %s", "docker service ps", serviceID,
                "--format '{{.Name}} {{.Image}} {{.Node}} {{.CurrentState}}'");

        ArrayList<String> output = this.getTerminalOutput(command);
        ArrayList<String> alreadyFoundContainers = new ArrayList<String>();
        ArrayList<Container> containers = new ArrayList<Container>();

        if (!output.isEmpty()) {
            for (String line : output) {
                String[] tokens = line.split(" ");

                if (!alreadyFoundContainers.contains(tokens[0])) {
                    alreadyFoundContainers.add(tokens[0]);
                    containers.add(new Container(tokens));
                }
            }
        }

        return containers;
    }

    protected class CommandOutputException extends Exception {
        public static final long serialVersionUID = 93L;

        public CommandOutputException(String message) {
            super(message);
        }
    }
}
