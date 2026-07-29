package kaede.reabista.capabilities;

public interface IAbilityData {
    boolean isGluttonyEnabled();
    void setGluttonyEnabled(boolean value);
    boolean isFlyEnabled();
    void setFlyEnabled(boolean value);

    // ワールド管理用
    String getCurrentWorldId();
    void setCurrentWorldId(String worldId);
}
