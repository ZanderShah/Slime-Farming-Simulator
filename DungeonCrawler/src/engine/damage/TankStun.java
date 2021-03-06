package engine.damage;

import java.awt.Color;
import java.awt.Graphics;

import engine.StatusEffect;
import utility.Constants;
import utility.Vector2D;

/**
 * Tank's stun ability, stuns near by enemies
 * @author Callum
 *
 */
public class TankStun extends CircleDamageSource {

	public TankStun(Vector2D pos, int rad, boolean p, long id) {
		super(pos, rad, 0, 5, false, p, 0, new StatusEffect(Constants.TANK_STUN_LENGTH, 0, 0, StatusEffect.STUN, true), 0, id);
	}

	@Override
	public void draw(Graphics g, Vector2D offset) {
		g.setColor(new Color(0, 0, 0, 64));
		Vector2D pos = getHitbox().getPosition().add(offset);
		int radius = getHitbox().getRadius();
		g.fillOval((int) pos.getX() - radius, (int) pos.getY() - radius, radius * 2, radius * 2);
	}
}