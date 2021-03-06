package world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import player.Player;
import utility.Constants;
import utility.SpriteSheet;
import utility.Vector2D;
import enemy.Enemy;
import engine.AABB;
import engine.Drawable;
import engine.Particle;
import engine.ParticleEmitter;
import engine.damage.DamageSource;
import engine.damage.Projectile;

/**
 * Contains information about a specific room in the dungeon
 *
 * @author Alexander Shah
 * @version Jun 15, 2016
 */
public class Room implements Drawable
{
	// x y are used only for position relative to other rooms, when dealing with
	// the room individually,
	// bottom left is (0, 0) and top right is (width, height)
	private int x, y, width, height, difficulty, id;
	private boolean currentRoom, bossRoom;
	private Room up, down, left, right, nextLevel;
	private ArrayList<LevelObject> objects;
	private LevelObject[] doors;
	private ArrayList<DamageSource> damageSources;
	private ArrayList<Player> players;
	private BufferedImage nonMovingStuff;
	private LevelObject nonMovingStuffLevelObject;
	private ArrayList<Enemy> enemies;

	private ArrayList<Particle> particles;
	private ArrayList<ParticleEmitter> emitters;

	public Room(int x, int y, int width, int height, int difficulty, int id)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.difficulty = difficulty;
		this.id = id;

