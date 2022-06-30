package com.zenithst.core.content.web;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import com.zenithst.archive.api.ZArchFileMgtService;
import com.zenithst.archive.api.ZArchMFileMgtService;
import com.zenithst.archive.service.ZArchFileService;
import com.zenithst.archive.service.ZArchVersionService;
import com.zenithst.archive.util.CryptoNUtil;
import com.zenithst.archive.util.CryptoUtil;
import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.archive.vo.ZArchVersion;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.content.api.ZappContentMgtService;
//import com.zenithst.core.common.utility.ZappConverPdf;
import com.zenithst.framework.util.ZstFwExtractUtils;
import com.zenithst.framework.util.ZstFwFileUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

@Controller
@RequestMapping(value = "/api/file")
public class ZappFileController extends ZappController {

	@Resource
	private ZArchMFileMgtService zArchMFileMgtService;

	@Resource
	private ZArchFileService zArchFileService;

	@Resource
	private ZArchFileMgtService zArchFileMgtService;

	@Autowired
	private ZArchVersionService zArchVersionService;
	
	
	@Autowired
	private ZappContentMgtService zappContentMgtService; 

	
	@Value("#{archiveconfig['UPLOAD_TEMP_PATH']}")
	private String upTempPath ;

	@Value("#{archiveconfig['DOWN_TEMP_PATH']}")
	private String downTempPath ;
	
	private String html5ServerIP = "localhost";
	//private String html5ServerIP = "112.216.54.251";
	private String html5ServerPort = "8088";

