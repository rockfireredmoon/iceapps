package org.icescene.configuration;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.icelib.Color;
import org.icelib.RGB;

public class PNGPaletteReader extends LinkedHashMap<Integer, RGB> {

	static class Chunk {

		private final long dataLength;
		private final String type;
		private byte[] data;
		private long crc;
		private final DataInputStream din;

		Chunk(DataInputStream din) throws IOException {
			this.din = din;
			dataLength = readUnsignedInt(din);
			byte[] t = new byte[4];
			din.readFully(t);
			type = new String(t, "ASCII");
		}

		void read() throws IOException {
			data = new byte[(int) dataLength];
			din.readFully(data);
			readCrc();
		}

		void skip() throws IOException {
			din.skip(dataLength);
			readCrc();
		}

		void readCrc() throws IOException {
			crc = readUnsignedInt(din);
		}

		private long readUnsignedInt(DataInputStream din) throws IOException {
			return din.readInt() & 0xffffffff;
		}
	}

	private Map<RGB, Integer> reverseMap = new LinkedHashMap<RGB, Integer>();
	private int alpha = -1;

	public PNGPaletteReader() {
	}

	public PNGPaletteReader(InputStream in) throws IOException {
		read(in);
	}

	public int getAlphaIndex() {
		return alpha;
	}

	public int getIndex(RGB color) {
		return reverseMap.containsKey(color) ? reverseMap.get(color) : -1;
	}

	public void read(InputStream in) throws IOException {
		DataInputStream din = new DataInputStream(in);
		byte[] header = new byte[8];
		din.readFully(header);

		Chunk palette = null;
		while (true) {
			try {
				Chunk chunk = new Chunk(din);
				if (chunk.type.equals("PLTE")) {
					chunk.read();
					palette = chunk;
					break;
				} else if (chunk.type.equals("tRNS")) {
					// http://www.imagemagick.org/discourse-server/viewtopic.php?t=17980
					chunk.read();
					alpha = chunk.data[0] & 0xFF;
					break;
				} else {
					chunk.skip();
				}
			} catch (EOFException eof) {
				break;
			}
		}

		if (palette == null) {
			throw new IOException("No palette chunk found");
		}

		int i = 0;
		int idx = 0;
		while (i < palette.dataLength) {
			int r = palette.data[i++] & 0xFF;
			int g = palette.data[i++] & 0xFF;
			int b = palette.data[i++] & 0xFF;
			RGB col;
			if (idx == alpha)
				col = new Color(r, g, b, 0);
			else
				col = new Color(r, g, b);
			put(idx, col);
			reverseMap.put(col, idx);
			idx++;
		}
	}
}
