package l2r.gameserver.model.actor.instances.player;

import l2r.gameserver.model.StatsSet;
import l2r.gameserver.utils.TimeUtils;

/**
 * Class for the Premium Bonus object
 * @author netvirus
 */

public class PremiumBonus {
    // premium
    private int _bonusId = 0;
    private String _nameBonus = "";
    private String _iconNameBonus = "";
    private boolean _auraBonus = false;
    private boolean _typeBonus = false;
    // rates
    private double _expRate = 1.;
    private double _spRate = 1.;
    private double _dropRate = 1.;
    private double _dropChance = 1.;
    private double _dropAmount = 1.;
    private double _spoilRate = 1.;
    private double _spoilChance = 1.;
    private double _spoilAmount = 1.;
    private double _adenaDropRate = 1.;
    private double _weightLimitRate = 1.;
    private double _craftChance = 1.;
    private double _masterCraftChance = 1.;
    private double _doubleCraftChance = 1.0;
    private double _extractableRate = 1.;
    private double _questDropRate = 1.;
    private double _questRewardRate = 1.;
    private double _petExpRate = 1.;
    private double _raidDropChance = 1.;
    // time
    private int _days = 0;
    private int _hours = 0;
    private int _minutes = 0;
    private long _duration = 0;
    // price
    private int _itemId = 0;
    private int _itemAmount = 0;

    public PremiumBonus() {}

    // Constructor for clone
    public PremiumBonus(int _bonusId, String _nameBonus, boolean _auraBonus, boolean _typeBonus, long _duration, double _expRate, double _spRate, double _dropRate, double _dropChance, double _dropAmount, double _spoilRate, double _spoilChance, double _spoilAmount, double _adenaDropRate, double _weightLimitRate, double _craftChance, double _masterCraftChance, double _doubleCraftChance, double _extractableRate, double _questDropRate, double _questRewardRate, double _petExpRate, double _raidDropChance) {
        this._bonusId = _bonusId;
        this._nameBonus = _nameBonus;
        this._auraBonus = _auraBonus;
        this._typeBonus = _typeBonus;
        this._duration = _duration;
        this._expRate = _expRate;
        this._spRate = _spRate;
        this._dropRate = _dropRate;
        this._dropChance = _dropChance;
        this._dropAmount = _dropAmount;
        this._spoilRate = _spoilRate;
        this._spoilChance = _spoilChance;
        this._spoilAmount = _spoilAmount;
        this._adenaDropRate = _adenaDropRate;
        this._weightLimitRate = _weightLimitRate;
        this._craftChance = _craftChance;
        this._masterCraftChance = _masterCraftChance;
        this._doubleCraftChance = _doubleCraftChance;
        this._extractableRate = _extractableRate;
        this._questDropRate = _questDropRate;
        this._questRewardRate = _questRewardRate;
        this._petExpRate = _petExpRate;
        this._raidDropChance = _raidDropChance;
    }

    // Do clone
    public PremiumBonus(PremiumBonus original) {
        this(
                // premium
                original.getBonusId(),
                original.getBonusName(),
                original.isBonusAuraEnabled(),
                original.isBonusMain(),
                original.getBonusDuration(),
                // rates
                original.getBonusExpRate(),
                original.getBonusSpRate(),
                original.getBonusDropRate(),
                original.getBonusDropChance(),
                original.getBonusDropAmount(),
                original.getBonusSpoilRate(),
                original.getBonusSpoilChance(),
                original.getBonusSpoilAmount(),
                original.getBonusAdenaDropRate(),
                original.getBonusWeightLimitRate(),
                original.getBonusCraftChance(),
                original.getBonusMasterCraftChance(),
                original.getBonusDoubleCraftChance(),
                original.getBonusExtractableRate(),
                original.getBonusQuestDropRate(),
                original.getBonusQuestRewardRate(),
                original.getBonusPetExpRate(),
                original.getBonusRaidDropChance()
        );
    }

