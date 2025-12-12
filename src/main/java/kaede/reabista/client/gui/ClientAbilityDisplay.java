package kaede.reabista.client.gui;

public class ClientAbilityDisplay {
    private static boolean shouldShow = false;

    public static void setShouldShow(boolean show) {
        shouldShow = show;
    }

    public static boolean getShouldShow() {
        return shouldShow;
    }
}
