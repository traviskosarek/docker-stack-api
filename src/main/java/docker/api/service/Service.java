package docker.api.service;

public class Service {

    private final String id;
    private ServiceMode mode;
    private int runningReplicas;
    private int totalReplicas;
    private final String image;

    public Service(String[] tokens) {
        if (tokens.length == 4) {
            this.id = tokens[0];
            this.mode = this.parseServiceMode(tokens[1]);
            this.runningReplicas = this.parseRunningReplicas(tokens[2]);
            this.totalReplicas = this.parseTotalReplicas(tokens[2]);
            this.image = tokens[3];
        } else {
            this.id = "";
            this.mode = ServiceMode.Unknown;
            this.runningReplicas = 0;
            this.totalReplicas = 0;
            this.image = "";
        }
    }

    public String getId() {
        return this.id;
    }

    public String getImage() {
        return this.image;
    }

    public ServiceMode getMode() {
        return this.mode;
    }

    public int getRunningReplicas() {
        return this.runningReplicas;
    }

    public int getTotalReplicas() {
        return this.totalReplicas;
    }

    private ServiceMode parseServiceMode(String mode) {
        try {
            ServiceMode serviceMode = ServiceMode.valueOf(mode);
            return serviceMode;
        } catch (Exception e) {
            return ServiceMode.Unknown;
        }
    }

    private int parseRunningReplicas(String replicas) {
        String[] tokens = replicas.split("/");
        if (tokens.length == 2 && this.stringIsInteger(tokens[0])) {
            return Integer.parseInt(tokens[0]);
        }
        return 0;
    }

    private int parseTotalReplicas(String replicas) {
        String[] tokens = replicas.split("/");
        if (tokens.length == 2 && this.stringIsInteger(tokens[1])) {
            return Integer.parseInt(tokens[1]);
        }
        return 0;
    }

    private Boolean stringIsInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}