package l2r.gameserver.handler.voicecommands.impl;

import l2r.gameserver.Config;
import l2r.gameserver.data.htm.HtmCache;
import l2r.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2r.gameserver.model.Creature;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.base.Element;
import l2r.gameserver.model.items.ItemInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.stats.Formulas;
import l2r.gameserver.stats.Stats;
import l2r.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2r.gameserver.utils.Strings;

import java.text.NumberFormat;
import java.util.Locale;

public class WhoAmI implements IVoicedCommandHandler
{
	private final String[] _commandList = new String[] { "whoami", "whoiam" };

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if (!Config.ALLOW_WHOAMI_COMMAND)
			return false;
		
		Creature target = null;
		
		double hpRegen = Formulas.calcHpRegen(player);
		double cpRegen = Formulas.calcCpRegen(player);
		double mpRegen = Formulas.calcMpRegen(player);
		double hpDrain = player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., target, null);
		double mpDrain = player.calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0., target, null);
		double hpGain = player.calcStat(Stats.HEAL_EFFECTIVNESS, 100., target, null);
		double mpGain = player.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., target, null);
		double critPerc = 2 * player.calcStat(Stats.CRITICAL_DAMAGE, target, null);
		double critStatic = player.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, null);
		double mCritRate = player.calcStat(Stats.MCRITICAL_RATE, target, null);
		double blowRate = player.calcStat(Stats.FATALBLOW_RATE, target, null);

		ItemInstance shld = player.getSecondaryWeaponInstance();
		boolean shield = shld != null && shld.getItemType() == WeaponType.NONE;

		double shieldDef = shield ? player.calcStat(Stats.SHIELD_DEFENCE, player.getTemplate().getBaseShldDef(), target, null) : 0.;
		double shieldRate = shield ? player.calcStat(Stats.SHIELD_RATE, target, null) : 0.;

		double fireResist = player.calcStat(Element.FIRE.getDefence(), 0., target, null);
		double windResist = player.calcStat(Element.WIND.getDefence(), 0., target, null);
		double waterResist = player.calcStat(Element.WATER.getDefence(), 0., target, null);
		double earthResist = player.calcStat(Element.EARTH.getDefence(), 0., target, null);
		double holyResist = player.calcStat(Element.HOLY.getDefence(), 0., target, null);
		double unholyResist = player.calcStat(Element.UNHOLY.getDefence(), 0., target, null);

		double bleedPower = player.calcStat(Stats.BLEED_POWER, target, null);
		double bleedResist = player.calcStat(Stats.BLEED_RESIST, target, null);
		double poisonPower = player.calcStat(Stats.POISON_POWER, target, null);
		double poisonResist = player.calcStat(Stats.POISON_RESIST, target, null);
		double stunPower = player.calcStat(Stats.STUN_POWER, target, null);
		double stunResist = player.calcStat(Stats.STUN_RESIST, target, null);
		double rootPower = player.calcStat(Stats.ROOT_POWER, target, null);
		double rootResist = player.calcStat(Stats.ROOT_RESIST, target, null);
		double sleepPower = player.calcStat(Stats.SLEEP_POWER, target, null);
		double sleepResist = player.calcStat(Stats.SLEEP_RESIST, target, null);
		double paralyzePower = player.calcStat(Stats.PARALYZE_POWER, target, null);
		double paralyzeResist = player.calcStat(Stats.PARALYZE_RESIST, target, null);
		double mentalPower = player.calcStat(Stats.MENTAL_POWER, target, null);
		double mentalResist = player.calcStat(Stats.MENTAL_RESIST, target, null);
		double debuffPower = player.calcStat(Stats.DEBUFF_POWER, target, null);
		double debuffResist = player.calcStat(Stats.DEBUFF_RESIST, target, null);
		double cancelPower = player.calcStat(Stats.CANCEL_POWER, target, null);
		double cancelResist = player.calcStat(Stats.CANCEL_RESIST, target, null);

		double swordResist = 100. - player.calcStat(Stats.SWORD_WPN_VULNERABILITY, target, null);
		double dualResist = 100. - player.calcStat(Stats.DUAL_WPN_VULNERABILITY, target, null);
		double bluntResist = 100. - player.calcStat(Stats.BLUNT_WPN_VULNERABILITY, target, null);
		double daggerResist = 100. - player.calcStat(Stats.DAGGER_WPN_VULNERABILITY, target, null);
		double bowResist = 100. - player.calcStat(Stats.BOW_WPN_VULNERABILITY, target, null);
		double crossbowResist = 100. - player.calcStat(Stats.CROSSBOW_WPN_VULNERABILITY, target, null);
		double poleResist = 100. - player.calcStat(Stats.POLE_WPN_VULNERABILITY, target, null);
		double fistResist = 100. - player.calcStat(Stats.FIST_WPN_VULNERABILITY, target, null);

		double critChanceResist = 100. - player.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, target, null);
		double critDamResistStatic = player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, target, null);
		double critDamResist = 100. - 100 * (player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1., target, null) - critDamResistStatic);

		String dialog = HtmCache.getInstance().getNotNull("command/whoami.htm", player);

		NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);

		dialog = dialog.replace("%hpRegen%", df.format(hpRegen));
		dialog = dialog.replace("%cpRegen%", df.format(cpRegen));
		dialog = dialog.replace("%mpRegen%", df.format(mpRegen));
		dialog = dialog.replace("%hpDrain%", df.format(hpDrain));
		dialog = dialog.replace("%mpDrain%", df.format(mpDrain));
		dialog = dialog.replace("%hpGain%", df.format(hpGain));
		dialog = dialog.replace("%mpGain%", df.format(mpGain));
		dialog = dialog.replace("%critPerc%", df.format(critPerc));
		dialog = dialog.replace("%critStatic%", df.format(critStatic));
		dialog = dialog.replace("%mCritRate%", df.format(mCritRate));
		dialog = dialog.replace("%blowRate%", df.format(blowRate));
		dialog = dialog.replace("%shieldDef%", df.format(shieldDef));
		dialog = dialog.replace("%shieldRate%", df.format(shieldRate));
		dialog = dialog.replace("%fireResist%", df.format(fireResist));
		dialog = dialog.replace("%windResist%", df.format(windResist));
		dialog = dialog.replace("%waterResist%", df.format(waterResist));
		dialog = dialog.replace("%earthResist%", df.format(earthResist));
		dialog = dialog.replace("%holyResist%", df.format(holyResist));
		dialog = dialog.replace("%darkResist%", df.format(unholyResist));
		dialog = dialog.replace("%bleedPower%", df.format(bleedPower));
		dialog = dialog.replace("%bleedResist%", df.format(bleedResist));
		dialog = dialog.replace("%poisonPower%", df.format(poisonPower));
		dialog = dialog.replace("%poisonResist%", df.format(poisonResist));
		dialog = dialog.replace("%stunPower%", df.format(stunPower));
		dialog = dialog.replace("%stunResist%", df.format(stunResist));
		dialog = dialog.replace("%rootPower%", df.format(rootPower));
		dialog = dialog.replace("%rootResist%", df.format(rootResist));
		dialog = dialog.replace("%sleepPower%", df.format(sleepPower));
		dialog = dialog.replace("%sleepResist%", df.format(sleepResist));
		dialog = dialog.replace("%paralyzePower%", df.format(paralyzePower));
		dialog = dialog.replace("%paralyzeResist%", df.format(paralyzeResist));
		dialog = dialog.replace("%mentalPower%", df.format(mentalPower));
		dialog = dialog.replace("%mentalResist%", df.format(mentalResist));
		dialog = dialog.replace("%debuffPower%", df.format(debuffPower));
		dialog = dialog.replace("%debuffResist%", df.format(debuffResist));
		dialog = dialog.replace("%cancelPower%", df.format(cancelPower));
		dialog = dialog.replace("%cancelResist%", df.format(cancelResist));
		dialog = dialog.replace("%swordResist%", df.format(swordResist));
		dialog = dialog.replace("%dualResist%", df.format(dualResist));
		dialog = dialog.replace("%bluntResist%", df.format(bluntResist));
		dialog = dialog.replace("%daggerResist%", df.format(daggerResist));
		dialog = dialog.replace("%bowResist%", df.format(bowResist));
		dialog = dialog.replace("%crosdialogowResist%", df.format(crossbowResist));
		dialog = dialog.replace("%fistResist%", df.format(fistResist));
		dialog = dialog.replace("%poleResist%", df.format(poleResist));
		dialog = dialog.replace("%critChanceResist%", df.format(critChanceResist));
		dialog = dialog.replace("%critDamResist%", df.format(critDamResist));
		NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setHtml(Strings.bbParse(dialog));
		player.sendPacket(msg);

		return true;
	}
}
