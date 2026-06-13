package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum Assessment {
	UNDEFINED, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;

	private int value;

	Assessment() {
		this.value = ordinal();
	}

	public static Assessment of(Integer value) {
		if (value == null) {
			value = 10;
		}

		Assessment result = Assessment.UNDEFINED;
		if (value < 1 || value > 10) {
			result.setValue(value);
			return result;
		}

		result = values()[value];
		result.setValue(result.ordinal());
		return result;
	}

	private void setValue(int value) {
		this.value = value;
	}
}

