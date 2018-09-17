package org.icescene.gamecontrols;

import java.util.HashMap;
import java.util.Map;

import icetone.controls.lists.SlideTray;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.ToolKit;
import icetone.core.event.ElementEvent.Type;
import icetone.core.utils.Alarm.AlarmTask;
import icetone.css.CssEvent;

public class TimedItemBar extends SlideTray {

	public static class TimedItem {
		private final BaseElement element;
		private final float ttl;
		private final float warning;

		public TimedItem(BaseElement element, float ttl) {
			this(element, ttl, ttl * 0.1f);
		}

		public TimedItem(BaseElement element, float ttl, float warning) {
			super();
			if (warning > ttl)
				throw new IllegalArgumentException("Warning must be less than or equal TTL");
			this.element = element;
			this.ttl = ttl;
			this.warning = warning;
		}

		public BaseElement getElement() {
			return element;
		}

		public float getTtl() {
			return ttl;
		}

		public float getWarning() {
			return warning;
		}

	}

	class TimedItemState {
		AlarmTask warningTask;
		AlarmTask hideTask;
		TimedItem item;

		TimedItemState(TimedItem item) {
			this.item = item;
		}

		void cancelTimers() {
			if (warningTask != null)
				warningTask.cancel();
			if (hideTask != null)
				hideTask.cancel();
		}
	}

	private Map<TimedItem, TimedItemState> map = new HashMap<>();

	public TimedItemBar() {
		super();
	}

	public TimedItemBar(BaseScreen screen, Orientation orientation) {
		super(screen, orientation);
	}

	public TimedItemBar(BaseScreen screen) {
		super(screen);
	}

	public TimedItemBar(Orientation orientation) {
		super(orientation);
	}

	public void addTimedItem(TimedItem item) {
		addTrayElement(item.getElement());

		TimedItemState state = new TimedItemState(item);
		startTimers(state);

		/*
		 * Watch for element being hidden externally. In which case we shut down
		 * our timers
		 */
		item.getElement().onElementEvent(evt -> {
			if (!evt.getSource().isAdjusting()) {
				state.cancelTimers();
			}
		}, Type.HIDDEN);

		map.put(item, state);

	}

	protected void startTimers(TimedItemState state) {

		if (state.item.getTtl() != 0) {
			if (state.item.getWarning() != 0)
				state.warningTask = ToolKit.get().getAlarm().timed(() -> {
					state.item.getElement().triggerCssEvent(CssEvent.HIGHLIGHT);
				}, state.item.getTtl() - state.item.getWarning());

			state.hideTask = ToolKit.get().getAlarm().timed(() -> {
				// Hide
				state.item.getElement().runAdjusting(() -> removeTrayElement(state.item.getElement()));
			}, state.item.getTtl());
		}
	}
}
