package com.zenithst.core.common.utility;

//import org.jodconverter.OfficeDocumentConverter; 
//import org.jodconverter.office.DefaultOfficeManagerBuilder; 
//import org.jodconverter.office.OfficeException;
//import org.jodconverter.office.OfficeManager;

//@Slf4j
public class ZappConverPdf {

	//static OfficeManager officeManager = null;

	private ZappConverPdf() {
		throw new IllegalStateException("ZappConverPdf class");
	}

//	// Windows Test
//	public static void main(String[] args) throws Exception {
//		
//		String origPath = "C:/temp/test_doc/zenith-scan-20170510-162649-001-0014.tif";
//		String newPath = "C:/temp/test_doc/convert/zenith-scan-20170510-162649-001-0014.tif.pdf";
//		System.out.println("origPath : " + origPath);
//		System.out.println("newPath : " + newPath);
//		File origFile = new File(origPath);
//		File newFile = new File(newPath);
//
//		//OfficeManager officeManager = new DefaultOfficeManagerBuilder().build();
//		//OfficeManager officeManager = new DefaultOfficeManagerBuilder().setOfficeHome("C:/Program Files/LibreOffice").build();
//		
//		try {
//			System.out.println("encoding: " + System.getProperty("file.encoding"));
//			System.out.println("origFilePath: " + getFileEncoding(origPath));
//
//			//officeManager.start();
//			System.out.println("=== officemanager start");
//			//OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
//			System.out.println("=== new converter");
//			
//			//converter.convert(origFile, newFile);
//
//			System.out.println("origFile Delete !!");
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("FileToPdfParser ERROR !! "+ e.getMessage());
//		} finally {
//			//officeManager.stop();
//        }
//	}

//	// jodConvert를 이용한 pdf 변환
//	public static int convertPdf(String origPath, String newPath) throws Exception {
//
//		if (officeManager == null || !officeManager.isRunning()) {
//			System.out.println("=== officeManager is null or not running");
//			officeManager = new DefaultOfficeManagerBuilder().setOfficeHome("D:/LibreOffice").build();
//		}
//		
//		//String origPath = "C:/temp/test_doc/zenith-scan-20170510-162649-001-0014.tif";
//		//String newPath = "C:/temp/test_doc/convert/zenith-scan-20170510-162649-001-0014.tif.pdf";
//		System.out.println("origPath : " + origPath);
//		System.out.println("newPath : " + newPath);
//		File newFile = new File(newPath);
//
//		try {
//			System.out.println("encoding: " + System.getProperty("file.encoding"));
//			System.out.println("origFilePath: " + getFileEncoding(origPath));
//
//			File origFile = new File(origPath);				
//
//			if (!officeManager.isRunning()) {
//				System.out.println("=== officeManger is not running");
//				officeManager.start();
//				System.out.println("=== officemanager start");
//			}
//
//			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
//			System.out.println("=== new converter");
//			
//			converter.convert(origFile, newFile);
//
//			System.out.println("convert Finish !!");
//			
//			String tmpPath = newPath.substring(0, newPath.lastIndexOf("\\")+1);
//			String tmpFile = newPath.substring(newPath.lastIndexOf("\\")+1, newPath.length());
//			System.out.println("=== tmpPath:" + tmpPath);
//			System.out.println("=== tmpFile:" + tmpFile);
//			makeThumb(tmpPath, tmpFile);
//			
//			return 0;
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("FileToPdfParser ERROR !! "+ e.getMessage());
//			return -1;
//		} finally {
//			//officeManager.stop();
//        }
//	}
	
//	// PDFBox를 이용한 썸네일 이미지 추출
//	public static ArrayList<String> makeThumb(String fpath, String fname){
//		//이미지로 변환한 파일 리스트를 담는 객체
//		ArrayList<String> al = new ArrayList();
//		try {
//			String realfname;
//			
//			if (fname.lastIndexOf(".") > 0) {
//				realfname = fname.substring(0, fname.lastIndexOf("."));
//			} else {
//				realfname = fname;
//			}
//			String imgFileName = fpath + realfname + ".png";
//			
//			File thumbFile = new File(imgFileName);
//			if (thumbFile.exists())
//				return null;
//
//			//PDF파일을 읽어온다.
//			InputStream is = new FileInputStream(fpath + fname);
//			//스트림으로 읽어온 데이터를 document에 넣는다.
//			PDDocument doc = PDDocument.load(is);
//			//document에 들어온 데이터를 정제한다.
//			PDFRenderer render = new PDFRenderer(doc);
//			//이미지 저장경로
//			//경로를 따로 지정할경우 폴더를 생성하는 로직
//			//Files.createDirectories(Paths.get(fPath));
//			//System.out.println("시작");
//			//Date aa = new Date();
//			//System.out.println(aa.getHours() + ":" + aa.getMinutes() + ":" + aa.getSeconds());
//			System.out.println("=== cnt:" + doc.getPages().getCount());
//			//for (int i = 0; i < doc.getPages().getCount(); i++) {
//				al.add(imgFileName);
//				BufferedImage bim = render.renderImageWithDPI(0, 30, ImageType.RGB);
//				
//				ImageIOUtil.writeImage(bim, imgFileName, 40);
//			//}
//		    is.close();
//			doc.close();
//			//Date aa1 = new Date();
//			//System.out.println(aa1.getHours() + ":" + aa1.getMinutes() + ":" + aa1.getSeconds());
//			//System.out.println("끝");
//		} catch(FileNotFoundException e) {
//			al.add("FileError");
//		} catch(IOException e) {
//			al.add("IOError");
//			e.printStackTrace();
//		} catch (StringIndexOutOfBoundsException e) {
//			al.add("StringError");
//			e.printStackTrace();
//		} catch (Exception e) {
//			al.add("Error");
//			e.printStackTrace();
//		}
//		return al;
//	}
	
//	// encoding return
//	private static String getFileEncoding(String filePath) {
//		String fileEncodingStr = "EUC-KR";
//
//		try {
//			FileInputStream fis = new FileInputStream(filePath);
//			byte[] BOM = new byte[4];
//			fis.read(BOM, 0, 4);
//
//			if ((BOM[0] & 0xFF) == 0xEF && (BOM[1] & 0xFF) == 0xBB && (BOM[2] & 0xFF) == 0xBF) {
//				fileEncodingStr = "UTF-8";
//			} else if ((BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF) {
//				fileEncodingStr = "UTF-16BE";
//			} else if ((BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE) {
//				fileEncodingStr = "UTF-16LE";
//			} else if ((BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00 && (BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF) {
//				fileEncodingStr = "UTF-32BE";
//			} else if ((BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE && (BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00) {
//				fileEncodingStr = "UTF-32LE";
//			}
//		} catch (Exception e) {
//			//if (log.isErrorEnabled()) {
//			System.out.println("fileEncodingChk ERROR !! " + e.getMessage());
//			//}
//		}
//		return fileEncodingStr;
//	}
//

//	public static boolean setFileToPdfParse(String origFilePath, String parseFilePath) throws OfficeException, IOException {
//		boolean result = true;
//
//		if (origFilePath.isEmpty() || parseFilePath.isEmpty()) {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> setFileToPdfParse Parameter Empty ERROR !!");
//			result = false;
//		}
//
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> encoding: " + System.getProperty("file.encoding"));
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> origFilePath: " + getFileEncoding(origFilePath));
//
//		File origFile = new File(origFilePath);
//		File parseFile = new File(parseFilePath);
//
//		DefaultOfficeManagerBuilder builder = new DefaultOfficeManagerBuilder();
//		builder.setPortNumber(8100);
//		builder.setOfficeHome(new File("/opt/openoffice4"));
//		builder.setTaskExecutionTimeout(600000L);   // 10 minutes
//		builder.setMaxTasksPerProcess(2);
//		OfficeManager officeManager = builder.build();
//
//		try {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> [PDF Parser Util] Start !!");
//			officeManager.start();
//			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
//			converter.convert(origFile, parseFile);
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> [PDF Parser Util] End !!");
//		} catch (Exception e) {
//			//if (log.isErrorEnabled()) {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> setFileToPdfParse ERROR !! "+ e.getMessage());
//			//}
//		} finally {
//			officeManager.stop();
//			origFile.delete();
//		}
//
//		return result;
//	}

}
