package dev.majek.activelobby;

public class SavedState {

    private final boolean state;
    private long lastInteract;
    private final Interactable type;

    public SavedState(boolean state, long lastInteract, Interactable type) {
        this.state = state;
        this.lastInteract = lastInteract;
        this.type = type;
    }

    public boolean getState() {
        return state;
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
}
