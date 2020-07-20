/**
 * 
 */
package com.java.example.reports.utils;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * @author Mangesh
 * @date 20 Jul 2020
 * @company self
 *
 */
public class MyPDFTextStripper extends PDFTextStripper {

	public MyPDFTextStripper() throws IOException {
		super();
	}

	@Override
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
		for (TextPosition textPos : textPositions) {
			System.out
					.println("String[" + textPos.getXDirAdj() + "," + textPos.getYDirAdj() + " fs=" + textPos.getFontSize()
							+ " xscale=" + textPos.getXScale() + " height=" + textPos.getHeightDir() + " space="
							+ textPos.getWidthOfSpace() + " width=" + textPos.getWidthDirAdj() + "]" + textPos.getUnicode());
		}
	}
}
