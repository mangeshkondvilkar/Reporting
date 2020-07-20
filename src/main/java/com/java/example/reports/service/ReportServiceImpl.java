package com.java.example.reports.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.java.example.reports.utils.UtilsPDF;

/**
 * @author Mangesh
 * @date 8 Jul 2020
 * @company self
 *
 */
@Service
public class ReportServiceImpl implements IReportService {

	@Override
	public void generateTOC(String filePath, final ArrayList<String> pageHeaderList) throws Exception {
		try {
			
			// get keyToPageNoMap
			// final HashMap<String, Integer> keyToPageNoMap = UtilsPDF.splitPDFByKeys(filePath, pageHeaderList.toArray(new String[pageHeaderList.size()]));
			
			// generate TOC page
			UtilsPDF.generateTOC(filePath, pageHeaderList.toArray(new String[pageHeaderList.size()]));
//			System.out.println(keyToPageNoMap);
			
		} catch (Exception e) {
		}
	}

}
