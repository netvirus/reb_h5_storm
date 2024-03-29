package l2r.gameserver.templates.item.support;

import java.util.Collection;
import java.util.List;

import l2r.commons.collections.MultiValueSet;
import l2r.gameserver.model.reward.RewardItem;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public class FishTemplate
{
	private final FishGroup _group;
	private final FishGrade _grade;
	private final double _biteRate;
	private final double _guts;
	private final double _lengthRate;
	private final double _hpRegen;
	private final double _gutsCheckProbability;
	private final double _cheatingProb;
	private final int _itemId;
	private final int _hp;
	private final int _level;
	private final int _maxLength;
	private final int _startCombatTime;
	private final int _combatDuration;
	private final int _gutsCheckTime;

	private final IntObjectMap<RewardItem> _rewards = new HashIntObjectMap<RewardItem>();

	public FishTemplate(MultiValueSet<String> map, List<MultiValueSet<String>> rewards)
	{
		_group = map.getEnum("group", FishGroup.class);
		_grade = map.getEnum("grade", FishGrade.class);

		_biteRate = map.getDouble("bite_rate");
		_guts = map.getDouble("guts");
		_lengthRate = map.getDouble("length_rate");
		_hpRegen = map.getDouble("hpregen");
		_gutsCheckProbability = map.getDouble("guts_check_probability");
		_cheatingProb = map.getDouble("cheating_prob");

		_itemId = map.getInteger("id");
		_level = map.getInteger("level");
		_hp = map.getInteger("hp");
		_maxLength = map.getInteger("max_length");
		_startCombatTime = map.getInteger("start_combat_time");
		_combatDuration = map.getInteger("combat_duration");
		_gutsCheckTime = map.getInteger("guts_check_time");

		for(MultiValueSet<String> reward : rewards)
		{
			int id = reward.getInteger("id");
			int mindrop = reward.getInteger("mindrop");
			int maxdrop = reward.getInteger("maxdrop");
			int chance = reward.getInteger("chance");
			_rewards.put(id, new RewardItem(id, mindrop, maxdrop,  chance));
		}
	}

	public FishGroup getGroup()
	{
		return _group;
	}

	public FishGrade getGrade()
	{
		return _grade;
	}

	public double getBiteRate()
	{
		return _biteRate;
	}

	public double getGuts()
	{
		return _guts;
	}

	public double getLengthRate()
	{
		return _lengthRate;
	}

	public double getHpRegen()
	{
		return _hpRegen;
	}

	public double getGutsCheckProbability()
	{
		return _gutsCheckProbability;
	}

	public double getCheatingProb()
	{
		return _cheatingProb;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getHp()
	{
		return _hp;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getMaxLength()
	{
		return _maxLength;
	}

	public int getStartCombatTime()
	{
		return _startCombatTime;
	}

	public int getCombatDuration()
	{
		return _combatDuration;
	}

	public int getGutsCheckTime()
	{
		return _gutsCheckTime;
	}

	public Collection<RewardItem> getRewards()
	{
		return _rewards.values();
	}
}