	/**
	 * 파일을 서버에 전송한다.
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/fileSend", produces = {"application/json", "application/xml"},consumes = "multipart/form-data")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity fileSend(HttpServletRequest request, HttpServletResponse response) {		
		
		String savedFileName = "";	
		Random random = null;
		HashMap<String,Object> result = new HashMap<String,Object>();

		try {
			if(upTempPath.equals(BLANK)){
				logger.error("Temporary upload path does not exist.");
			} else {
				if(ServletFileUpload.isMultipartContent(request) == false){
					logger.error("This is not multipart type content.");
				} else {					
					File uploadDir = new File(upTempPath);
					if(!uploadDir.exists()){
						uploadDir.mkdirs();
					}
					List<FileItem> multiparts;
					multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
					ArrayList<HashMap<String,String>> array = new ArrayList<HashMap<String,String>>();
					for(FileItem item : multiparts){
						if(!item.isFormField()){
							HashMap<String,String> filedata = new HashMap<String,String>();
							savedFileName = new File(item.getName()).getName();
							String ext = savedFileName.substring(savedFileName.lastIndexOf('.')+1);

							String savedFileFullPath  = upTempPath + File.separator + System.currentTimeMillis()+"_"+ savedFileName;
							File savedFile = new File(savedFileFullPath);
							if(savedFile.exists()){
								random = new Random();
								savedFileFullPath += random.nextInt(100);
							}
							item.write( new File(savedFileFullPath));
							filedata.put("objFileName",savedFileFullPath);
							filedata.put("objFileExt",ext);
							filedata.put("filename",savedFileName);//checkFormat
							filedata.put("checkFormat","false");
							filedata.put("action", "ADD");
							array.add(filedata);		   
						}
					}// end for		
					result.put("zappFiles", array);	
					result.put("result", 0);
				}
			}			

		} catch (FileUploadException e) {	
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity(result, HttpStatus.CREATED);
	}

	
//	@RequestMapping(value = "/toBlob_With_Path/{versionid}", method = RequestMethod.GET)
//	public ResponseEntity<byte[]>  filedownload_With_Path(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {
//		String Status = "SUCCESS";
//		ResponseEntity entity = null;
//		
//		try {			
//			logger.debug("=== filedownload_With_Path versionid:" + versionid);
//
//			if(ZstFwValidatorUtils.valid(versionid) == true) {
//				ZArchVersion pIn = new ZArchVersion();
//				pIn.setVersionid(versionid);
//				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
//				if(zarchVersion != null) {
//					String fileName = zarchVersion.getFilename();
//
//					ZArchFile ZarchMfile = new ZArchFile();
//					ZarchMfile.setUfileid(zarchVersion.getUfileid());
//					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);
//
//					//임시영역으로 파일을 카피한다.
//					//String filePath = getArchiveFilePath(zArchFile);
//					//서버 저장스토리지 경로를 리턴
//					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
//					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();
//					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length());
//
//					logger.debug("=== filePath:" + filePath);
//					logger.debug("=== fileName:" + fileName);
//					logger.debug("=== fileExt:" + fileExt);
//
//					File regFile = null;
//					
//					String _DECRYPTED_PATH;
//					if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
//						//_DECRYPTED_PATH = filePath + ".dec";
//						_DECRYPTED_PATH = filePath + fileExt;
//						File file = new File(_DECRYPTED_PATH);
//						if (!file.exists()) {
//							logger.debug("=== doDecrypt");
//							CryptoNUtil.doDecrypt(new File(filePath), new File(_DECRYPTED_PATH));
//						} else {
//							logger.debug("=== dec file already exist, so skip Decrypt");
//						}
//					} else {
//						_DECRYPTED_PATH = filePath + fileExt;
//						ZstFwFileUtils.copyFile(filePath, _DECRYPTED_PATH);
//					}
//					logger.debug("=== _DECRYPTED_PATH: " + _DECRYPTED_PATH);
//				
//					try {
//			            // filePath 방식
//			            URL hrdUrl = new URL("http://" + html5ServerIP + ":" + html5ServerPort + "/api/convert?filePath=" + _DECRYPTED_PATH + "&orgfilename=" + URLEncoder.encode(fileName, "UTF-8"));
//			            BufferedReader hrdStr = new BufferedReader(new InputStreamReader(hrdUrl.openStream()));
//			            String line = "";
//			            if ((line = hrdStr.readLine()) != null){
//			                //userHRDCnt = line;
//			            	logger.debug("=== convert result:" + line);
//			            	
//			            	JSONObject jObject = new JSONObject(line);
//			                boolean bError 		= jObject.getBoolean("error");
//			                if (bError) {
//			                	String errMsg 	= jObject.getString("message");
//				            	logger.debug("=== errMsg: " + errMsg);
//			                } else {
//				                String fileid 		= jObject.getString("fileid");
//				                String crtdate 		= jObject.getString("crtdate");
//				                String orgfilename 	= jObject.getString("orgfilename");
//				                String ext 			= jObject.getString("ext");
//				                String domainUrl 	= jObject.getString("domainurl");
//				                String target 		= jObject.getString("target");
//				                
//				                JSONArray jArray = jObject.getJSONArray("imgList");
//				                for (int i = 0; i < jArray.length(); i++) {
//				                    //JSONObject obj = jArray.getJSONObject(i);
//				                    //String title = obj.getString("title");
//				                }
//				            	logger.debug("=== fileid: " + fileid);
//				            	logger.debug("=== crtdate: " + crtdate);
//				            	logger.debug("=== orgfilename: " + orgfilename);
//				            	logger.debug("=== ext: " + ext);
//				            	logger.debug("=== domainUrl: " + domainUrl);
//				            	logger.debug("=== jArray.length(): " + jArray.length());
//				            	
//					            hrdUrl = new URL("http://" + html5ServerIP + ":" + html5ServerPort + "/api/get/pdf/" + crtdate + "/" + fileid);
//					            BufferedInputStream bis = new BufferedInputStream(hrdUrl.openStream());
//								ByteArrayOutputStream baos = new ByteArrayOutputStream();
//								byte[] buf = new byte[1024];
//								int readlen = 0;
//								while ((readlen = bis.read(buf)) != -1) {
//									baos.write(buf, 0, readlen);
//								}
//								byte[] imgbuf = null;
//								imgbuf = baos.toByteArray();
//								baos.close();
//								bis.close();
//								
//								int len = imgbuf.length;
//								ServletOutputStream os = response.getOutputStream();
//								os.write(imgbuf, 0, len);
//								os.close();
//
//			                }
//			            }
//			        } catch (Exception e) { 
//			        	e.printStackTrace();
//			        }
//			       
//			        //out.print(userHRDCnt);
//				} else {
//					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
//					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//				}
//			} else {
//				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
//				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
//			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
//		} finally {
//			try {
//				response.flushBuffer();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return entity;
//	}
	
// jodConvert	
//	@RequestMapping(value = "/toBlob_With_Path/{versionid}", method = RequestMethod.GET)
//	//public ResponseEntity<byte[]> filedownload_With_Path(@RequestBody Map<String, String> pIn) throws IOException {
//	//public ResponseEntity<byte[]> filedownload_With_Path(@PathVariable String versionid) throws IOException {
//	public ResponseEntity<byte[]>  filedownload_With_Path(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		
//		
//		try {
//			logger.debug("=== filedownload_With_Path");
//			logger.debug("=== versionid:" + versionid);
//			if(ZstFwValidatorUtils.valid(versionid) == true) {
//				ZArchVersion pIn = new ZArchVersion();
//				pIn.setVersionid(versionid);
//				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
//				if(zarchVersion != null) {
//					String fileName = zarchVersion.getFilename();
//	
//					ZArchFile ZarchMfile = new ZArchFile();
//					ZarchMfile.setUfileid(zarchVersion.getUfileid());
//					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);
//	
//					//임시영역으로 파일을 카피한다.
//					//String filePath = getArchiveFilePath(zArchFile);
//					//서버 저장스토리지 경로를 리턴
//					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
//					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();
//					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length());
//	
//					logger.debug("=== filePath:" + filePath);
//					logger.debug("=== fileName:" + fileName);
//					logger.debug("=== fileExt:" + fileExt);
//	
//					File regFile = null;
//					
//					String _DECRYPTED_PATH;
//					if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
//						_DECRYPTED_PATH = filePath + ".dec";
//						File file = new File(_DECRYPTED_PATH);
//						if (!file.exists()) {
//							logger.debug("=== doDecrypt");
//							CryptoNUtil.doDecrypt(new File(filePath), new File(_DECRYPTED_PATH));
//						} else {
//							logger.debug("=== dec file already exist, so skip Decrypt");
//						}
//					} else {
//						_DECRYPTED_PATH = filePath;
//					}
//					
//					if (fileExt.equals(".doc") || fileExt.equals(".docx") || fileExt.equals(".xls") || fileExt.equals(".xlsx")
//							|| fileExt.equals(".ppt") || fileExt.equals(".pptx") || fileExt.equals(".txt")) {
//						String pdfPath = filePath + ".pdf";
//						
//						//for (int i=0; i<100; i++) {
//							regFile = new File(pdfPath);
//							
//							if (!regFile.exists()) {
//								//regFile.delete();
//								if (ZappConverPdf.convertPdf(_DECRYPTED_PATH, pdfPath) == 0) {
//									logger.debug("=== convertPdf OK");						
//								} else {
//									logger.debug("=== convertPdf Error");												
//								}
//							} else {
//								logger.debug("=== pdf file already exist, so skip convert");
//							}
//						//}
//					} else if (fileExt.equals(".pdf") || fileExt.equals(".hwp") || fileExt.equals(".png") || fileExt.equals(".jar")) {
//						regFile = new File(_DECRYPTED_PATH);
//					} else {
//						logger.debug("=== Invalid file format ["  +fileExt + "]");
//						return null;
//					}
//
//				      //File file = new File(pIn.get("path"));
//				      //File orgFile = new File(pIn.get("orgPath"));
//				      String pFileName = "";
//				      FileInputStream fis = null;
//				      try{
//				         fis = new FileInputStream(regFile);
//				         byte[] contents = IOUtils.toByteArray(fis);
//				         HttpHeaders headers = new HttpHeaders();
//				         headers.setContentType(MediaType.parseMediaType("application/pdf"));
//				         headers.setContentDispositionFormData(pFileName, pFileName);
//				         ResponseEntity<byte[]> response1 = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
//				         return response1;
//				      }catch(Exception e){
//				         e.printStackTrace();
//				      }finally{
//				         fis.close();
////				         if(file.exists()){
////				            file.delete();
////				         }
//				         //if(orgFile.exists()) {
//				         //   orgFile.delete();
//				         //}
//				      }
//				}
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		} finally {
//		}
//	      return null;
//	   }

	// Zenith ServerBaseViewer를 이용한 PDF Conversion with Thumbnail
	@RequestMapping(value = "/officePdfConvertWithThumb/{versionid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity  officePdfConvertWithThumb(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		

		String Status = "SUCCESS";
		ResponseEntity entity = null;
		
		try {			
			logger.debug("=== officePdfConvertWithThumb versionid:" + versionid);

			if(ZstFwValidatorUtils.valid(versionid) == true) {
				ZArchVersion pIn = new ZArchVersion();
				pIn.setVersionid(versionid);
				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
				if(zarchVersion != null) {
					String fileName = zarchVersion.getFilename();

					ZArchFile ZarchMfile = new ZArchFile();
					ZarchMfile.setUfileid(zarchVersion.getUfileid());
					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);

					//임시영역으로 파일을 카피한다.
					//String filePath = getArchiveFilePath(zArchFile);
					//서버 저장스토리지 경로를 리턴
					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();
					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length());

					logger.debug("=== filePath:" + filePath);
					logger.debug("=== fileName:" + fileName);
					logger.debug("=== fileExt:" + fileExt);

					File regFile = null;
					
					String _DECRYPTED_PATH;
					if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
						//_DECRYPTED_PATH = filePath + ".dec";
						_DECRYPTED_PATH = filePath + fileExt;
						File file = new File(_DECRYPTED_PATH);
						if (!file.exists()) {
							logger.debug("=== doDecrypt");
							CryptoNUtil.doDecrypt(new File(filePath), new File(_DECRYPTED_PATH));
						} else {
							logger.debug("=== dec file already exist, so skip Decrypt");
						}
					} else {
						_DECRYPTED_PATH = filePath + fileExt;
						ZstFwFileUtils.copyFile(filePath, _DECRYPTED_PATH);
					}
					logger.debug("=== _DECRYPTED_PATH: " + _DECRYPTED_PATH);
				
					try {
						String downUrl = URLEncoder.encode("http://" + request.getServerName() + ":" + request.getServerPort() 
								+ request.getContextPath() + "/api/file/fileDown/" + versionid, "UTF-8");
						logger.debug("=== downUrl: " + downUrl);
						logger.debug("=== zArchFile.getUfileid(): " + zArchFile.getUfileid());

						// URL방식
						String html5Url = "http://" + html5ServerIP + ":" + html5ServerPort + "/api/convert?downurl=" + downUrl + "&orgfilename=" + URLEncoder.encode(fileName, "UTF-8")+"&fileid="+zArchFile.getUfileid();
						logger.debug("=== html5Url: " + html5Url);
			            URL hrdUrl = new URL(html5Url);
			            // filePath 방식
			            //URL hrdUrl = new URL("http://" + html5ServerIP + ":" + html5ServerPort + "/api/convert?filePath=" + _DECRYPTED_PATH + "&orgfilename=" + URLEncoder.encode(fileName, "UTF-8"));
			            BufferedReader hrdStr = new BufferedReader(new InputStreamReader(hrdUrl.openStream()));
			            String line = "";
			            if ((line = hrdStr.readLine()) != null){
			                //userHRDCnt = line;
			            	logger.debug("=== convert result:" + line);
			            	
			            	JSONObject jObject = new JSONObject(line);
			                boolean bError 		= jObject.getBoolean("error");
			                if (bError) {
			                	String errMsg 	= jObject.getString("message");
				            	logger.debug("=== errMsg: " + errMsg);
				            	response.setHeader("Content-Disposition", "attachment; filename=\" File Convert Fail \"");
								entity = new ResponseEntity("FAIL", HttpStatus.EXPECTATION_FAILED);
								response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			                } else {
				                String fileid 		= jObject.getString("fileid");
				                String crtdate 		= jObject.getString("crtdate");
				                String orgfilename 	= jObject.getString("orgfilename");
				                String ext 			= jObject.getString("ext");
				                String domainUrl 	= jObject.getString("domainurl");			                
				                JSONArray jArray = jObject.getJSONArray("imgList");
				            	logger.debug("=== fileid: " + fileid);
				            	logger.debug("=== crtdate: " + crtdate);
				            	logger.debug("=== orgfilename: " + orgfilename);
				            	logger.debug("=== ext: " + ext);
				            	logger.debug("=== domainUrl: " + domainUrl);
				            	logger.debug("=== imgList.length(): " + jArray.length());
				            	
				            	String thumbUrl = "http://" + html5ServerIP + ":" + html5ServerPort + "/api/get/thumb/" + crtdate + "/" + fileid + "/0?ext=pdf";
				            	logger.debug("=== thumbUrl: " + thumbUrl);
					            hrdUrl = new URL(thumbUrl);
					            BufferedInputStream bis = new BufferedInputStream(hrdUrl.openStream());
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								byte[] buf = new byte[1024];
								int readlen = 0;
								while ((readlen = bis.read(buf)) != -1) {
									baos.write(buf, 0, readlen);
								}
								byte[] imgbuf = null;
								imgbuf = baos.toByteArray();
								baos.close();
								bis.close();
								
								int len = imgbuf.length;
								ServletOutputStream os = response.getOutputStream();
								os.write(imgbuf, 0, len);
								os.close();

			                }
			            }
			        } catch (Exception e) { 
			        	e.printStackTrace();
			        	response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			        }
			       
			        //out.print(userHRDCnt);
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
				}
			} else {
				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		} finally {
			try {
				response.flushBuffer();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param versionid
	 * @param pSession
	 * @return
	 */
	@RequestMapping(value = "/convertOfficeThumb/{versionid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity  convertOfficeThumb(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		

		String Status = "SUCCESS";
		ResponseEntity entity = null;

		try {			
			logger.debug("=== convertOfficeThumb versionid:" + versionid);

			if(ZstFwValidatorUtils.valid(versionid) == true) {
				ZArchVersion pIn = new ZArchVersion();
				pIn.setVersionid(versionid);
				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
				if(zarchVersion != null) {
					String fileName = zarchVersion.getFilename();

					ZArchFile ZarchMfile = new ZArchFile();
					ZarchMfile.setUfileid(zarchVersion.getUfileid());
					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);

					//임시영역으로 파일을 카피한다.
					//String filePath = getArchiveFilePath(zArchFile);
					//서버 저장스토리지 경로를 리턴
					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length());

					logger.debug("=== fileName:" + fileName);
					logger.debug("=== fileExt:" + fileExt);				

					try {
						String downUrl = URLEncoder.encode("http://" + request.getServerName() + ":" + request.getServerPort() 
						+ request.getContextPath() + "/api/file/fileDown/" + versionid, "UTF-8");
						logger.debug("=== downUrl: " + downUrl);
						logger.debug("=== zArchFile.getUfileid(): " + zArchFile.getUfileid());

						// URL방식
						String html5Url = "http://" + html5ServerIP + ":" + html5ServerPort + "/api/convert?downurl=" + downUrl + "&orgfilename=" + URLEncoder.encode(fileName, "UTF-8")+"&fileid="+zArchFile.getUfileid();
						logger.debug("=== html5Url: " + html5Url);
						URL hrdUrl = new URL(html5Url);
						// filePath 방식
						//URL hrdUrl = new URL("http://" + html5ServerIP + ":" + html5ServerPort + "/api/convert?filePath=" + _DECRYPTED_PATH + "&orgfilename=" + URLEncoder.encode(fileName, "UTF-8"));
						BufferedReader hrdStr = new BufferedReader(new InputStreamReader(hrdUrl.openStream()));
						String line = "";
						if ((line = hrdStr.readLine()) != null){
							//userHRDCnt = line;
							logger.debug("=== convert result:" + line);

							JSONObject jObject = new JSONObject(line);
							boolean bError 		= jObject.getBoolean("error");
							if (bError) {
								String errMsg 	= jObject.getString("message");
								logger.debug("=== errMsg: " + errMsg);
								response.setHeader("Content-Disposition", "attachment; filename=\" File Convert Fail \"");
								entity = new ResponseEntity("FAIL", HttpStatus.EXPECTATION_FAILED);
								response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
							} else {
								String fileid 		= jObject.getString("fileid");
								String crtdate 		= jObject.getString("crtdate");
								String orgfilename 	= jObject.getString("orgfilename");
								String ext 			= jObject.getString("ext");
								String domainUrl 	= jObject.getString("domainurl");			                
								JSONArray jArray = jObject.getJSONArray("imgList");
								logger.debug("=== fileid: " + fileid);
								logger.debug("=== crtdate: " + crtdate);
								logger.debug("=== orgfilename: " + orgfilename);
								logger.debug("=== ext: " + ext);
								logger.debug("=== domainUrl: " + domainUrl);
								logger.debug("=== imgList.length(): " + jArray.length());

								String thumbUrl = "http://" + html5ServerIP + ":" + html5ServerPort + "/api/get/thumb/" + crtdate + "/" + fileid + "/0?ext=pdf";
								logger.debug("=== thumbUrl: " + thumbUrl);
								hrdUrl = new URL(thumbUrl);
								BufferedInputStream bis = new BufferedInputStream(hrdUrl.openStream());
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								byte[] buf = new byte[1024];
								int readlen = 0;
								while ((readlen = bis.read(buf)) != -1) {
									baos.write(buf, 0, readlen);
								}
								byte[] imgbuf = null;
								imgbuf = baos.toByteArray();
								baos.close();
								bis.close();

								int len = imgbuf.length;
								ServletOutputStream os = response.getOutputStream();
								os.write(imgbuf, 0, len);
								os.close();

							}
						}
					} catch (Exception e) { 
						e.printStackTrace();
						response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					}

					//out.print(userHRDCnt);
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
				}
			} else {
				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		} finally {
			try {
				response.flushBuffer();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}

	// Zenith ServerBaseViewer를 이용한 PDF Conversion
	@RequestMapping(value = "/officePdfConvert/{versionid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity  officePdfConvert(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		

		String Status = "SUCCESS";
		ResponseEntity entity = null;
		
		try {			
			logger.debug("=== hwpPdfConvert versionid:" + versionid);

			if(ZstFwValidatorUtils.valid(versionid) == true) {
				ZArchVersion pIn = new ZArchVersion();
				pIn.setVersionid(versionid);
				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
				if(zarchVersion != null) {
					String fileName = zarchVersion.getFilename();

					ZArchFile ZarchMfile = new ZArchFile();
					ZarchMfile.setUfileid(zarchVersion.getUfileid());
					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);

					//임시영역으로 파일을 카피한다.
					//String filePath = getArchiveFilePath(zArchFile);
					//서버 저장스토리지 경로를 리턴
					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();
					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length());

					logger.debug("=== filePath:" + filePath);
					logger.debug("=== fileName:" + fileName);
					logger.debug("=== fileExt:" + fileExt);

					File regFile = null;
					
					String _DECRYPTED_PATH;
					if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
						//_DECRYPTED_PATH = filePath + ".dec";
						_DECRYPTED_PATH = filePath + fileExt;
						File file = new File(_DECRYPTED_PATH);
						if (!file.exists()) {
							logger.debug("=== doDecrypt");
							CryptoNUtil.doDecrypt(new File(filePath), new File(_DECRYPTED_PATH));
						} else {
							logger.debug("=== dec file already exist, so skip Decrypt");
						}
					} else {
						_DECRYPTED_PATH = filePath + fileExt;
						ZstFwFileUtils.copyFile(filePath, _DECRYPTED_PATH);
					}
					logger.debug("=== _DECRYPTED_PATH: " + _DECRYPTED_PATH);
				
					try {
			            // filePath 방식
			            URL hrdUrl = new URL("http://" + html5ServerIP + ":" + html5ServerPort + "/api/convert?filePath=" + _DECRYPTED_PATH + "&orgfilename=" + URLEncoder.encode(fileName, "UTF-8"));
			            BufferedReader hrdStr = new BufferedReader(new InputStreamReader(hrdUrl.openStream()));
			            String line = "";
			            if ((line = hrdStr.readLine()) != null){
			                //userHRDCnt = line;
			            	logger.debug("=== convert result:" + line);
			            	
			            	JSONObject jObject = new JSONObject(line);
			                boolean bError 		= jObject.getBoolean("error");
			                if (bError) {
			                	String errMsg 	= jObject.getString("message");
				            	logger.debug("=== errMsg: " + errMsg);
			                } else {
				                String fileid 		= jObject.getString("fileid");
				                String crtdate 		= jObject.getString("crtdate");
				                String orgfilename 	= jObject.getString("orgfilename");
				                String ext 			= jObject.getString("ext");
				                String domainUrl 	= jObject.getString("domainurl");
				                String target 		= jObject.getString("target");
				                
				                JSONArray jArray = jObject.getJSONArray("imgList");
				                for (int i = 0; i < jArray.length(); i++) {
				                    //JSONObject obj = jArray.getJSONObject(i);
				                    //String title = obj.getString("title");
				                }
				            	logger.debug("=== fileid: " + fileid);
				            	logger.debug("=== crtdate: " + crtdate);
				            	logger.debug("=== orgfilename: " + orgfilename);
				            	logger.debug("=== ext: " + ext);
				            	logger.debug("=== domainUrl: " + domainUrl);
				            	logger.debug("=== jArray.length(): " + jArray.length());
				            	
					            hrdUrl = new URL("http://" + html5ServerIP + ":" + html5ServerPort + "/api/get/pdf/" + crtdate + "/" + fileid);
					            BufferedInputStream bis = new BufferedInputStream(hrdUrl.openStream());
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								byte[] buf = new byte[1024];
								int readlen = 0;
								while ((readlen = bis.read(buf)) != -1) {
									baos.write(buf, 0, readlen);
								}
								byte[] imgbuf = null;
								imgbuf = baos.toByteArray();
								baos.close();
								bis.close();
								
								int len = imgbuf.length;
								ServletOutputStream os = response.getOutputStream();
								os.write(imgbuf, 0, len);
								os.close();

			                }
			            }
			        } catch (Exception e) { 
			        	e.printStackTrace();
			        }
			       
			        //out.print(userHRDCnt);
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
				}
			} else {
				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}