    public PremiumBonus(StatsSet set) {
        // premium
        setBonusId(set.getInt("id"));
        setBonusName(set.getString("name"));
        setBonusIconName(set.getString("icon"));
        setBonusAura(set.getBoolean("aura"));
        setBonusType(set.getBoolean("main"));
        // rates
        setBonusExpRate(set.getDouble("expRate"));
        setBonusSpRate(set.getDouble("spRate"));
        setBonusDropRate(set.getDouble("dropRate"));
        setBonusDropChance(set.getDouble("dropChance"));
        setBonusDropAmount(set.getDouble("dropAmount"));
        setBonusSpoilRate(set.getDouble("spoilRate"));
        setBonusSpoilChance(set.getDouble("spoilChance"));
        setBonusSpoilAmount(set.getDouble("spoilAmount"));
        setBonusAdenaDropRate(set.getDouble("adenaDropRate"));
        setBonusWeightLimitRate(set.getDouble("weightLimitRate"));
        setBonusCraftChance(set.getDouble("craftChance"));
        setBonusMasterCraftChance(set.getDouble("mCraftChance"));
        setBonusDoubleCraftChance(set.getDouble("doubleCraftChance"));
        setBonusExtractableRate(set.getDouble("extractableRate"));
        setBonusQuestDropRate(set.getDouble("questDropRate"));
        setBonusQuestRewardRate(set.getDouble("questRewardRate"));
        setBonusPetExpRate(set.getDouble("petExpRate"));
        setBonusRaidDropChance(set.getDouble("raidDropChance"));
        // time
        setBonusDays(set.getInt("days"));
        setBonusHours(set.getInt("hours"));
        setBonusMinutes(set.getInt("minutes"));
        // price
        setBonusItemId(set.getInt("itemId"));
        setBonusItemAmount(set.getInt("itemAmount"));
    }

    /**
     * @return the bonus id.
     */
    public int getBonusId() { return _bonusId; }

    public void setBonusId(int bonusId) { _bonusId = bonusId; }

    /**
     * @return the bonus exp rate.
     */
    public double getBonusExpRate() { return _expRate; }

    public void setBonusExpRate(double expRate) { _expRate = expRate; }

    /**
     * @return the bonus sp rate.
     */
    public double getBonusSpRate() { return _spRate; }

    public void setBonusSpRate(double spRate) { _spRate = spRate; }

    /**
     * @return the bonus drop rate.
     */
    public double getBonusDropRate() { return _dropRate; }

    public void setBonusDropRate(double dropRate) { _dropRate = dropRate; }

    /**
     * @return the bonus drop chance.
     */
    public double getBonusDropChance() { return _dropChance; }

    public void setBonusDropChance(double dropChance) { _dropChance = dropChance; }

    /**
     * @return the bonus drop amount.
     */
    public double getBonusDropAmount() { return _dropAmount; }

    public void setBonusDropAmount(double dropAmount) { _dropAmount = dropAmount; }

    /**
     * @return the bonus spoil rate.
     */
    public double getBonusSpoilRate() { return _spoilRate; }

    public void setBonusSpoilRate(double spoilRate) { _spoilRate = spoilRate; }

    /**
     * @return the bonus spoil chance.
     */
    public double getBonusSpoilChance() { return _spoilChance; }

    public void setBonusSpoilChance(double spoilChance) { _spoilChance = spoilChance; }

    /**
     * @return the bonus spoil amount.
     */
    public double getBonusSpoilAmount() { return _spoilAmount; }

    public void setBonusSpoilAmount(double spoilAmount) { _spoilAmount = spoilAmount; }

    /**
     * @return the bonus drop rate adena.
     */
    public double getBonusAdenaDropRate() { return _adenaDropRate; }

    public void setBonusAdenaDropRate(double adenaDropRate) { _adenaDropRate = adenaDropRate; }

    /**
     * @return the bonus craft chance %.
     */
    public double getBonusCraftChance() { return _craftChance; }

    public void setBonusCraftChance(double craftChance) { _craftChance = craftChance; }

