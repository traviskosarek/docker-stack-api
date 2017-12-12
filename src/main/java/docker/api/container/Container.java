package docker.api.container;

import docker.api.service.TaskState;

public class Container {

    private final String name;
    private final String image;
    private final String node;

    private TaskState state;

    public Container(String[] tokens){
        if(tokens.length >= 4) {
            this.image = tokens[1];
            this.name = this.getContainerName(tokens[0], this.image);
            this.node = tokens[2];
            this.state = parseState(tokens[3]);
        }
        else {
            this.name = "";
            this.image = "";
            this.node = "";
            this.state = TaskState.Unknown;
        }
    }

    public String getName(){
        return this.name;
    }

    public String getImage(){
        return this.image;
    }

    public String hostNode(){
        return this.node;
    }

    public TaskState getState(){
        return this.state;
    }

    private String getContainerName(String name, String image){
        String[] nameTokens = name.split("\\.");
        String[] imageTokens = image.split(":");

        String containerName = imageTokens.length > 0 ? imageTokens[0] : "***";
        String identifier = nameTokens.length > 0 ? nameTokens[nameTokens.length - 1] : "***";
        return String.format("%s_%s", containerName, identifier);
    }

    private TaskState parseState(String state) {
        try {
            TaskState taskState = TaskState.valueOf(state);
            return taskState;
        } catch (Exception e) {
            return TaskState.Unknown;
        }
    }

}