package kaede.reabista.capabilities;

public class AbilityData implements IAbilityData {
    private boolean gluttonyEnabled = false;

    @Override
    public boolean isGluttonyEnabled() {
        return gluttonyEnabled;
    }

    @Override
    public void setGluttonyEnabled(boolean value) {
        this.gluttonyEnabled = value;
    }
}