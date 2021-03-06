package org.icescene.configuration;

import java.util.ArrayList;
import java.util.List;

import org.icelib.Color;
import org.icelib.ColourPalette;
import org.icelib.beans.MappedMap;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.Service;
import org.iceui.IceUI;

import com.jme3.math.ColorRGBA;

@Service
public class ColourPalettes extends MappedMap<String, ColourPalette> implements IcesceneService {

	private static final long serialVersionUID = 1L;

	public ColourPalettes() {
		super(String.class, ColourPalette.class);
	}

	@Override
	public void init(IcesceneApp app) {
	}

	public ColourPalette create(List<String> arr) {
		ColourPalette p = new ColourPalette("");
		for (String a : arr) {
			p.add(new Color(a));
		}
		return p;
	}

	public static List<ColorRGBA> toColourList(ColourPalette pal) {
		List<ColorRGBA> cc = new ArrayList<>();
		for (Color c : pal) {
			cc.add(IceUI.toRGBA(c));
		}
		return cc;
	}

	public ColourPalette unionPalettes(ColourPalette pal1, ColourPalette pal2) {
		ColourPalette pal3 = new ColourPalette("");
		pal3.addAll(pal1);
		for (int i = 0; i < pal2.size(); i++) {
			pal3.set(i, pal2.get(i));
		}
		return pal3;
	}
}
