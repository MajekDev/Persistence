package dev.majek.persistence;

import org.bukkit.Rotation;

import java.util.UUID;

public class SavedState {

    private final boolean state;
    private final Rotation rotation;
    private long lastInteract;
    private final Interactable type;
    private final UUID uuid;

    // Doors, trapdoors, gates, and levers
    public SavedState(boolean state, long lastInteract, Interactable type) {
        this(state, null, lastInteract, type, null);
    }

    // ItemFrames
    public SavedState(boolean state, Rotation rotation, long lastInteract, Interactable type, UUID uuid) {
        this.state = state;
        this.rotation = rotation;
        this.lastInteract = lastInteract;
        this.type = type;
        this.uuid = uuid;
    }

    public boolean getState() {
        return state;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public long getLastInteract() {
        return lastInteract;
    }

    public void setLastInteract(long lastInteract) {
        this.lastInteract = lastInteract;
    }

    public Interactable getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }
}
