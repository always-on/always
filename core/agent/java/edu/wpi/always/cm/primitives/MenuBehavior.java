package edu.wpi.always.cm.primitives;

import java.util.*;

import com.google.common.collect.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

public class MenuBehavior extends PrimitiveBehavior {

	private final List<String> items;
	private final boolean twoColumn;

	public MenuBehavior(List<String> items) {
		this(items, false);
	}

	public MenuBehavior(List<String> items, boolean twoColumn) {
		this.items = Collections.unmodifiableList(Lists.newArrayList(items));
		this.twoColumn = twoColumn;
	}

	@Override
	public Resource getResource() {
		return Resource.Menu;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + (twoColumn ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuBehavior other = (MenuBehavior) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;

		if (twoColumn != other.twoColumn)
			return false;

		return true;
	}

	public List<String> getItems() {
		return items;
	}

	public boolean isTwoColumn() {
		return twoColumn;
	}

	@Override
	public String toString() {
		return "Menu " + itemsCombined() + (isTwoColumn() ? " in Two columns" : "");
	}

	private String itemsCombined() {
		if (items.isEmpty())
			return "{}";

		StringBuilder sb = new StringBuilder();
		sb.append("{ ");

		for (String s : items) {
			sb.append(s);
			sb.append(", ");
		}

		return sb
				.delete(sb.length() - 2, sb.length())
				.append("}")
				.toString();
	}
}
