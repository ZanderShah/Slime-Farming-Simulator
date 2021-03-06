package engine.damage;

import java.awt.Color;
import java.awt.Graphics;

import engine.AABB;
import utility.Constants;
import utility.SpriteSheet;
import utility.Vector2D;

/**
 * Mage's basic attack
 * @author Callum
 *
 */
public class Fireball extends Projectile
{

	public Fireball(Vector2D pos, Vector2D spd, boolean player, long id)
	{
		super(new AABB(pos, 3, 3), 0, 80 + ((int) (Math.random() * 21) - 10),
				pos, spd.getNormalized().multiply(1.5), true, player,
				Constants.MAGE_DAMAGE, 1, id);
	}

	@Override
	public void draw(Graphics g, Vector2D offset)
	{
		Vector2D shifted = getPosition().add(offset);
		g.setColor(Color.RED);
		g.drawImage(SpriteSheet.PROJECTILES[(int) (Math.random() * 3)],
				(int) shifted.getX() - 2, (int) shifted.getY() - 2, null);
	}
}