		currentRoom = bossRoom = false;
		up = down = left = right = nextLevel = null;
		objects = new ArrayList<LevelObject>();
		doors = new LevelObject[5];
		damageSources = new ArrayList<DamageSource>();
		players = new ArrayList<Player>();
		enemies = new ArrayList<Enemy>();
		emitters = new ArrayList<ParticleEmitter>();
		particles = new ArrayList<Particle>();
	}

	public void update()
	{
		for (int i = 0; i < enemies.size(); i++)
		{
			try
			{
				enemies.get(i).update(this);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		for (int i = 0; i < emitters.size(); i++)
		{
			emitters.get(i).update(this);
			if (emitters.get(i).isDead())
			{
				emitters.remove(i);
				i--;
			}
		}
		for (int i = 0; i < particles.size(); i++)
		{
			particles.get(i).update();
			if (particles.get(i).isDead())
			{
				particles.remove(i);
				i--;
			}
		}

		for (int i = 0; i < damageSources.size(); i++)
		{
			DamageSource d = damageSources.get(i);
			if (d != null)
			{
				d.update(this);
				if (d.getDuration() == 0)
				{
					damageSources.remove(d);
					i--;
				}
				else
				{
					boolean destroyed = false;
					if (d instanceof Projectile)
					{
						if (!(new Rectangle(0, 0, width * 64, height * 64)
								.contains(((Projectile) d).getPosition()
										.toPoint())))
						{
							damageSources.remove(d);
							i--;
							destroyed = true;
						}
						else
						{
							for (int l = 0; l < objects.size(); l++)
							{
								if (objects.get(l).blocksPlayer()
										&& objects.get(l).hitbox()
												.intersects(d.getHitbox())
										&& i >= 0)
								{
									damageSources.remove(d);
									i--;
									destroyed = true;
								}
							}
						}
					}
					if (!destroyed && d.getHitCounter() == 0)
					{
						if (d.isPlayer())
						{
							for (int e = 0; e < enemies.size(); e++)
							{
								if (d.hit(enemies.get(e)) && d.isSingleHit()
										&& i >= 0)
								{
									damageSources.remove(d);
									i--;
								}
							}
						}
						else
						{
							for (int p = 0; p < players.size(); p++)
							{
								if (d.hit(players.get(p)) && d.isSingleHit()
										&& i >= 0)
								{
									damageSources.remove(d);
									i--;
								}
							}
						}
					}
				}
			}
		}
	}

	public void addPlayer(Player p)
	{
		players.add(p);
	}

	public void addDamageSource(DamageSource ds, double damageMultiplier)
	{
		ds.setDamage(ds.getDamage() * damageMultiplier);
		damageSources.add(ds);
	}

	public void removeDamageSource(long id)
	{
		for (int i = 0; i < damageSources.size(); i++)
		{
			DamageSource d = damageSources.get(i);
			if (damageSources.get(i).getSourceID() == id)
			{
				damageSources.remove(d);
				i--;
			}
		}
	}

	public void addParticle(Particle p)
	{
		particles.add(p);
	}

	public void addEmitter(ParticleEmitter e)
	{
		emitters.add(e);
	}

	public void setUp(Room up)
	{
		this.up = up;
	}

	public void setDown(Room down)
	{
		this.down = down;
	}

	public void setLeft(Room left)
	{
		this.left = left;
	}

	public void setRight(Room right)
	{
		this.right = right;
	}

	public Room getUp()
	{
		return up;
	}

	public Room getDown()
	{
		return down;
	}

	public Room getLeft()
	{
		return left;
	}

	public Room getRight()
	{
		return right;
	}

	public boolean isCleared()
	{
		return enemies.size() == 0;
	}

	public int width()
	{
		return width;
	}

	public int height()
	{
		return height;
	}

	public int x()
	{
		return x;
	}

	public int y()
	{
		return y;
	}

	public int id()
	{
		return id;
	}

	public int getDifficulty()
	{
		return difficulty;
	}

	public void clean()
	{
		damageSources = new ArrayList<DamageSource>();
		players = new ArrayList<Player>();
	}

	public boolean isCurrent()
	{
		return currentRoom;
	}

	public void setCurrent()
	{
		currentRoom = true;
	}

	public boolean isBossRoom()
	{
		return bossRoom;
	}

	public void setBossRoom(Room nextLevel)
	{
		bossRoom = true;
		this.nextLevel = nextLevel;
	}

	public Room getNextLevel()
	{
		return nextLevel;
	}

	public void addLevelObject(LevelObject o)
	{
		objects.add(o);
	}

	public void addEnemy(Enemy e)
	{
		enemies.add(e);
	}

	public void removeEnemy(Enemy e)
	{
		enemies.remove(e);
	}

	public LevelObject getDoor(int index)
	{
		return doors[index];
	}

	public void setDoor(LevelObject o, int index)
	{
		doors[index] = o;
	}

	public int randomX(Image img, Random rng)
	{
		return Math.max((int) (rng.nextDouble() * ((width - 2) * 64)) + 128, 0);
	}

	public int randomY(Image img, Random rng)
	{
		return Math
				.max((int) (rng.nextDouble() * ((height - 2) * 64)) + 128, 0);
	}

	public ArrayList<Player> getPlayers()
	{
		return players;
	}

	public ArrayList<DamageSource> getDamageSources()
	{
		return damageSources;
	}

	/**
	 * Prepares the new room and clears the old room between transitions
	 * 
	 * @param r the new room
	 * @param direction the direction from the old room to the new room
	 * @return the newly prepared room
	 */
	public Room moveTo(Room r, int direction)
	{
		Vector2D newPos;
		if (direction == Constants.LEFT)
			newPos = new Vector2D((r.width() - 1) * 64 + 32,
					r.height() / 2 * 64);
		else if (direction == Constants.RIGHT)
			newPos = new Vector2D(32, r.height() / 2 * 64);
		else if (direction == Constants.UP)
			newPos = new Vector2D(r.width() / 2 * 64,
					(r.height() - 1) * 64 + 32);
		else
			newPos = new Vector2D(r.width() / 2 * 64, 32);

		for (int i = 0; i < players.size(); i++)
		{
			r.addPlayer(players.get(i));
			players.get(i).update(r);
			players.get(i).setPos(newPos);
		}

		for (int i = 0; i < damageSources.size(); i++)
		{
			damageSources.get(i).update(r);
			r.addDamageSource(damageSources.get(i), 1);
		}

		currentRoom = false;
		r.setCurrent();
		clean();

		return r;
	}

	public ArrayList<Enemy> getEnemies()
	{
		return enemies;
	}

	/**
	 * Adds a given number of experience points to each player
	 * 
	 * @param num the number of experience points
	 */
	public void dropExperience(int num)
	{
		for (int i = 0; i < players.size(); i++)
			players.get(i).addExperience(num);
	}

	/**
	 * Attempt to stop the screen tearing (it worked!)
	 */
	public void stopTearing2017()
	{
		nonMovingStuff = new BufferedImage(width * 64, height * 64,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = nonMovingStuff.getGraphics();

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				g.drawImage(SpriteSheet.FLOORS[difficulty], i * 64, j * 64,
						null);

		for (int i = 0; i < objects.size(); i++)
		{
			g.drawImage(objects.get(i).image(), objects.get(i).x(), objects
					.get(i).y(), null);
		}

		nonMovingStuffLevelObject = new LevelObject(new Vector2D(0, 0), false,
				false, nonMovingStuff);
	}

	public void draw(Graphics g, Vector2D offset)
	{
		if (bossRoom && !isCleared())
			g.setColor(new Color(255, 0, 0, 127));
		else if (currentRoom)
			g.setColor(new Color(60, 255, 0, 127));
		else if (isCleared())
			g.setColor(new Color(155, 155, 155, 127));
		else
			g.setColor(new Color(88, 88, 88, 127));

		g.fillRect(x, y, width, height);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
		g.setColor(new Color(0, 0, 0, 127));
		if (getUp() != null)
		{
			g.fillOval(x + width / 2 - 2, y - 2, 4, 4);
		}
		if (getDown() != null)
		{
			g.fillOval(x + width / 2 - 2, y + height - 2, 4, 4);
		}
		if (getLeft() != null)
		{
			g.fillOval(x - 2, y + height / 2 - 2, 4, 4);
		}
		if (getRight() != null)
		{
			g.fillOval(x + width - 2, y + height / 2 - 2, 4, 4);
		}
	}

	/**
	 * Draws the current view (includes doors, particles, enemies, players,
	 * floor, decorative tiles, etc.)
	 * @param g
	 * @param offset the offset to apply to each draw
	 * @param p the player that is being controlled
	 */
	public void detailedDraw(Graphics g, Vector2D offset, Player p)
	{
		if (nonMovingStuffLevelObject == null)
			stopTearing2017();
		nonMovingStuffLevelObject.draw(g, offset);

		for (int i = 0; i < damageSources.size(); i++)
		{
			try
			{
				if (damageSources.get(i) != null)
					damageSources.get(i).draw(g, offset);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (isCleared())
			for (int i = 1; i < doors.length; i++)
				if (doors[i] != null)
					doors[i].draw(g, offset);

		for (int i = 0; i < particles.size(); i++)
		{
			if (i < particles.size() && particles.get(i) != null)
			{
				try
				{
					particles.get(i).draw(g, offset);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < enemies.size(); i++)
		{
			try
			{
				if (i < enemies.size())
				{
					enemies.get(i).drawStatusEffects(g, offset);
					enemies.get(i).draw(g, offset);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		for (int i = 0; i < players.size(); i++)
		{
			try
			{
				if (players.get(i).getID() == p.getID())
				{
					p.drawStatusEffects(g,
							Constants.MIDDLE.subtract(p.getPos()));
					p.draw(g, Constants.MIDDLE.subtract(p.getPos()));
				}
				else
				{
					if (players.get(i) != null)
					{
						players.get(i).draw(g, offset);
						p.drawStatusEffects(g, offset);
					}
				}
			}
			catch (Exception e)
			{

			}
		}
	}

	/**
	 * Checks whether or not the party is at a door
	 * 
	 * @return -1 if there is no door, or the door which the party is at
	 */
	public int atDoor()
	{
		if (!isCleared() || players.size() == 0)
			return -1;

		for (int i = 1; i < doors.length; i++)
		{
			if (doors[i] != null)
			{
				int numAtDoor = 0;

				for (int p = 0; p < players.size(); p++)
					if (players.get(p).getHitbox()
							.intersects(doors[i].hitbox()))
						numAtDoor++;

				if (numAtDoor == players.size())
					return i;
			}
		}

		return -1;
	}

	/**
	 * Checks whether or not a hitbox will have any collisions in a room
	 * 
	 * @param hitbox the given hitbox
	 * @return whether or not a hitbox will have any collisions
	 */
	public boolean hasCollisionWith(AABB hitbox)
	{
		// Can only walk into a door if the room is cleared
		if (isCleared())
		{
			AABB doorHitbox = new AABB(hitbox.getPosition(),
					hitbox.getWidth() + 4, hitbox.getHeight() + 4);
			for (int i = 1; i < doors.length; i++)
				if (doors[i] != null
						&& doorHitbox.intersects(doors[i].hitbox()))
					return false;
		}

		// Outside of map and not on door
		if ((hitbox.getPosition().getX() - hitbox.getWidth() / 2)
				* (hitbox.getPosition().getY() - hitbox.getHeight() / 2) < 0
				|| (hitbox.getPosition().getX() + hitbox.getWidth() / 2) > width * 64
				|| (hitbox.getPosition().getY() + hitbox.getHeight() / 2) > height * 64)
		{
			return true;
		}

		for (LevelObject l : objects)
			if (l.blocksPlayer() && l.hitbox().intersects(hitbox))
				return true;

		return false;
	}

	/**
	 * Checks whether or not the room has space for a given hitbox
	 * 
	 * @param n the given hitbox
	 * @param onTop whether or not the object can exist on top of decorative
	 *            objects
	 * @return whether or not the room has space for the given hitbox
	 */
	public boolean hasSpaceFor(AABB n, boolean onTop)
	{
		if (n.getPosition().getX() < 64
				|| n.getPosition().getX() + n.getWidth() > (width - 1) * 64
				|| n.getPosition().getY() < 64
				|| n.getPosition().getY() + n.getHeight() > (height - 1) * 64)
			return false;

		for (LevelObject o : objects)
			if (n.intersects(o.hitbox()) && (!onTop || o.blocksPlayer()))
				return false;

		for (int i = 1; i < doors.length; i++)
			if (doors[i] != null && n.intersects(doors[i].hitbox()))
				return false;

		return true;
	}
}
