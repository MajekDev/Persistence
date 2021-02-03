package dev.majek.persistence;

public enum Interactable {

    DOOR(Persistence.getCore().getConfig().getInt("door-cooldown"),
            Persistence.getCore().getConfig().getInt("door-cooldown") == -1),
    TRAPDOOR(Persistence.getCore().getConfig().getInt("trapdoor-cooldown"),
            Persistence.getCore().getConfig().getInt("trapdoor-cooldown") == -1),
    FENCE_GATE(Persistence.getCore().getConfig().getInt("fence-gate-cooldown"),
            Persistence.getCore().getConfig().getInt("fence-gate-cooldown") == -1),
    LEVER(Persistence.getCore().getConfig().getInt("lever-cooldown"),
            Persistence.getCore().getConfig().getInt("lever-cooldown") == -1),
    ITEM_FRAME(Persistence.getCore().getConfig().getInt("item-frame-cooldown"),
            Persistence.getCore().getConfig().getInt("item-frame-cooldown") == -1),
    VEHICLE(Persistence.getCore().getConfig().getInt("vehicle-teleport-back"),
            Persistence.getCore().getConfig().getInt("vehicle-teleport-back") == -1);

    private final int cooldown;
    private final boolean disabled;

    Interactable(int cooldown, boolean disabled) {
        this.cooldown = cooldown;
        this.disabled = disabled;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isEnabled() {
        return !disabled;
    }
}
