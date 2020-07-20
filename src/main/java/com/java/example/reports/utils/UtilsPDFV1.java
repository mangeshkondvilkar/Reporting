package com.java.example.reports.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Component;

/**
 * @author Mangesh
 * @date 8 Jul 2020
 * @company self
 *
 */
@Component
public class UtilsPDFV1 {

	public static final ArrayList<String> PAGE_HEADER_LIST_PASCAL = new ArrayList<>();
	static {
		PAGE_HEADER_LIST_PASCAL.add("RISK AND RETURN ANALYSIS");
		PAGE_HEADER_LIST_PASCAL.add("ONE YEAR RISK RETURN OF UNDERLYING INVESTMENTS");
		PAGE_HEADER_LIST_PASCAL.add("HOLDINGS ANALYSIS");
	}

	public static final ArrayList<String> PAGE_HEADER_LIST_UOB = new ArrayList<>();
	static {
		PAGE_HEADER_LIST_UOB.add("Total Asset Value");
		PAGE_HEADER_LIST_UOB.add("Loans Exposure");
		PAGE_HEADER_LIST_UOB.add("XAU Gold");
		PAGE_HEADER_LIST_UOB.add("Unit Trust Leverage Financing");
		PAGE_HEADER_LIST_UOB.add("Shares - Cash");
		PAGE_HEADER_LIST_UOB.add("Structured Notes");
		PAGE_HEADER_LIST_UOB.add("Structured Deposits");
		PAGE_HEADER_LIST_UOB.add("Maxi Yield");
		PAGE_HEADER_LIST_UOB.add("Equity-Linked Notes");
		PAGE_HEADER_LIST_UOB.add("Time Deposits");
		PAGE_HEADER_LIST_UOB.add("Savings Account");
		PAGE_HEADER_LIST_UOB.add("Portfolio - Financing");
	}

	public static final String PDF_SPLIT_PART_1 = "initial";

