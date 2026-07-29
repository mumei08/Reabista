package kaede.reabista.capabilities;

public class AbilityData implements IAbilityData {
    private boolean gluttonyEnabled = false;
    private boolean flyEnabled = false;
    private String currentWorldId = "overworld"; // デフォルトはoverworld

    @Override
    public boolean isGluttonyEnabled() {
        return gluttonyEnabled;
    }

    @Override
    public void setGluttonyEnabled(boolean value) {
        this.gluttonyEnabled = value;
    }

    @Override
    public boolean isFlyEnabled() {
        return flyEnabled;
    }

    @Override
    public void setFlyEnabled(boolean value) {
        this.flyEnabled = value;
    }

    @Override
    public String getCurrentWorldId() {
        return currentWorldId;
    }

    @Override
    public void setCurrentWorldId(String worldId) {
        this.currentWorldId = worldId;
    }
}