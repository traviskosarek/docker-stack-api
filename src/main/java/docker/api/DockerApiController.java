package docker.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import docker.api.swarm.Swarm;
import docker.api.node.Node;
import docker.api.service.Service;
import docker.api.container.Container;

@RestController
public class DockerApiController {

    @RequestMapping("/services")
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
                for (Container container : containers){

                    if(container.hostNode().equals(node.getHostName())){
                        nodeContainers.add(container);
                    }
                }
                node.setContainers(nodeContainers);
            }

            swarm.setNodes(nodes);

            return ResponseEntity.ok(swarm);
        } catch (Exception e) {
            e.printStackTrace();

            Swarm emptyStack = new Swarm("");

            emptyStack.setErrorMessage(e.toString());

            return ResponseEntity.status(500).body(emptyStack);
        }
    }

    private ArrayList<String> getTerminalOutput(String command) throws Exception {

        ArrayList<String> output = new ArrayList<String>();
        final Process p = Runtime.getRuntime().exec(command);

        new Thread(new Runnable() {
            public void run() {
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

                try {
                    String line = null;
                    while ((line = input.readLine()) != null) {
                        output.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        p.waitFor();

        return output;
    }

    private Swarm getSwarm() throws Exception {

        String name = "";
        ArrayList<String> output = this.getTerminalOutput("docker stack ls --format \"{{.Name}}\"");

        if (!output.isEmpty()) {
            name = output.get(0).split(" ")[0];
        }

        return new Swarm(name);
    }

    private ArrayList<Node> getNodes() throws Exception {

        ArrayList<String> output = this.getTerminalOutput(
                "docker node ls --format \"{{.ID}} {{.Hostname}} {{.Status}} {{.Availability}} {{.ManagerStatus}}\"");
        ArrayList<Node> nodes = new ArrayList<Node>();

        if (!output.isEmpty()) {
            for (String line : output) {
                String[] tokens = line.split(" ");
                nodes.add(new Node(tokens));
            }
        }

        return nodes;
    }

    private ArrayList<Service> getServices(String swarmName) throws Exception {

        String command = String.format("%s %s",
                "docker stack services --format \"{{.ID}} {{.Mode}} {{.Replicas}} {{.Image}}\"",
                swarmName);

        ArrayList<String> output = this.getTerminalOutput(command);
        ArrayList<Service> services = new ArrayList<Service>();

        if (!output.isEmpty()) {
            for (String line : output) {
                String[] tokens = line.split(" ");
                services.add(new Service(tokens));
            }
        }

        return services;
    }

    private ArrayList<Container> getContainers(String serviceID) throws Exception{

        String command = String.format("%s %s %s",
                "docker service ps",
                serviceID,
                "--format \"{{.Name}} {{.Image}} {{.Node}} {{.CurrentState}}\"");

        ArrayList<String> output = this.getTerminalOutput(command);
        ArrayList<String> alreadyFoundContainers = new ArrayList<String>();
        ArrayList<Container> containers = new ArrayList<Container>();

        if (!output.isEmpty()) {
            for (String line : output) {
                String[] tokens = line.split(" ");

                if(!alreadyFoundContainers.contains(tokens[0])){
                    alreadyFoundContainers.add(tokens[0]);
                    containers.add(new Container(tokens));
                }
            }
        }

        return containers;
    }
}
