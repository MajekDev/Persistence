package dev.majek.activelobby;

public enum Interactable {

    DOOR(ActiveLobby.getInstance().getConfig().getInt("door-cooldown")),
    TRAPDOOR(ActiveLobby.getInstance().getConfig().getInt("trapdoor-cooldown")),
    FENCE_GATE(ActiveLobby.getInstance().getConfig().getInt("fence-gate-cooldown")),
    LEVER(ActiveLobby.getInstance().getConfig().getInt("lever-cooldown"));

    private final int cooldown;

    Interactable(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }
}
