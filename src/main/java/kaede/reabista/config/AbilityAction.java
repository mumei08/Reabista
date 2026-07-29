package kaede.reabista.config;

public record AbilityAction(String key, int requiredAP, ActionType type) {

    public enum ActionType {
        EDIT,
    }

    public static final AbilityAction[] ALL_ACTIONS = new AbilityAction[]{
            new AbilityAction("copy", 2000, ActionType.EDIT),
            new AbilityAction("fly", 1100, ActionType.EDIT),
            new AbilityAction("teleport", 1300, ActionType.EDIT),
            new AbilityAction("gluttony", 1400, ActionType.EDIT),
            new AbilityAction("gluttonyp", 1400, ActionType.EDIT),
            new AbilityAction("guard", 1200, ActionType.EDIT),
            new AbilityAction("yggdrasill", 1500, ActionType.EDIT),
            new AbilityAction("healer", 1200, ActionType.EDIT),
            new AbilityAction("lightning", 1200, ActionType.EDIT),
            new AbilityAction("strength", 1200, ActionType.EDIT),
            new AbilityAction("clone", 1200, ActionType.EDIT),
            new AbilityAction("smoke", 1200, ActionType.EDIT),
            new AbilityAction("binary", 1200, ActionType.EDIT),
            new AbilityAction("erase", 1200, ActionType.EDIT),
            new AbilityAction("creation", 1200, ActionType.EDIT),
            new AbilityAction("destruction", 1200, ActionType.EDIT),
    };

    /** 指定タイプの操作リストを取得 */
    public static AbilityAction[] getByType(ActionType type) {
        return java.util.Arrays.stream(ALL_ACTIONS)
                .filter(a -> a.type() == type)
                .toArray(AbilityAction[]::new);
    }
}
