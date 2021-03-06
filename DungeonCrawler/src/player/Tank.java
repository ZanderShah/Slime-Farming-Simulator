package player;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import engine.AABB;
import engine.ParticleEmitter;
import engine.Stats;
import engine.StatusEffect;
import engine.damage.DamageSource;
import engine.damage.SwordDamageSource;
import engine.damage.TankStun;
import utility.Constants;
import utility.SpriteSheet;
import utility.Vector2D;
import world.Room;

public class Tank extends Player {
	private DamageSource ds;

	public Tank() {
		super(3);
		setStats(new Stats(Constants.TANK_HEALTH, Constants.TANK_ATTACK_SPEED, Constants.TANK_ATTACK_LENGTH,
				Constants.TANK_SPEED, Constants.TANK_DEFENCE));
		setHitbox(new AABB(getPos().add(new Vector2D(SpriteSheet.TANK_IMAGES[1].getWidth(null) / 2, getHeight() / 2)), SpriteSheet.TANK_IMAGES[1].getWidth(null), getHeight()));
	}

	@Override
	public void draw(Graphics g, Vector2D offset) {
		Vector2D shifted = getPos().add(offset);
		g.drawImage(SpriteSheet.TANK_IMAGES[getDirection()], (int) shifted.getX() - getWidth() / 2,
				(int) shifted.getY() - getHeight() / 2, null);
	}

	@Override
	public boolean attack(Point p, Room r) {
		boolean attacked = super.attack(p, r);
		if (attacked)
			ds = new SwordDamageSource(getPos(), Constants.TANK_SWORD_SIZE, (int) getAttackDir().getAngle() - Constants.TANK_SWING_ANGLE / 2, Constants.TANK_SWING_ANGLE, Constants.TANK_ATTACK_LENGTH, true, Constants.TANK_DAMAGE, Constants.TANK_KNOCKBACK, getID(), 4);
		r.addDamageSource(ds, getStats().getDamageMultiplier());
		return super.attack(p, r);
	}

	@Override
	public void update(Room r) {
		ds.getHitbox().updatePosition(getPos());
		super.update(r);
	}

	// Shield spell: gives all players a 1.5x defensive buff for 10 seconds
	// Cooldown: 25 seconds
	@Override
	public void ability1(Point p, Room r) {
		if (getCooldown(1) == 0) {
			setAbilityActive(1, Constants.TANK_BUFF_LENGTH);
			ArrayList<Player> players = r.getPlayers();
			for (int i = 0; i < players.size(); i++) {
				players.get(i).giveStatusEffect(new StatusEffect(Constants.TANK_BUFF_LENGTH, 0,
						Constants.TANK_BUFF_STRENGTH, StatusEffect.DEF, true));
			}
		}
	}

	// Stun: stuns all nearby enemies
	// Cooldown 15 seconds
	@Override
	public void ability2(Point p, Room r) {
		if (getCooldown(2) == 0) {
			r.addEmitter(new ParticleEmitter(0, getPos(), new Vector2D(), 2, 40, 0, 0, 80, Constants.TANK_STUN_RANGE, 0,
					40));
			r.addDamageSource(new TankStun(getPos(), Constants.TANK_STUN_RANGE, true, getID()),
					getStats().getDamageMultiplier());
			setCooldown(2, Constants.AB_COOLDOWNS[3][1]);
		}
	}

	// Reflect: hide behind your shield and reflect all projectiles (also turn
	// them friendly)
	// Cooldown: 20 seconds
	@Override
	public void ability3(Point p, Room r) {
		if (getCooldown(3) == 0) {
			setAbilityActive(0, Constants.TANK_REFLECT_TIME);
			setAbilityActive(3, Constants.TANK_REFLECT_TIME);
		}
	}

	@Override
	public int getWidth() {
		return SpriteSheet.TANK_IMAGES[getDirection()].getWidth(null);
	}

	@Override
	public int getHeight() {
		return SpriteSheet.TANK_IMAGES[0].getHeight(null);
	}
}
