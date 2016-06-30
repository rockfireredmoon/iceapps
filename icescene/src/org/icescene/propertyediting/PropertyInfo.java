package org.icescene.propertyediting;

import java.lang.reflect.Method;
import java.util.List;

import org.icescene.propertyediting.Property.Hint;

public class PropertyInfo<T extends PropertyBean> implements Comparable<PropertyInfo> {

	public static class Range<T extends Number> {

		private T min;
		private T max;
		private T incr;
		private int precision;

		public Range(T min, T max, T incr) {
			this.min = min;
			this.max = max;
			this.incr = incr;
		}

		public T getMin() {
			return min;
		}

		public T getMax() {
			return max;
		}

		public T getIncr() {
			return incr;
		}

		public void setMin(T min) {
			this.min = min;
		}

		public void setMax(T max) {
			this.max = max;
		}

		public void setIncr(T incr) {
			this.incr = incr;
		}

		public int getPrecision() {
			return precision;
		}

		public void setPrecision(int precision) {
			this.precision = precision;
		}

	}

	private Method getter;
	private Method setter;
	private String id;
	private String eventId;
	private String label = "";
	private Class<?> clazz;
	private int weight;
	private T object;
	private List<String> options;
	private Property.Hint hint = Hint.UNSET;
	private Range<?> range = null;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public T getObject() {
		return object;
	}

	public Range<?> getRange() {
		return range;
	}

	public void setRange(Range<?> range) {
		this.range = range;
	}

	public Property.Hint getHint() {
		return hint;
	}

	public void setHint(Property.Hint hint) {
		this.hint = hint;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public int compareTo(PropertyInfo o) {
		return new Integer(weight).compareTo(o.weight);
	}

	public boolean isReadable() {
		return getter != null;
	}

	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	public boolean isWriteable() {
		return setter != null;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "PropertyInfo{" + "id=" + id + ", label=" + label + ", clazz=" + clazz + ", weight=" + weight + ", object=" + object
				+ ", options=" + options + ", hint=" + hint + ", range=" + range + '}';
	}
}
