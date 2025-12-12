package kaede.reabista.weapons.gameasset;

import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;

public class ReabistaWeaponColliders {
    public static final Collider THEUSFALL = new MultiOBBCollider(
            10,
            1.0,
            1.0,
            2.0,
            (double)0.0F,  // offset-X
            (double)0.0F,  // offset-Y
            (double)-1.0F);// offset-Z
    public static final Collider THAOSVENOM = new MultiOBBCollider(
            10,
            1.0,
            1.0,
            2.0,
            (double)0.0F,  // offset-X
            (double)0.0F,  // offset-Y
            (double)-1.0F);// offset-Z
}
