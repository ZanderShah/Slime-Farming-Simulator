package player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import utility.Constants;
import utility.ControlState;
import utility.SpriteSheet;
import utility.Vector2D;
import world.Room;
import engine.AABB;
import engine.Stats;
import engine.damage.SwordDamageSource;

public class Warrior extends Player {
	private static final int SIZE = 32;

	public Warrior() {
		super(0);
		setStats(new Stats(Constants.WARRIOR_HEALTH,
				Constants.WARRIOR_ATTACK_SPEED,
				Constants.WARRIOR_ATTACK_LENGTH, Constants.WARRIOR_SPEED,
				Constants.WARRIOR_DEFENCE));
		setHitbox(new AABB(getPos().add(
				new Vector2D(getWidth() / 2, getHeight() / 2)), getWidth(),
				getHeight()));
	}

	@Override
	public void draw(Graphics g, Vector2D offset) {
		Vector2D shifted = getPos().add(offset);
		g.drawImage(SpriteSheet.WARRIOR_IMAGES[getDirection()],
				(int) shifted.getX() - getWidth() / 2, (int) shifted.getY()
						- getHeight() / 2,
				null);
	}

	@Override
	public int getWidth() {
		return SpriteSheet.WARRIOR_IMAGES[getDirection()].getWidth(null);
	}

	@Override
	public int getHeight() {
		return SpriteSheet.WARRIOR_IMAGES[0].getHeight(null);
	}

	@Override
	public void update(ControlState cs, Room r) {
		super.update(cs, r);
	}

	@Override
	public boolean attack(Point p, Room r) {
		boolean attacked = super.attack(p, r);
		if (attacked)
			r.addDamageSource(new SwordDamageSource(getPos(),
					Constants.WARRIOR_SWORD_SIZE,
					(int) getAttackDir().getAngle()
							- Constants.WARRIOR_SWING_ANGLE / 2,
					Constants.WARRIOR_SWING_ANGLE, getStats().getAttackTime(),
					true,
					Constants.WARRIOR_DAMAGE, Constants.WARRIOR_KNOCKBACK,
					getID()),
					getStats().getDamageMultiplier());
		return attacked;
	}

	// Spin attack ability: Do a giant spinning sword attack, hitting everything
	// around you
	// Cooldown: 5 seconds
	@Override
	public void ability1(Point p, Room r) {
		if (getAbilityActive(0) == 0 && getCooldown(1) == 0) {
			setAbilityActive(0, getStats().getAttackTime());
			setAbilityActive(1, getStats().getAttackTime());
			r.addDamageSource(new SwordDamageSource(getPos(),
					(int) (Constants.WARRIOR_SWORD_SIZE * 1.5), 0, 360,
					getStats().getAttackTime(), true,
					Constants.WARRIOR_DAMAGE, Constants.WARRIOR_KNOCKBACK,
					getID()),
					getStats().getDamageMultiplier());
		}
	}

	@Override
	public void ability2(Point p, Room r) {

	}

	@Override
	public void ability3(Point p, Room r) {

	}
}