package dev.majek.activelobby;

public enum Interactable {

    DOOR(Persistence.getInstance().getConfig().getInt("door-cooldown")),
    TRAPDOOR(Persistence.getInstance().getConfig().getInt("trapdoor-cooldown")),
    FENCE_GATE(Persistence.getInstance().getConfig().getInt("fence-gate-cooldown")),
    LEVER(Persistence.getInstance().getConfig().getInt("lever-cooldown"));

    private final int cooldown;

    Interactable(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }
}
