package ru.yandex.practicum.filmorate.servise.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum FilmSortingAction {
	YEAR, LIKES, UNDEFINED;

	@Getter
	private String sortingName;

	public static FilmSortingAction of(String value) {
		FilmSortingAction filterAction;
		for (FilmSortingAction ffa : values()) {
			if (ffa.name().equalsIgnoreCase(value)) {
				return ffa;
			}
		}

		filterAction = FilmSortingAction.UNDEFINED;
		filterAction.setSortingName(value);
		return filterAction;
	}

	private void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}

	public boolean isUndefined() {
		return this == UNDEFINED;
	}

	public static List<String> getValidSortingList() {
		return Arrays.stream(FilmSortingAction.values())
				.filter(f -> !f.equals(FilmSortingAction.UNDEFINED))
				.map(FilmSortingAction::name)
				.map(String::toLowerCase)
				.toList();
	}

}
