package l2r.gameserver.model.base;

import java.util.Objects;
import java.util.Optional;

public enum Race
{
	human,
	elf,
	darkelf,
	orc,
	dwarf,
	kamael;

	public static Optional<Race> value(final String name) {
		for (final Race playerRace : Race.values()) {
			if (Objects.equals(playerRace.name(), name)) {
				return Optional.of(playerRace);
			}
		}
		return Optional.empty();
	}

	public int getId() {
		return ordinal();
	}
}
