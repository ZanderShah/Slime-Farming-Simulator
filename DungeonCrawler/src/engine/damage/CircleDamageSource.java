package engine.damage;

import engine.CircleHitbox;
import engine.StatusEffect;
import utility.Vector2D;

/**
 * A damage source that is circular
 * @author Callum
 *
 */
public abstract class CircleDamageSource extends DamageSource {

	public CircleDamageSource(Vector2D pos, int rad, int f, int d, boolean single, boolean p, double dam, int kb, long id) {
		super(new CircleHitbox(pos, rad), f, d, single, p, dam, kb, id);
	}
	
	public CircleDamageSource(Vector2D pos, int rad, int f, int d, boolean single, boolean p, double dam, StatusEffect e, int kb, long id) {
		super(new CircleHitbox(pos, rad), f, d, single, p, dam, e, kb, id);
	}
	
	public CircleHitbox getHitbox() {
		return (CircleHitbox) super.getHitbox();
	}
}