    /**
     * @return the bonus master craft chance %.
     */
    public double getBonusMasterCraftChance() { return _masterCraftChance; }

    public void setBonusMasterCraftChance(double doubleCraftChance) { _doubleCraftChance = doubleCraftChance; }

    /**
     * @return the bonus double craft chance %.
     */
    public double getBonusDoubleCraftChance() { return _doubleCraftChance; }

    public void setBonusDoubleCraftChance(double masterCraftChance) { _masterCraftChance = masterCraftChance; }

    /**
     * @return the bonus weight limit rate.
     */
    public double getBonusWeightLimitRate() { return _weightLimitRate; }

    public void setBonusWeightLimitRate(double weightLimitRate) { _weightLimitRate = weightLimitRate; }

    /**
     * @return the bonus extractable items rate.
     */
    public double getBonusExtractableRate() { return _extractableRate; }

    public void setBonusExtractableRate(double extractableRate) { _extractableRate = extractableRate; }

    /**
     * @return the bonus quest items drop rate.
     */
    public double getBonusQuestDropRate() { return _questDropRate; }

    public void setBonusQuestDropRate(double questDrop) { _questDropRate = questDrop; }

    /**
     * @return the bonus quest items drop rate.
     */
    public double getBonusQuestRewardRate() { return _questRewardRate; }

    public void setBonusQuestRewardRate(double questRewardRate) { _questRewardRate = questRewardRate; }

    /**
     * @return the bonus exp rate for pet.
     */
    public double getBonusPetExpRate() { return _petExpRate; }

    public void setBonusPetExpRate(double petExpRate) { _petExpRate = petExpRate; }

    /**
     * @return the bonus raidboss drop chance.
     */
    public double getBonusRaidDropChance() { return _raidDropChance; }

    public void setBonusRaidDropChance(double raidDropChance) { _raidDropChance = raidDropChance; }

    /**
     * @return the bonus is main or not.
     * The player can have only one main premium
     */
    public boolean isBonusMain() { return _typeBonus; }

    public void setBonusType(boolean typeBonus) { _typeBonus = typeBonus; }

    /**
     * @return the bonus can have abnormal effect
     */
    public boolean isBonusAuraEnabled() { return _auraBonus; }

    public void setBonusAura(boolean auraBonus) { _auraBonus = auraBonus; }

    /**
     * @return the bonus name
     */
    public String getBonusName() { return _nameBonus; }

    public void setBonusName(String nameBonus) { _nameBonus = nameBonus; }

    /**
     * @return the bonus icon name
     */
    public String getBonusIconName() { return _iconNameBonus; }

    public void setBonusIconName(String iconNameBonus) { _iconNameBonus = iconNameBonus; }

    /**
     * @return the bonus days
     */
    public int getBonusDayes() { return _days; }

    public void setBonusDays(int days) { _days = days; }

    /**
     * @return the bonus hours
     */
    public int getBonusHours() { return _hours; }

    public void setBonusHours(int hours) { _hours = hours; }

    /**
     * @return the bonus minutes
     */
    public int getBonusMinutes() { return _minutes; }

    public void setBonusMinutes(int minutes) { _minutes = minutes; }

    /**
     * @return the bonus itemId
     */
    public int getBonusItemId() { return _itemId; }

    public void setBonusItemId(int itemId) { _itemId = itemId; }

    /**
     * @return the bonus minutes
     */
    public int getBonusItemAmount() { return _itemAmount; }

    public void setBonusItemAmount(int itemAmount) { _itemAmount = itemAmount; }

    /**
     * @return Profile the bonus duration in millisec
     */
    public long getBonusDurationFromProfile()
    {
        return TimeUtils.getMillisecondsFromDaysHoursMinutes(getBonusDayes(), getBonusHours(), getBonusMinutes());
    }

    public long getBonusDuration()
    {
        return _duration;
    }

    public void setBonusDuration(long duration)
    {
        _duration = duration;
    }
}