	public static HashMap<String, Integer> splitPDFByKeys(String fileName, String[] keys) throws IOException {
		// Open the file
		File file = new File(fileName);
//		HashMap<String, File> keyToFile = new HashMap<>();
//		HashMap<String, ArrayList<Integer>> keyToStartEndPages = new HashMap<>();
		HashMap<String, Integer> keyToPageNo = new HashMap<>();

		int size = 0;

		try (PDDocument doc = PDDocument.load(new File(fileName));) {
			size = doc.getNumberOfPages();
			// split the PDF

			keyToPageNo = getThePagesOfKeys(file, keys, size);
			// keyToStartEndPages = getStartEndPagesOfKeys(keys, keyToPageNo, size);
			// keyToFile = getFileOfKeys(file, fileName, keyToStartEndPages);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		return keyToPageNo;
	}

	public static HashMap<String, Integer> getThePagesOfKeys(File file, String[] keys, int size) {
		return getThePagesOfKeys(file, keys, 1, size);
	}

	public static HashMap<String, Integer> getThePagesOfKeys(File file, String[] keys, int startPage, int endPage) {
		int keySize = keys.length;

		HashMap<String, Integer> keyToPage = new HashMap<>();

		for (int i = startPage; i <= endPage; i++) {
			String thisPage = pdftoText(file, i, i);
			for (int j = 0; j < keySize; j++) {
				if (keyToPage.get(keys[j]) == null) {
					if (thisPage.contains(keys[j])) {
						keyToPage.put(keys[j], i);
						break;
					} else {
						keyToPage.put(keys[j], null);
					}
				}
			}

		}
		return keyToPage;
	}

	public static String pdftoText(File file, int size) {
		return pdftoText(file, 1, size);
	}

	public static String pdftoText(final File file, int startPage, int endPage) {
		String parsedText = null;
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;

		try {
			pdDoc = PDDocument.load(file);
			pdfStripper = new PDFTextStripper();
			pdfStripper.setStartPage(startPage);
			pdfStripper.setEndPage(endPage);

			parsedText = pdfStripper.getText(pdDoc);
		} catch (Exception e) {
			System.out.println(String.format("An exception occured in parsing the PDF Document. {}", e.getMessage()));

		} finally {
			try {
				if (pdDoc != null) {
					IOUtils.closeQuietly(pdDoc);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return parsedText;
	}

	public static HashMap<String, ArrayList<Integer>> getStartEndPagesOfKeys(String[] keys,
			HashMap<String, Integer> keyToPage, int size) {

		HashMap<String, ArrayList<Integer>> keyToStartEndPages = new HashMap<>();
		ArrayList<Integer> flag = new ArrayList<>();

		for (int i = 0; i < keys.length; i++) {

			// the key is found

			if (keyToPage.get(keys[i]) != null) {

				// store the page number
				flag.add(keyToPage.get(keys[i]));

				// all the keys except the last one
				if (i != keys.length - 1) {

					// find next key that is contained in the PDF and the value
					// is different from this current one
					int j = 0;
					for (j = 1; j < keys.length - i; j++) {

						if (keyToPage.get(keys[i + j]) != null
								&& keyToPage.get(keys[i + j]).intValue() != keyToPage.get(keys[i]).intValue()) {
							break;
						}
						// found the last one
						if (i + j == keys.length - 1) { // -1
							break;
						}
					}

					ArrayList<Integer> startEndPage = new ArrayList<>();
					startEndPage.add(keyToPage.get(keys[i]));
					if (keyToPage.get(keys[i + j]) != null) {
						startEndPage.add(keyToPage.get(keys[i + j]) - 1);
					} else {
						// if the last key value is null, add the last page
						startEndPage.add(size);
					}

					keyToStartEndPages.put(keys[i], startEndPage);

				}
				// The last key
				else {
					ArrayList<Integer> startEndPage = new ArrayList<>();
					startEndPage.add(keyToPage.get(keys[i]));
					startEndPage.add(size);
					keyToStartEndPages.put(keys[i], startEndPage);
				}

			}
			// the key is not found
			else {
				ArrayList<Integer> startEndPage = new ArrayList<>();
				startEndPage.add(0);
				startEndPage.add(0);
				keyToStartEndPages.put(keys[i], startEndPage);
			}

		}

		// add a new key called PDF_SPLIT_PART_1, if no string on the first page
		if (!flag.isEmpty() && flag.get(0) != 1) {

			ArrayList<Integer> startEndPage = new ArrayList<>();
			startEndPage.add(1);
			startEndPage.add(flag.get(0) - 1);
			keyToStartEndPages.put(PDF_SPLIT_PART_1, startEndPage);
		}

		// print
		for (String key : keyToStartEndPages.keySet()) {
			System.out.println(String.format("Key = {}", key));
			System.out.println(String.format("Values = {}", keyToStartEndPages.get(key)));

		}

		return keyToStartEndPages;
	}

	private static HashMap<String, File> getFileOfKeys(File file, String fileName,
			HashMap<String, ArrayList<Integer>> keyToStartEndPage) throws Exception {

		HashMap<String, File> keyToFile = new HashMap<>();
		int i = 1;

		if (keyToStartEndPage.containsKey(PDF_SPLIT_PART_1)) {

			String thisName = fileName.substring(0, fileName.lastIndexOf('/')) + "/tmp/splitfile/"
					+ fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length() - 4) + "-" + i + ".pdf";

			splitPDFFileByPages(file, keyToStartEndPage.get(PDF_SPLIT_PART_1).get(0),
					keyToStartEndPage.get(PDF_SPLIT_PART_1).get(1), thisName);

			File thisFile = new File(thisName);

			keyToFile.put(PDF_SPLIT_PART_1, thisFile);

			i++;

		}

		for (String key : keyToStartEndPage.keySet()) {

			// only execute if the string is found, otherwise the startpage and
			// endpage will be 0
			if (keyToStartEndPage.get(key).get(0) != 0 && keyToStartEndPage.get(key).get(1) != 0) {

				if (!key.equals(PDF_SPLIT_PART_1)) {

					String thisName = fileName.substring(0, fileName.lastIndexOf('/')) + "/tmp/splitfile/"
							+ fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length() - 4) + "-" + i
							+ ".pdf";

					splitPDFFileByPages(file, keyToStartEndPage.get(key).get(0), keyToStartEndPage.get(key).get(1),
							thisName);

					File thisFile = new File(thisName);

					keyToFile.put(key, thisFile);

					i++;
				}

			} else {
				// if the key is not found, the filename would be null
				keyToFile.put(key, null);
			}
		}
		return keyToFile;
	}

	private static void splitPDFFileByPages(File file, int startPage, int endPage, String fileName) throws Exception {
		final Splitter splitter = new Splitter();
		List<PDDocument> documents = null;

		try (PDDocument document = PDDocument.load(file)) {
			if (startPage != 0 && endPage != 0) {

				splitter.setStartPage(startPage);
				splitter.setSplitAtPage(endPage);
				documents = splitter.split(document);

				try (PDDocument doc = documents.get(0)) {

					File f = new File(fileName.substring(0, fileName.lastIndexOf('/')));
					f.mkdirs();
					doc.save(fileName);

				} finally {
					for (PDDocument thisDoc : documents) {
						IOUtils.closeQuietly(thisDoc);
					}
				}
			}
		}
	}

	/**
	 * this is new
	 * 
	 * @param fileName
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public static void generateTOC(String fileName, String[] keys) throws IOException {
		final HashMap<String, Integer> keyToPageNoMap = splitPDFByKeys(fileName, keys);
		System.out.println(keyToPageNoMap);
		// keys = new String[] {"RISK AND RETURN ANALYSIS","ONE YEAR RISK RETURN OF
		// UNDERLYING INVESTMENTS","HOLDINGS ANALYSIS"};

//		try (PDDocument doc = PDDocument.load(new File(fileName));) {
		try {
			boolean isLandscape = isDocLandscape(PDDocument.load(new File(fileName)));

			PDDocument doc = new PDDocument();
			PDPage page = new PDPage(PDRectangle.A4);
			if(isLandscape) {
				page.setRotation(90);
			}
			doc.addPage(page);

			PDRectangle pageSize = page.getMediaBox();
			float pageWidth = pageSize.getWidth();

			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			// set page rotation landscape mode 
			if(isLandscape) {
				contentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));
			}
			contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
			// set line spacing vertically
			contentStream.setLeading(20f);

			// start new line text at this position
			float curYVal = 700f;
			if(isLandscape) {
				curYVal = pageSize.getUpperRightX() - 50f;
			}
			contentStream.beginText();
			contentStream.newLineAtOffset(210, curYVal);
			contentStream.showText("TABLE OF CONTENT");
			contentStream.endText();

			for (int idx = 0; idx < keys.length; idx++) {
				curYVal = curYVal - 20;
				contentStream.beginText();
				contentStream.newLineAtOffset(25, curYVal);
				contentStream.showText(keys[idx] + "  --------------------- " + keyToPageNoMap.get(keys[idx]));
				contentStream.endText();

//				if (curYVal - 50f > 0) {
//					curYVal = curYVal - 50f;
//					contentStream.newLineAtOffset(20, curYVal);
//				} else {
//					contentStream.endText();
//					contentStream.close(); // close writing area
//					curFileNamePage = new PDPage(PDRectangle.A4);
//					doc.addPage(curFileNamePage);
//
//					contentStream = new PDPageContentStream(doc, curFileNamePage, PDPageContentStream.AppendMode.APPEND,
//							true, true);
//					contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//					contentStream.beginText();
//					curYVal = 650f;
//					contentStream.newLineAtOffset(2, curYVal);
//					contentStream.showText(keys[idx] + "------- " + keyToPageNoMap.get(keys[idx]));
//				}
			}

//			contentStream.endText();
			contentStream.close(); // close writing area

			// create new empty file and save TOC doc
			final Path pathTOC = Paths.get(fileName.replace(".pdf", "TOC.pdf"));
			try {
				Files.createFile(pathTOC);
			} catch (FileAlreadyExistsException e) {
				System.out.println("File already exists.");
			}
			doc.save(pathTOC.toFile());// Saving the document
			doc.close();

			// create a destination file for merging
			final Path path2 = Paths.get(fileName.replace(".pdf", "_with_TOC.pdf"));
			try {
				Files.createFile(path2);
			} catch (FileAlreadyExistsException e) {
				System.out.println("Combined File already exists.");
			}

			// merge TOC and main pdf
			String[] files = new String[] { pathTOC.toFile().getAbsolutePath(), fileName };
			mergePdfFiles(files, path2.toFile().getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isDocLandscape(final PDDocument document) throws IOException {
		if (document != null) {
			final PDRectangle pageSize = document.getPage(0).getMediaBox();
			int degree = document.getPage(0).getRotation();

			if ((pageSize.getWidth() > pageSize.getHeight()) || (degree == 90) || (degree == 270)) {
				document.close();
				return true; // document is landscape
			}
		}

		return false;
	}

	/**
	 * merge multiple pdf files
	 *
	 * @param files    file path
	 * @param savepath
	 */
	public static boolean mergePdfFiles(String[] files, String savepath) {
		try {
			File saveFile = new File(savepath);
			saveFile.createNewFile();

			PDFMergerUtility documentMerger = new PDFMergerUtility();
			for (String pdfFile : files) {
				try (PDDocument doc = PDDocument.load(new File(pdfFile))) {
					if (doc.isEncrypted()) {
						try {
							doc.setAllSecurityToBeRemoved(true);
							doc.save(pdfFile);
							doc.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				documentMerger.addSource(pdfFile);
			}

			documentMerger.setDestinationFileName(savepath);
			documentMerger.mergeDocuments(null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
