package docker.api.service;

public enum TaskState {
    New,
    Pending,
    Assigned,
    Accepted,
    Preparing,
    Starting,
    Ready,
    Running,
    Complete,
    Failed,
    Shutdown,
    Rejected,
    Orphaned,
    Unknown
}