	/**
	 * 서버베이스 뷰어에 이미 변환된 PDF를 다운로드한다.
	 * @param request
	 * @param response
	 * @param versionid
	 * @param pSession
	 * @return
	 */
	@RequestMapping(value = "/pdfdown/{versionid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity  pdfdown(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		

		String Status = "SUCCESS";
		ResponseEntity entity = null;

		try {			
			logger.debug("=== hwpPdfConvert versionid:" + versionid);

			if(ZstFwValidatorUtils.valid(versionid) == true) {
				ZArchVersion pIn = new ZArchVersion();
				pIn.setVersionid(versionid);
				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
				if(zarchVersion != null) {
					String fileName = zarchVersion.getFilename();

					logger.debug("=== fileName:" + fileName);
					logger.debug("=== fileid:" + zarchVersion.getUfileid());

					try {
						//변환된 PDF를 바로 가져온다.
						URL hrdUrl = new URL("http://" + html5ServerIP + ":" + html5ServerPort + "/api/down?fileid="+zarchVersion.getUfileid()+"&orgfilename="+URLEncoder.encode(fileName, "UTF-8"));
						BufferedInputStream bis = new BufferedInputStream(hrdUrl.openStream());
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int readlen = 0;
						while ((readlen = bis.read(buf)) != -1) {
							baos.write(buf, 0, readlen);
						}
						byte[] imgbuf = null;
						imgbuf = baos.toByteArray();
						baos.close();
						bis.close();

						int len = imgbuf.length;
						ServletOutputStream os = response.getOutputStream();
						os.write(imgbuf, 0, len);
						os.close();

					} catch (Exception e) { 
						e.printStackTrace();
					}

					//out.print(userHRDCnt);
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
				}
			} else {
				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}

//	@RequestMapping(value = "/pdfConvertByUrl/{versionid}", method=RequestMethod.GET)
//	@ResponseStatus(HttpStatus.CREATED)
//	public ResponseEntity  pdfConvertByUrl(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		
//
//		String Status = "SUCCESS";
//		ResponseEntity entity = null;
//		
//		try {			
//			logger.debug("=== pdfConvertByUrl versionid:" + versionid);
//
//			if(ZstFwValidatorUtils.valid(versionid) == true) {
//				ZArchVersion pIn = new ZArchVersion();
//				pIn.setVersionid(versionid);
//				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
//				if(zarchVersion != null) {
//					String fileName = zarchVersion.getFilename();
//
//					ZArchFile ZarchMfile = new ZArchFile();
//					ZarchMfile.setUfileid(zarchVersion.getUfileid());
//					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);
//
//					//임시영역으로 파일을 카피한다.
//					//String filePath = getArchiveFilePath(zArchFile);
//					//서버 저장스토리지 경로를 리턴
//					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
//					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();
//					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length());
//
//					logger.debug("=== filePath:" + filePath);
//					logger.debug("=== fileName:" + fileName);
//					logger.debug("=== fileExt:" + fileExt);
//
//					File regFile = null;
//					
//					String _DECRYPTED_PATH;
//					if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
//						//_DECRYPTED_PATH = filePath + ".dec";
//						_DECRYPTED_PATH = filePath + fileExt;
//						File file = new File(_DECRYPTED_PATH);
//						if (!file.exists()) {
//							logger.debug("=== doDecrypt");
//							CryptoNUtil.doDecrypt(new File(filePath), new File(_DECRYPTED_PATH));
//						} else {
//							logger.debug("=== dec file already exist, so skip Decrypt");
//						}
//					} else {
//						_DECRYPTED_PATH = filePath + fileExt;
//						ZstFwFileUtils.copyFile(filePath, _DECRYPTED_PATH);
//					}
//					logger.debug("=== _DECRYPTED_PATH: " + _DECRYPTED_PATH);
//
//					//String downUrl = URLEncoder.encode("http://localhost:8080/ecm40/api/file/fileDown/" + versionid, "UTF-8");
//					String downUrl = URLEncoder.encode("http://" + request.getServerName() + ":" + request.getServerPort() 
//							+ request.getContextPath() + "/api/file/fileDown/" + versionid, "UTF-8");
//					logger.debug("=== downUrl: " + downUrl);
//
//					try {
//						// URL방식
//			            URL hrdUrl = new URL("http://localhost:8088/api/convert?downurl=" + downUrl + "&orgfilename=" + fileName);
//			            // filePath 방식
//			            //URL hrdUrl = new URL("http://localhost:8088/api/convert?filePath=" + _DECRYPTED_PATH + "&orgfilename=" + fileName);
//			            BufferedReader hrdStr = new BufferedReader(new InputStreamReader(hrdUrl.openStream()));
//			            String line = "";
//			            if ((line = hrdStr.readLine()) != null){
//			                //userHRDCnt = line;
//			            	logger.debug("=== convert result:" + line);
//			            }
//			        } catch (Exception e) { 
//			        	e.printStackTrace();
//			        }
//			       
//			        //out.print(userHRDCnt);
//					
//					
//					/**
//					if (fileExt.equals(".doc") || fileExt.equals(".docx") || fileExt.equals(".xls") || fileExt.equals(".xlsx")
//							|| fileExt.equals(".ppt") || fileExt.equals(".pptx")) {
//						String pdfPath = filePath + ".pdf";
//						
//					} else if (fileExt.equals(".pdf") || fileExt.equals(".hwp") || fileExt.equals(".png") || fileExt.equals(".jar")) {
//						regFile = new File(_DECRYPTED_PATH);
//					} else {
//						logger.debug("=== pdf file already exist, so skip convert");						
//					}
//					
//					String mimeType ="application/octet-stream";
//					FileInputStream fis = null;
//
//					try {
//						if (fileExt.equals(".doc") || fileExt.equals(".docx") || fileExt.equals(".xls") || fileExt.equals(".xlsx")
//								|| fileExt.equals(".ppt") || fileExt.equals(".pptx") || fileExt.equals(".pdf")) {
//							fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
//						} else {
//							// PDF변환 불가 파일은 원본 다운로드
//						}
//						fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
//						logger.debug("=== pdf fileName:" + fileName);
//
//						if(regFile.exists() == true){
//							int contentlength = (int)regFile.length();					
//							response.setContentType(mimeType.toString());
//							//inline : 
//							response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
//							fis = FileUtils.openInputStream(regFile);							
//							response.setContentLength(contentlength);//물리파일의 파일 크기를 지정한다.(원본이므로)
//							IOUtils.copy(fis, response.getOutputStream());
//
//							entity = new ResponseEntity("SUCCESS", HttpStatus.CREATED);
//						} else {
//							response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+" File Not Found\"");
//							//response.setStatus(HttpStatus.NOT_FOUND.value());							
//							entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//						response.setHeader("Content-Disposition", "attachment; filename=\" Server Error \"");
//						entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
//					}finally {
//						if(fis != null) fis.close();
//					}
//					 **/
//				} else {
//					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
//					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//				}
//			} else {
//				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
//				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
//			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
//		} finally {
//			try {
//				response.flushBuffer();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return entity;
//	}

	
//	@RequestMapping(value = "/office2pdfDown/{versionid}", method=RequestMethod.GET)
//	@ResponseStatus(HttpStatus.CREATED)
//	public ResponseEntity  office2pdfDown(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		
//
//		String Status = "SUCCESS";
//		ResponseEntity entity = null;
//		
//		try {			
//			logger.debug("==== office2pdfDown");
//			if(ZstFwValidatorUtils.valid(versionid) == true) {
//				ZArchVersion pIn = new ZArchVersion();
//				pIn.setVersionid(versionid);
//				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
//				if(zarchVersion != null) {
//					String fileName = zarchVersion.getFilename();
//
//					ZArchFile ZarchMfile = new ZArchFile();
//					ZarchMfile.setUfileid(zarchVersion.getUfileid());
//					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);
//
//					//임시영역으로 파일을 카피한다.
//					//String filePath = getArchiveFilePath(zArchFile);
//					//서버 저장스토리지 경로를 리턴
//					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
//					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();
//					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length());
//
//					logger.debug("=== filePath:" + filePath);
//					logger.debug("=== fileName:" + fileName);
//					logger.debug("=== fileExt:" + fileExt);
//
//					File regFile = null;
//					
//					String _DECRYPTED_PATH;
//					if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
//						_DECRYPTED_PATH = filePath + ".dec";
//						File file = new File(_DECRYPTED_PATH);
//						if (!file.exists()) {
//							logger.debug("=== doDecrypt");
//							CryptoNUtil.doDecrypt(new File(filePath), new File(_DECRYPTED_PATH));
//						} else {
//							logger.debug("=== dec file already exist, so skip Decrypt");
//						}
//					} else {
//						_DECRYPTED_PATH = filePath;
//					}
//					
//					if (fileExt.equals(".doc") || fileExt.equals(".docx") || fileExt.equals(".xls") || fileExt.equals(".xlsx")
//							|| fileExt.equals(".ppt") || fileExt.equals(".pptx")) {
//						String pdfPath = filePath + ".pdf";
//						
//						//for (int i=0; i<100; i++) {
//							regFile = new File(pdfPath);
//							
//							if (!regFile.exists()) {
//								//regFile.delete();
//								if (ZappConverPdf.convertPdf(_DECRYPTED_PATH, pdfPath) == 0) {
//									logger.debug("=== convertPdf OK");						
//								} else {
//									logger.debug("=== convertPdf Error");												
//								}
//							} else {
//								logger.debug("=== pdf file already exist, so skip convert");
//							}
//						//}
//					} else if (fileExt.equals(".pdf") || fileExt.equals(".hwp") || fileExt.equals(".png") || fileExt.equals(".jar")) {
//						regFile = new File(_DECRYPTED_PATH);
//					} else {
//						logger.debug("=== pdf file already exist, so skip convert");						
//					}
//					
//					String mimeType ="application/octet-stream";
//					FileInputStream fis = null;
//
//					try {
//						if (fileExt.equals(".doc") || fileExt.equals(".docx") || fileExt.equals(".xls") || fileExt.equals(".xlsx")
//								|| fileExt.equals(".ppt") || fileExt.equals(".pptx") || fileExt.equals(".pdf")) {
//							fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
//						} else {
//							// PDF변환 불가 파일은 원본 다운로드
//						}
//						fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
//						logger.debug("=== pdf fileName:" + fileName);
//
//						if(regFile.exists() == true){
//							int contentlength = (int)regFile.length();					
//							response.setContentType(mimeType.toString());
//							//inline : 
//							response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
//							fis = FileUtils.openInputStream(regFile);							
//							response.setContentLength(contentlength);//물리파일의 파일 크기를 지정한다.(원본이므로)
//							IOUtils.copy(fis, response.getOutputStream());
//
//							entity = new ResponseEntity("SUCCESS", HttpStatus.CREATED);
//						} else {
//							response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+" File Not Found\"");
//							//response.setStatus(HttpStatus.NOT_FOUND.value());							
//							entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//						response.setHeader("Content-Disposition", "attachment; filename=\" Server Error \"");
//						entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
//					}finally {
//						if(fis != null) fis.close();
//					}
//					 
//				} else {
//					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
//					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//				}
//			} else {
//				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
//				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
//			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
//		} finally {
//			try {
//				response.flushBuffer();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return entity;
//	}

	// ZappConvertPdf내의 PDFBox를 이용한 썸네일 이미지 추출
	@RequestMapping(value = "/thumbView/{versionid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity  thumbView(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		

		String Status = "SUCCESS";
		ResponseEntity entity = null;
		
		try {			
			logger.debug("=== thumbView versionid:" + versionid);

			if(ZstFwValidatorUtils.valid(versionid) == true) {
				ZArchVersion pIn = new ZArchVersion();
				pIn.setVersionid(versionid);
				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
				if(zarchVersion != null) {
					String fileName = zarchVersion.getFilename();

					ZArchFile ZarchMfile = new ZArchFile();
					ZarchMfile.setUfileid(zarchVersion.getUfileid());
					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);

					//임시영역으로 파일을 카피한다.
					//String filePath = getArchiveFilePath(zArchFile);
					//서버 저장스토리지 경로를 리턴
					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();
					String fileExt = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();

					System.out.println("=== filePath:" + filePath);
					System.out.println("=== orgName:" + fileName);
					System.out.println("=== fileExt:" + fileExt);

					File thumbFile = new File(filePath + ".png");
					
					if (!thumbFile.exists()) {
						String _DECRYPTED_PATH;
						if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
							_DECRYPTED_PATH = filePath + ".dec";
							File file = new File(_DECRYPTED_PATH);
							if (!file.exists()) {
								System.out.println("=== doDecrypt");
								CryptoNUtil.doDecrypt(new File(filePath), new File(_DECRYPTED_PATH));
							}
						} else {
							_DECRYPTED_PATH = filePath; 
						}

						if (fileExt.equals(".doc") || fileExt.equals(".docx") || fileExt.equals(".xls") || fileExt.equals(".xlsx")
								|| fileExt.equals(".ppt") || fileExt.equals(".pptx") || fileExt.equals(".txt")) {
							
							logger.error("==== This format[" + fileExt + "] must using officePdfConvertWithThumb");
//							String pdfPath = filePath + ".pdf";
//							if (ZappConverPdf.convertPdf(_DECRYPTED_PATH, pdfPath) == 0) {
//								logger.debug("=== convertPdf OK");						
//							} else {
//								logger.debug("=== convertPdf Error");												
//							}
//							String tmpPath = pdfPath.substring(0, pdfPath.lastIndexOf("\\")+1);
//							String tmpFile = pdfPath.substring(pdfPath.lastIndexOf("\\")+1, pdfPath.length());
//
//							System.out.println("=== office tmpPath:" + tmpPath);
//							System.out.println("=== office tmpFile:" + tmpFile);
//							
//							// PDFBox를 이용한 썸네일 이미지 추출
//							ZappConverPdf.makeThumb(tmpPath, tmpFile);
							
						} 
						else if (fileExt.equals(".pdf")){					
							String tmpPath = _DECRYPTED_PATH.substring(0, _DECRYPTED_PATH.lastIndexOf("\\")+1);
							String tmpFile = _DECRYPTED_PATH.substring(_DECRYPTED_PATH.lastIndexOf("\\")+1, _DECRYPTED_PATH.length());

							System.out.println("=== pdf tmpPath:" + tmpPath);
							System.out.println("=== pdf tmpFile:" + tmpFile);
							
							// PDFBox를 이용한 썸네일 이미지 추출
							makeThumb(tmpPath, tmpFile);
							
						} else if (fileExt.equals(".png") || fileExt.equals(".jpg")) {
							thumbFile = new File(_DECRYPTED_PATH);
						} else {
							System.out.println("== Not Office/PDF/Txt, so don't make Thumbnail");
							// TODO: must be change to resource image
							thumbFile = new File("D:/Archive_Repo/ZSTTT/eye_off_icon.png");
						}
					}
					
					FileInputStream fis = new FileInputStream(thumbFile);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					int readlen = 0;
					while ((readlen = fis.read(buf)) != -1) {
						baos.write(buf, 0, readlen);
					}
					byte[] imgbuf = null;
					imgbuf = baos.toByteArray();
					baos.close();
					fis.close();
					
					int len = imgbuf.length;
					ServletOutputStream os = response.getOutputStream();
					os.write(imgbuf, 0, len);
					os.close();
					 
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
				}
			} else {
				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}
	
	@RequestMapping(value = "/fileDown/{versionid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity  fileDown(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		

		String Status = "SUCCESS";
		ResponseEntity entity = null;
		
		try {			
			logger.debug("=== fileDown");
			
			if(ZstFwValidatorUtils.valid(versionid) == true) {
				ZArchVersion pIn = new ZArchVersion();
				pIn.setVersionid(versionid);
				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
				if(zarchVersion != null) {
					String fileName = zarchVersion.getFilename();

					ZArchFile ZarchMfile = new ZArchFile();
					ZarchMfile.setUfileid(zarchVersion.getUfileid());
					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);

					//임시영역으로 파일을 카피한다.
					//String filePath = getArchiveFilePath(zArchFile);
					//서버 저장스토리지 경로를 리턴
					ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
					String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();						

					String mimeType ="application/octet-stream";
					FileInputStream fis = null;

					try {
						File regFile = new File(filePath);
						fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");

						if(regFile.exists() == true){
							int contentlength = (int)regFile.length();					
							response.setContentType(mimeType.toString());
							//inline : 
							response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
							fis = FileUtils.openInputStream(regFile);							
							//암호화 여부 확인
							if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
								//원본 파일 사이즈를 지정한다..
								response.setContentLength(Integer.parseInt(String.valueOf(Math.round(zArchFile.getFilesize()))));								
								CryptoNUtil enc = new CryptoNUtil();								
								enc.doDecrypt(fis, response.getOutputStream());
							}else {
								response.setContentLength(contentlength);//물리파일의 파일 크기를 지정한다.(원본이므로)
								IOUtils.copy(fis, response.getOutputStream());
							}

							entity = new ResponseEntity("SUCCESS", HttpStatus.CREATED);
						} else {
							response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+" File Not Found\"");
							//response.setStatus(HttpStatus.NOT_FOUND.value());							
							entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
						}
					} catch (IOException e) {
						e.printStackTrace();
						response.setHeader("Content-Disposition", "attachment; filename=\" Server Error \"");
						entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
					}finally {
						if(fis != null) fis.close();
					}

				} else {
					response.setHeader("Content-Disposition", "attachment; filename=\" Data Not Found \"");
					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
				}
			} else {
				response.setHeader("Content-Disposition", "attachment; filename=\" invalid fileid \"");
				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}
	
	/**
	 * 파일을 임시영역에 카피한후 다운로드 한다.
	 * @param request
	 * @param response
	 * @param versionid
	 * @param pSession
	 * @return
	 */
	@RequestMapping(value = "/fileCopyDown/{versionid}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity  fileCopyDown(HttpServletRequest request, HttpServletResponse response,@PathVariable String versionid, HttpSession pSession) {		

		String Status = "SUCCESS";
		ResponseEntity entity = null;
		
		try {			
			logger.debug("=== fileCopyDown");

			if(ZstFwValidatorUtils.valid(versionid) == true) {
				
				ZArchVersion pIn = new ZArchVersion();
				pIn.setVersionid(versionid);
				ZArchVersion zarchVersion =  zArchVersionService.rSingleRow_Vo(pIn);
				if(zarchVersion != null) {
					String fileName = zarchVersion.getFilename();

					ZArchFile ZarchMfile = new ZArchFile();
					ZarchMfile.setUfileid(zarchVersion.getUfileid());
					ZArchFile zArchFile = zArchFileService.rSingleRow_Vo(ZarchMfile);

					String filePath = getArchiveFilePath(zArchFile);
					String mimeType ="application/octet-stream";
					FileInputStream fis = null;
					try {
						File regFile = new File(filePath);
						fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
						if(regFile.exists() == true){
							int contentlength = (int)regFile.length();					
							response.setContentType(mimeType.toString());
							response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");//inline : 
							response.setContentLength(contentlength);
							fis = FileUtils.openInputStream(regFile);
							IOUtils.copy(fis, response.getOutputStream());
							response.flushBuffer();
							entity = new ResponseEntity("SUCCESS", HttpStatus.CREATED);
						} else {
							entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
						}
					} catch (IOException e) {
						e.printStackTrace();
						entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
					}finally {
						if(null!=fis) fis.close();
					}

				} else {
					entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
				}
			} else {
				entity = new ResponseEntity("FAIL", HttpStatus.NOT_FOUND);
			}

		} catch (FileUploadException e) {	
			e.printStackTrace();
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
		}	

		return entity;
	}
	
	

	// 드래그앤 드랍 시쿼스 번호 조회
	@RequestMapping(value = "/seqnum", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse resonse,  HttpSession session) {
		String contNum ="";
		ZappAuth pObjAuth = getAuth(session);
		resonse.setContentType("text/html; charset=UTF-8");
	    try {
	    	contNum = zappContentMgtService.getContentNo(pObjAuth);
	    	System.out.println("contNum : " + contNum);
	    	return contNum;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return contNum;
	}
	
	
	
	/**
	 * 파일을 비교한다.
	 * @param resonse
	 * @param uFileId1
	 * @param uFileId2
	 * @param objTaskId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/compareVersion", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String compareVersion(HttpServletResponse resonse
							   , @RequestParam String uFileId1
							   , @RequestParam String uFileId2
							   , @RequestParam String objTaskId) throws Exception {

		String diffStr = "";

		resonse.setContentType("text/html; charset=UTF-8");

		try {

			ZArchVersion pIn1 = new ZArchVersion();
			pIn1.setVersionid(uFileId1);
			ZArchVersion zarchVersion1 =  zArchVersionService.rSingleRow_Vo(pIn1);

			ZArchFile ZarchMfile1 = new ZArchFile();
			ZarchMfile1.setUfileid(zarchVersion1.getUfileid());
			ZArchFile zArchFile1 = zArchFileService.rSingleRow_Vo(ZarchMfile1);
			String filePath1 = getArchiveFilePath(zArchFile1);

			ZArchVersion pIn2 = new ZArchVersion();
			pIn2.setVersionid(uFileId2);
			ZArchVersion zarchVersion2 =  zArchVersionService.rSingleRow_Vo(pIn2);

			ZArchFile ZarchMfile2 = new ZArchFile();
			ZarchMfile2.setUfileid(zarchVersion2.getUfileid());
			ZArchFile zArchFile2 = zArchFileService.rSingleRow_Vo(ZarchMfile2);

			String filePath2 = getArchiveFilePath(zArchFile2);			
			String parsedFile1 = ZstFwExtractUtils.parseToFile(filePath1);
			String parsedFile2 = ZstFwExtractUtils.parseToFile(filePath2);			

			//diffStr = ZstFwDiffUtils.getUnifiedDiffStr(parsedFile1, parsedFile2);

			diffStr = getUnifiedDiffStr(parsedFile1, parsedFile2);

			return diffStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	

	/**
	 * 
	 * @param srcFile
	 * @param dstFile
	 * @return
	 * @throws PatchFailedException
	 * @throws IOException
	 */
	private  String getUnifiedDiffStr(String srcFile, String dstFile) throws PatchFailedException, IOException {
		
		StringBuffer sbUnifiedDiff = new StringBuffer();
		
		try {
			List<String> original = Files.readAllLines(new File(srcFile).toPath());
			List<String> patched = Files.readAllLines(new File(dstFile).toPath());

			//generating diff information.
			Patch<String> diff = DiffUtils.diff(original, patched);

			//generating unified diff format
			List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(srcFile, dstFile, original, diff, 9999);
			System.out.println("\n=========== unifiedDiff");        
			for(int i=0; i<unifiedDiff.size(); i++) {
				//System.out.println(unifiedDiff.get(i));
				sbUnifiedDiff.append(unifiedDiff.get(i));
				sbUnifiedDiff.append("\n");
			}

			return sbUnifiedDiff.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * 파일 경로를 조회한다.
	 * @param zArchFile
	 * @return
	 * @throws Exception
	 */
	private String getArchiveFilePath(ZArchFile zArchFile) throws Exception {

		StringBuffer storeFile = new StringBuffer();
		StringBuffer downFile = new StringBuffer();

		ZArchResult zArchResult = new ZArchResult();
		String fullFilePath = "";

		String Ufile = zArchFile.getUfileid();

		downFile.append(FilenameUtils.normalize(downTempPath));
		if (downTempPath.lastIndexOf(File.separator) == downTempPath.length()) {
			downFile.append(File.separator);
		}
		downFile.append(UUID.randomUUID());

		zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 	
		String filePath = (String)zArchResult.getResult();						
		storeFile.append(FilenameUtils.normalize(filePath+Ufile));			

		if(zArchFile.getIsencrypted().toString().trim().equals(YES)){			
			fullFilePath = downFile.toString();
			File InFile = new File(storeFile.toString());							
			File OutFile = new File(fullFilePath);							

			CryptoUtil.doDecrypt(InFile, OutFile);

		} else { 
			/*
			if (!FileUtil.copyFile(storeFile.toString(), downFile.toString())) {
				new ZArchFileException("It failed to copy the stored file to the download directory.");
			}
			 */
			fullFilePath = storeFile.toString();
		}

		return fullFilePath;
	}
	
	// PDFBox를 이용한 썸네일 이미지 추출
	private ArrayList<String> makeThumb(String fpath, String fname){
		//이미지로 변환한 파일 리스트를 담는 객체
		ArrayList<String> al = new ArrayList();
		try {
			String realfname;
			
			if (fname.lastIndexOf(".") > 0) {
				realfname = fname.substring(0, fname.lastIndexOf("."));
			} else {
				realfname = fname;
			}
			String imgFileName = fpath + realfname + ".png";
			
			File thumbFile = new File(imgFileName);
			if (thumbFile.exists())
				return null;

			//PDF파일을 읽어온다.
			InputStream is = new FileInputStream(fpath + fname);
			//스트림으로 읽어온 데이터를 document에 넣는다.
			PDDocument doc = PDDocument.load(is);
			//document에 들어온 데이터를 정제한다.
			PDFRenderer render = new PDFRenderer(doc);
			//이미지 저장경로
			//경로를 따로 지정할경우 폴더를 생성하는 로직
			//Files.createDirectories(Paths.get(fPath));
			//System.out.println("시작");
			//Date aa = new Date();
			//System.out.println(aa.getHours() + ":" + aa.getMinutes() + ":" + aa.getSeconds());
			System.out.println("=== cnt:" + doc.getPages().getCount());
			//for (int i = 0; i < doc.getPages().getCount(); i++) {
				al.add(imgFileName);
				BufferedImage bim = render.renderImageWithDPI(0, 30, ImageType.RGB);
				
				ImageIOUtil.writeImage(bim, imgFileName, 40);
			//}
		    is.close();
			doc.close();
			//Date aa1 = new Date();
			//System.out.println(aa1.getHours() + ":" + aa1.getMinutes() + ":" + aa1.getSeconds());
			//System.out.println("끝");
		} catch(FileNotFoundException e) {
			al.add("FileError");
		} catch(IOException e) {
			al.add("IOError");
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			al.add("StringError");
			e.printStackTrace();
		} catch (Exception e) {
			al.add("Error");
			e.printStackTrace();
		}
		return al;
	}
	
}
