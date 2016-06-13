package engine.damage;

import java.util.ArrayList;

import engine.Hitbox;
import engine.LivingEntity;
import engine.StatusEffect;

public abstract class NonRepeatingDamageSource extends DamageSource {
	
	private ArrayList<LivingEntity> alreadyHit;
	
	public NonRepeatingDamageSource(Hitbox h, int d, boolean p, int dam, int kb) {
		super(h, 0, d, false, p, dam, kb);
		alreadyHit = new ArrayList<LivingEntity>();
	}
	
	public NonRepeatingDamageSource(Hitbox h, int d, boolean p, int dam, StatusEffect e, int kb) {
		super(h, 0, d, false, p, dam, e, kb);
		alreadyHit = new ArrayList<LivingEntity>();
	}
	
	@Override
	public boolean hit(LivingEntity le) {
		if (!alreadyHit.contains(le)) {
			if (super.hit(le)) {
				alreadyHit.add(le);
				return true;
			}
		}
		return false;
	}
}