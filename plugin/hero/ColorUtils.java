package plugin.hero;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import com.alee.utils.*;

public class ColorUtils {
	public static int argb(int R, int G, int B) {
		return argb(Byte.MAX_VALUE, R, G, B);
	}

	public static int argb(int A, int R, int G, int B) {
		byte[] colorByteArr = {(byte) A, (byte) R, (byte) G, (byte) B};
		return byteArrToInt(colorByteArr);
	}

	public static int byteArrToInt(byte[] colorByteArr) {
		return (colorByteArr[0] << 24) + ((colorByteArr[1] & 0xFF) << 16) + ((colorByteArr[2] & 0xFF) << 8)
				+ (colorByteArr[3] & 0xFF);
	}

	/**
	 * Converts the source to 1-bit colour depth (monochrome). No transparency.
	 * 
	 * @param src the source image to convert
	 * @return a copy of the source image with a 1-bit colour depth.
	 */
	public static BufferedImage convert1(BufferedImage src) {
		IndexColorModel icm = new IndexColorModel(1, 2, new byte[]{(byte) 0, (byte) 0xFF},
				new byte[]{(byte) 0, (byte) 0xFF}, new byte[]{(byte) 0, (byte) 0xFF});

		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY, icm);

		ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(),
				dest.getColorModel().getColorSpace(), null);

		cco.filter(src, dest);

		return dest;
	}

	/**
	 * Converts the source image to 24-bit colour (RGB). No transparency.
	 * 
	 * @param src the source image to convert
	 * @return a copy of the source image with a 24-bit colour depth
	 */
	public static BufferedImage convert24(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
		ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(),
				dest.getColorModel().getColorSpace(), null);
		cco.filter(src, dest);
		return dest;
	}

	/**
	 * Converts the source image to 32-bit colour with transparency (ARGB).
	 * 
	 * @param src the source image to convert
	 * @return a copy of the source image with a 32-bit colour depth.
	 */
	public static BufferedImage convert32(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
		ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(),
				dest.getColorModel().getColorSpace(), null);
		cco.filter(src, dest);
		return dest;
	}

	/**
	 * Converts the source image to 4-bit colour using the default 16-colour palette:
	 * <ul>
	 * <li>black</li>
	 * <li>dark red</li>
	 * <li>dark green</li>
	 * <li>dark yellow</li>
	 * <li>dark blue</li>
	 * <li>dark magenta</li>
	 * <li>dark cyan</li>
	 * <li>dark grey</li>
	 * <li>light grey</li>
	 * <li>red</li>
	 * <li>green</li>
	 * <li>yellow</li>
	 * <li>blue</li>
	 * <li>magenta</li>
	 * <li>cyan</li>
	 * <li>white</li>
	 * </ul>
	 * No transparency.
	 * 
	 * @param src the source image to convert
	 * @return a copy of the source image with a 4-bit colour depth, with the default colour pallette
	 */
	public static BufferedImage convert4(BufferedImage src) {
		int[] cmap = new int[]{0x000000, 0x800000, 0x008000, 0x808000, 0x000080, 0x800080, 0x008080, 0x808080, 0xC0C0C0,
				0xFF0000, 0x00FF00, 0xFFFF00, 0x0000FF, 0xFF00FF, 0x00FFFF, 0xFFFFFF};
		return convert4(src, cmap);
	}

	/**
	 * Converts the source image to 4-bit colour using the given colour map. No transparency.
	 * 
	 * @param src the source image to convert
	 * @param cmap the colour map, which should contain no more than 16 entries The entries are in the form RRGGBB
	 *        (hex).
	 * @return a copy of the source image with a 4-bit colour depth, with the custom colour pallette
	 */
	public static BufferedImage convert4(BufferedImage src, int[] cmap) {
		IndexColorModel icm = new IndexColorModel(4, cmap.length, cmap, 0, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY, icm);
		ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(),
				dest.getColorModel().getColorSpace(), null);
		cco.filter(src, dest);

		return dest;
	}

	public static BufferedImage convert4to32(BufferedImage src) {
		BufferedImage bi = convert4(src);
		BufferedImage tmp = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tmp.createGraphics();
		g2.drawImage(bi, 0, 0, null);
		g2.dispose();
		return tmp;

	}

	/**
	 * Converts the source image to 8-bit colour using the default 256-colour palette. No transparency.
	 * 
	 * @param src the source image to convert
	 * @return a copy of the source image with an 8-bit colour depth
	 */
	public static BufferedImage convert8(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
		ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(),
				dest.getColorModel().getColorSpace(), null);
		cco.filter(src, dest);
		return dest;
	}

	public static Rectangle findArea(BufferedImage image, int x, int y, int coldif) {
		Color tocol = Color.PINK;
		Color fromcol = new Color(image.getRGB(x, y));
		BufferedImage copy = ImageUtils.createCompatibleImage(image);
		boolean init = true;
		flood(copy, x, y, fromcol, tocol, coldif);
		Rectangle rec = new Rectangle();
		for (int px = 0; px < copy.getWidth(); px++) {
			for (int py = 0; py < copy.getHeight(); py++) {
				if (copy.getRGB(px, py) == tocol.getRGB()) {
					// the first time
					if (init) {
						rec.x = px;
						rec.y = py;
						init = false;
					} else {
						rec.add(px, py);
					}
				}
			}
		}

		return rec;
	}

	public static void flood(BufferedImage image, int x, int y, Color fromcol, Color tocol, int coldif) {
		if (x >= 1 && y >= 1 && x < image.getWidth() && y < image.getHeight()) {
			Color c2 = new Color(image.getRGB(x, y));
			// if this point was painted already
			if (c2.equals(tocol)) {
				return;
			}

			// outside of color diference
			if (getColorDifference(c2, fromcol) > coldif) {
				return;
			}

			// if (Math.abs(c2.getGreen() - fromcol.getGreen()) < 10 && Math.abs(c2.getRed() - fromcol.getRed()) <
			// 10
			// && Math.abs(c2.getBlue() - fromcol.getBlue()) < 10) {
			image.setRGB(x, y, tocol.getRGB());
			flood(image, x, y + 1, fromcol, tocol, coldif);
			flood(image, x + 1, y, fromcol, tocol, coldif);
			flood(image, x - 1, y, fromcol, tocol, coldif);
			flood(image, x, y - 1, fromcol, tocol, coldif);
		}
	}

	/**
	 * * Computes the difference between two RGB colors by converting them to the L*a*b scale and comparing them using
	 * the CIE76 algorithm { http://en.wikipedia.org/wiki/Color_difference#CIE76}
	 * <p>
	 * the values for this method range from 1.0 to 100. where
	 * <ul>
	 * <li>1.0 no jperceptible by human eye
	 * <li>1-2 perceptible by close observation
	 * <li>2-10 perceptible at glance
	 * <li>11-49 color are more similar than oposite
	 * <li>100 color are oposites
	 * 
	 * @param colorA - color colorA
	 * @param colorB - color colorB
	 * @return delta e diference
	 */
	public static double getColorDifference(Color colorA, Color colorB) {
		int r1, g1, b1, r2, g2, b2;
		r1 = colorA.getRed();
		g1 = colorA.getGreen();
		b1 = colorA.getBlue();
		r2 = colorB.getRed();
		g2 = colorB.getGreen();
		b2 = colorB.getBlue();
		int[] lab1 = rgb2lab(r1, g1, b1);
		int[] lab2 = rgb2lab(r2, g2, b2);
		return Math
				.sqrt(Math.pow(lab2[0] - lab1[0], 2) + Math.pow(lab2[1] - lab1[1], 2) + Math.pow(lab2[2] - lab1[2], 2));
	}

	public static int getColorModel(BufferedImage image) {
		return image.getColorModel().getNumColorComponents();
	}

	/**
	 * Return a {@link Hashtable} where the key is an Integer representing the color and the value the number of pixels
	 * present whit this color. The key value (the color of pixes) is in AARRGGBB fomrmat.
	 * <p>
	 * NOTE: if the transparency is present in the image, this method will count the result color. in this case, make
	 * suse that the image is opaque
	 * 
	 * @param image - Source image
	 * @return Histogram of colors
	 */
	public static Hashtable<Integer, Integer> getHistogram(BufferedImage image) {
		Hashtable<Integer, Integer> histo = new Hashtable<>();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int col = image.getRGB(x, y);
				// col = col & 0x00ffffff;
				Integer icnt = histo.get(col);
				int cnt = icnt == null ? 1 : icnt.intValue() + 1;
				histo.put(col, cnt);
			}
		}
		return histo;
	}

	public static double getHSBColorDistance(Color c1, Color c2) {
		float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);

		double dh = Math.min(Math.abs(hsb2[0] - hsb1[0]), 360 - Math.abs(hsb2[0] - hsb1[0])) / 180.0;
		double ds = Math.abs(hsb2[1] - hsb1[1]);
		double dv = Math.abs(hsb2[2] - hsb1[2]) / 255.0;
		// Each of these values will be in the range [0,1]. You can compute the length of this tuple:

		return Math.sqrt(dh * dh + ds * ds + dv * dv);
	}

	/**
	 * Return the Color that has most presence or more frequent in the image.
	 * 
	 * @param image - source image
	 * @return the most frequent color
	 */
	public static Color getMaxColor(BufferedImage image) {
		Hashtable<Integer, Integer> histo = getHistogram(image);
		Vector<Integer> ks = new Vector<>(histo.keySet());
		int max = -1;
		int color = Color.pink.getRGB(); // if no color is present, return pink
		for (Integer col : ks) {
			int cnt = histo.get(col);
			if (max < cnt) {
				max = cnt;
				color = col;
			}
		}
		return new Color(color);
	}

	/**
	 * Return the string representation of the color argument. The input parameter is expected be a color value in
	 * format AARRGGBB and this method ignore the alpha section.
	 * 
	 * @return color representation in format RRGGBB
	 */
	public static String getRGBColor(Color col) {
		int c = col.getRGB();
		String mc = Integer.toHexString(c).substring(2);
		return mc;
	}

	public static double getRGBColorDistance(Color c1, Color c2) {
		long rmean = (c1.getRed() + c2.getRed()) / 2;
		long r = (long) c2.getRed() - (long) c1.getRed();
		long g = (long) c2.getGreen() - (long) c1.getGreen();
		long b = (long) c2.getBlue() - (long) c1.getBlue();
		return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
	}

	/**
	 * Return the percentage of white color present in the image <code>image</code>
	 * 
	 * @param image - image o scan
	 * @return white color percentage
	 */
	public static double getWhitePercent(BufferedImage image) {
		Hashtable<Integer, Integer> histo = getHistogram(image);
		float tot = image.getWidth() * image.getHeight();
		int wcnt = histo.get(0xFFFFFFFF) == null ? 0 : histo.get(0xFFFFFFFF); // pure white WHIT ALPHA
		double d = (wcnt / tot * 100);
		int t = (int) (d * 100); // decimal reduction
		return t / 100d;
	}

	/**
	 * Return the percentage of white color present in the image <code>image</code> converting firt the image to color
	 * detph 4 bit
	 * 
	 * @see #convert4(BufferedImage)
	 * @param image - image
	 * @return white color percentage
	public static double getWhitePercent4(BufferedImage image) {
		BufferedImage img4 = convert4(image);
		return getWhitePercent(img4);
	}

	public static void MarkSimilar(BufferedImage image, int x, int y, Color fromcol, int coldif) {
		if (x >= 1 && y >= 1 && x < image.getWidth() && y < image.getHeight()) {
			// find the color at point x, y
			Color c2 = new Color(image.getRGB(x, y));

			// if (!c2.equals(Color.PINK) && getColorDifference(c2, fromcol) < coldif) {
			if (!c2.equals(Color.PINK)) {
				image.setRGB(x, y, Color.PINK.getRGB());
				MarkSimilar(image, x, y + 1, fromcol, coldif);
				MarkSimilar(image, x + 1, y, fromcol, coldif);
				MarkSimilar(image, x - 1, y, fromcol, coldif);
				MarkSimilar(image, x, y - 1, fromcol, coldif);
			}
		}
	}

	public static BufferedImage negative(BufferedImage image) {
		short[] negative = new short[256 * 1];
		for (int i = 0; i < 256; i++)
			negative[i] = (short) (255 - i);
		ShortLookupTable table = new ShortLookupTable(0, negative);
		LookupOp op = new LookupOp(table, null);
		return op.filter(image, null);
	}
	public static int[] rgb(int argb) {
		return new int[]{(argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF};
	}

	/**
	 * Convert from RBG color space to LAB color space
	 * 
	 * @param R - red component
	 * @param G - green component
	 * @param B - blue component
	 * 
	 * @return LAB
	 */
	public static int[] rgb2lab(int R, int G, int B) {
		// http://www.brucelindbloom.com

		float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float Ls, as, bs;
		float eps = 216.f / 24389.f;
		float k = 24389.f / 27.f;

		float Xr = 0.964221f; // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;

		// RGB to XYZ
		r = R / 255.f; // R 0..1
		g = G / 255.f; // G 0..1
		b = B / 255.f; // B 0..1

		// TODO: check this error: he code above has an error in rgb2lab: division by 12 should be replaced by division
		// by 12.92 in r, g and b conversion. otherwise the function is not continuous at r = 0.04045
		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r / 12;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045)
			g = g / 12;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045)
			b = b / 12;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
		Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
		Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

		// XYZ to Lab
		xr = X / Xr;
		yr = Y / Yr;
		zr = Z / Zr;

		if (xr > eps)
			fx = (float) Math.pow(xr, 1 / 3.);
		else
			fx = (float) ((k * xr + 16.) / 116.);

		if (yr > eps)
			fy = (float) Math.pow(yr, 1 / 3.);
		else
			fy = (float) ((k * yr + 16.) / 116.);

		if (zr > eps)
			fz = (float) Math.pow(zr, 1 / 3.);
		else
			fz = (float) ((k * zr + 16.) / 116);

		Ls = (116 * fy) - 16;
		as = 500 * (fx - fy);
		bs = 200 * (fy - fz);

		int[] lab = new int[3];
		lab[0] = (int) (2.55 * Ls + .5);
		lab[1] = (int) (as + .5);
		lab[2] = (int) (bs + .5);
		return lab;
	}

	public static BufferedImage Sharpen(BufferedImage image) {
		float[] elements = {0.0f, -1.0f, 0.0f, -1.0f, 5.f, -1.0f, 0.0f, -1.0f, 0.0f};
		return convolve(image, elements);
	}

	private static BufferedImage convolve(BufferedImage image, float[] elements) {
		Kernel kernel = new Kernel(3, 3, elements);
		ConvolveOp op = new ConvolveOp(kernel);
		return op.filter(image, null);
	}
}