package com.java.example.reports.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.example.reports.service.IReportService;
import com.java.example.reports.utils.UtilsPDF;

/**
 * @author Mangesh
 * @date 8 Jul 2020
 * @company self
 *
 */
@RestController
public class ReportController {

	@Autowired
	private IReportService reportService;
	
	@GetMapping(path="/reports", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<?> generatePDF(final HttpServletResponse response) {
		try {
			final Resource resource = new ClassPathResource("reports/UOB_Tracker_Report.pdf");
//			final Resource resource = new ClassPathResource("reports/Pascal_Test_Portfolio.pdf");
			final File file = resource.getFile();
			System.out.println("file loaded=" + file.getAbsolutePath());

			// generate TOC
			reportService.generateTOC(file.getAbsolutePath(), UtilsPDF.PAGE_HEADER_LIST_UOB);
			
			// Files.copy(path, response.getOutputStream());
			final Path path2 = Paths.get(file.getAbsolutePath().replace(".pdf", "_with_TOC.pdf"));
			try {
				Files.createFile(path2);
			} catch (FileAlreadyExistsException e) {
				System.out.println("Combined File already exists.");
			}
			final File fileOut = path2.toFile();

			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName().replace(".pdf", "_with_TOC.pdf") + "\"");
			response.setContentLength((int) Files.size(path2));

			try(final FileInputStream inputStream = new FileInputStream(fileOut);){
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			}
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
