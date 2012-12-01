package ar.com.hjg.pngj.test;

import java.io.File;
import java.util.List;
import java.util.zip.Deflater;

import ar.com.hjg.pngj.FileHelper;
import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLine;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.ChunkPredicate;
import ar.com.hjg.pngj.chunks.PngChunk;

/**
 * Mirrors an image, along the rows.
 * This works for ALL image types, see also TestPngSuite
 */
public class SampleMirrorImage {

	public static void mirror(File orig, File dest,boolean overwrite) throws Exception {
		PngReader pngr = FileHelper.createPngReader(orig);
		PngWriter pngw = FileHelper.createPngWriter(dest, pngr.imgInfo, overwrite);
		pngr.setUnpackedMode(true); // we dont want to do the unpacking ourselves, we want a sample per array element
		pngw.setUseUnPackedMode(true); // not really necesary here, as we pass the ImageLine, but anyway...
		pngw.copyChunksFirst(pngr, ChunkCopyBehaviour.COPY_ALL_SAFE);
		for (int row = 0; row < pngr.imgInfo.rows; row++) {
			ImageLine line = pngr.readRowInt(row);
			mirrorLineInt(pngr.imgInfo, line.scanline);
			pngw.writeRow(line, row);
		}
		pngw.copyChunksLast(pngr, ChunkCopyBehaviour.COPY_ALL_SAFE);
		pngr.end(); 
		pngw.end();
	}

	private static void mirrorLineInt(ImageInfo imgInfo, int[] line) { // unpacked line
		int aux;
		int channels = imgInfo.channels;
		for (int c1 = 0, c2 = imgInfo.cols - 1; c1 < c2; c1++, c2--) { // swap pixels (not samples!)
			for (int i = 0; i < channels; i++) {
				aux = line[c1 * channels + i];
				line[c1 * channels + i] = line[c2 * channels + i];
				line[c2 * channels + i] = aux;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2 || args[0].equals(args[1])) {
			System.err.println("Arguments: [pngsrc] [pngdest]");
			System.exit(1);
		}
		File file1 = new File(args[0]); 
		File file2 = new File(args[1]);
		mirror(file1,file2,false);
		System.out.printf("Done: %s -> %s\n", file1.toString(),file2.toString());
	}
}