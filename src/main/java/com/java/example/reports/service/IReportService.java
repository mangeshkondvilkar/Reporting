package com.java.example.reports.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

/**
 * @author Mangesh
 * @date 8 Jul 2020
 * @company self
 *
 */
@Service
public interface IReportService {

	/** generate TOC page using pageKeyMap 
	 * @param filePath
	 * @param pageKeyToPageNoMap
	 * @throws Exception
	 */
	public void generateTOC(final String filePath, final ArrayList<String> pageHeaderList) throws Exception;
}
