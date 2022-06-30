package com.zenithst.core.content.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.zenithst.archive.api.ZArchFileMgtService;
import com.zenithst.archive.api.ZArchMFileMgtService;
import com.zenithst.archive.service.ZArchFileService;
import com.zenithst.archive.util.CryptoNUtil;
import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchMFileRes;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwEncodeUtils;
import com.zenithst.framework.util.ZstFwExtractUtils;
import com.zenithst.framework.util.ZstFwFileUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;


/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of FTR info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappContentService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappFtrService")
public class ZappFtrServiceImpl extends ZappService implements ZappFtrService {
	
	/* Master file */
	@Autowired
	private ZArchMFileMgtService zarchMfileMgtService;
	
	/* Unique file */
	@Autowired
	private ZArchFileMgtService zarchfileMgtService;
	@Autowired
	private ZArchFileService zarchfileService;
	
	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Index Path  */
	@Value("#{archiveconfig['FTR_STORE_PATH']}")
	protected String FTR_STORE_PATH;
	
	/* Parsing Index Path  */
	@Value("#{archiveconfig['FTR_PARSE_PATH']}")
	protected String FTR_PARSE_PATH;	
	
	/* Index File Type  */
	@Value("#{archiveconfig['FTR_IDX_FILETYPE']}")
	protected String FTR_IDX_FILETYPE;		
	
	/* */
	static IndexWriter indexWriter;
	
	
	/**
	 * <pre>
	 * Creating index
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth
	 * @param pObjContent ZappContentPar
	 * @param pObjRes ZstFwResult
	 * @return ZstFwResult
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */	
	@Async
	public ZstFwResult executeIndex(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, IOException {
		
		/* Variables */
		List<ZArchFile> pToBeIndexedFiles = new ArrayList<ZArchFile>();		// To be indexed
		List<ZArchFile> pToBeDeindexedFiles = new ArrayList<ZArchFile>();	// To be deindexed
		List<ZArchFile> pNotCompletedFiles = new ArrayList<ZArchFile>();	// Not completed
		CryptoNUtil cryptoNUtil = new CryptoNUtil();
		
		/* Validation */
		/* 호출처에서 pObjContent.setObjHandleType() 셋팅되도록 수정함.
		pObjContent.setObjHandleType(ZappConts.ACTION.CHANGE.name());
		if(pObjContent.getZappFiles() == null) {
			pObjContent.setObjHandleType(ZappConts.ACTION.ADD.name());
		}
		if(pObjContent.getZappFiles().size() == ZERO) {
			pObjContent.setObjHandleType(ZappConts.ACTION.ADD.name());
		}		
		*/
		
		logger.info("=== getObjHandleType: " + pObjContent.getObjHandleType());
		
		if(pObjContent.getObjHandleType() == null || pObjContent.getObjHandleType().equals("") ) {
			if(pObjContent.getZappFiles() == null) {
				pObjContent.setObjHandleType(ZappConts.ACTION.ADD.name());
			}
			if(pObjContent.getZappFiles().size() == ZERO) {
				pObjContent.setObjHandleType(ZappConts.ACTION.ADD.name());
			}		

			logger.info("=== getZappFiles: " + pObjContent.getZappFiles());
			logger.info("=== getZappFiles.size: " + pObjContent.getZappFiles().size());
			
		}
		
		pObjRes = valid(pObjAuth, pObjContent, ZappConts.ACTION.ADD, "executeIndex", pObjRes);
		
		
		
		
		/* Inquiry File info. */
		ZArchResult rZArchResult = new ZArchResult();
		if(pObjContent.getObjHandleType().equals(ZappConts.ACTION.ADD.name())) {	// Add

			ZArchMFile pZArchMFile = new ZArchMFile();
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pZArchMFile.setLinkid(pObjContent.getContentid());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pZArchMFile.setMfileid(pObjContent.getContentid());
			}
			
			try {
				rZArchResult = zarchMfileMgtService.listMFileAll(pZArchMFile, null, null);
			} catch (Exception e) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[executeIndex] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(ZappFinalizing.isSuccess(rZArchResult) == false) {
				return ZappFinalizing.finalising_Archive(rZArchResult.getCode(), pObjAuth.getObjlang());
			}		
			@SuppressWarnings("unchecked")
			List<ZArchMFileRes> rZArchFileList = (List<ZArchMFileRes>) rZArchResult.getResult();
			if(rZArchFileList != null) {
				for(ZArchMFileRes vo : rZArchFileList) {
					ZArchFile pZArchFile = new ZArchFile();
					pZArchFile.setObjTaskid(pObjContent.getObjTaskid());			// Task ID
					pZArchFile.setUfileid(vo.getzArchFile().getUfileid());			// Unique File ID
					pZArchFile.setCabinetid(vo.getzArchFile().getCabinetid());		// Cabinet ID
					pZArchFile.setCreatetime(vo.getzArchFile().getCreatetime());	// Create Time
					
					try {
						rZArchResult = zarchfileMgtService.getExistingArchivePath(pZArchFile);
					} catch (Exception e) {
						return ZappFinalizing.finalising("ERR_R_FILE", "[executeIndex] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(ZappFinalizing.isSuccess(rZArchResult) == false) {
						pNotCompletedFiles.add(pZArchFile);
						continue;
					}
					
					// Stored Path
					String _STORED_PATH = ZstFwFileUtils.addSeperator((String) rZArchResult.getResult()) + vo.getzArchFile().getUfileid();
					logger.debug("=== _STORED_PATH: " + _STORED_PATH);
					// Encryption
					if(vo.getzArchFile().getIsencrypted().equals(YES)) {
						String _DECRYPTED_PATH = _STORED_PATH + ".decrypt";
						try {
							cryptoNUtil.doDecrypt(new File(_STORED_PATH), new File(_DECRYPTED_PATH));
						} catch (Exception e) {
							return ZappFinalizing.finalising("ERR_R_FILE", "[executeIndex] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						pZArchFile.setObjFilePath(_DECRYPTED_PATH);	// Stored File Path
					} else {
						pZArchFile.setObjFilePath(_STORED_PATH);	// Stored File Path
					}
					
					pToBeIndexedFiles.add(pZArchFile);
				}
			}
			
		}  else if(pObjContent.getObjHandleType().equals(ZappConts.ACTION.CHANGE.name())) {	// Change
			
			for(ZappFile vo : pObjContent.getZappFiles()) {
				if(vo.getAction().equals(ZappConts.ACTION.ADD.name())) {
					ZArchFile pZArchFile = new ZArchFile();
					pZArchFile.setObjTaskid(pObjContent.getObjTaskid());		// Task ID
					pZArchFile.setUfileid(vo.getMfileid());						// Unique File ID
					try {
						ZArchFile rZArchFile = zarchfileService.rSingleRow_Vo(pZArchFile);
						rZArchResult = zarchfileMgtService.getExistingArchivePath(rZArchFile);
					} catch (Exception e) {
						return ZappFinalizing.finalising("ERR_R_FILE", "[executeIndex] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(ZappFinalizing.isSuccess(rZArchResult) == false) {
						pNotCompletedFiles.add(pZArchFile);
						continue;
					}
					pZArchFile.setObjFilePath(ZstFwFileUtils.addSeperator((String) rZArchResult.getResult()) + vo.getMfileid());	// Stored File Path
					pToBeIndexedFiles.add(pZArchFile);
				}
				if(vo.getAction().equals(ZappConts.ACTION.DISCARD.name())) {
					ZArchFile pZArchFile = new ZArchFile();
					pZArchFile.setObjTaskid(pObjContent.getObjTaskid());		// Task ID
					pZArchFile.setUfileid(vo.getMfileid());						// Unique File ID
					pToBeDeindexedFiles.add(pZArchFile);
				}
			}
		}
		
		/* Preparation */
		ZstFwFileUtils.makeDir(ZstFwFileUtils.revisePath(FTR_STORE_PATH));		// Index Store Path
		String FINAL_PARSE_PATH = ZstFwFileUtils.revisePath(ZstFwFileUtils.addSeperator(FTR_PARSE_PATH)) + pObjContent.getContentid(); 
		ZstFwFileUtils.makeDir(FINAL_PARSE_PATH); 	// Temporary Index Store Path
		
		/* Configuration */
		configIndexWriter();
		
		/* Indexing */
		for(ZArchFile tobevo : pToBeIndexedFiles) {
			
			// Checking File
			File checkFile = new File(ZstFwFileUtils.revisePath(tobevo.getObjFilePath()));
//			if(checkFile.isDirectory() == false && checkFile.isHidden() == false 
//					&& checkFile.exists() == true && checkFile.canRead() == true) {
//				logger.debug("=== checkFile not file or else");
//				pNotCompletedFiles.add(tobevo);
//				continue;
//			}
				
			// Parsed file
			boolean SUCCESS_INDEX = true; 
			File parseFile = new File(ZstFwFileUtils.revisePath(tobevo.getObjFilePath()) + ".parse");
			File decryptFile = new File(ZstFwFileUtils.revisePath(tobevo.getObjFilePath()));
			logger.debug("=== parseFile:" + parseFile.getCanonicalPath());
			logger.debug("=== decryptFile:" + decryptFile.getCanonicalPath());
			if(parseFile.exists() == false) {
				String PARSED_FILENAME = ZstFwExtractUtils.parseToFile(ZstFwFileUtils.revisePath(tobevo.getObjFilePath()));
				if(ZstFwValidatorUtils.valid(PARSED_FILENAME) == false) {
					SUCCESS_INDEX = false;
				}
			}
			if(SUCCESS_INDEX == true) {
				pObjRes = indexFile(pObjContent.getContentid()
								  , pObjContent.getObjType()
								  , tobevo.getUfileid()
								  , parseFile.getAbsolutePath()
								  , pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_FTR_INDEXING", "[executeIndex] " + messageService.getMessage("ERR_FTR_INDEXING",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			// Clear file
			if (!parseFile.delete()) {
				logger.debug("== parseFile delete Err");				
				//System.gc();
				//System.runFinalization();
			}
			if (!decryptFile.delete()) {
				logger.debug("== decryptFile delete Err");				
			}
		}

		/* Deindexing */
		for(ZArchFile tobevo : pToBeDeindexedFiles) {
			pObjRes = deindexFile(pObjContent.getContentid(), pObjContent.getObjType(), tobevo.getUfileid(), pObjRes);
		}
		
		/* Not Completed Files */
		if(pNotCompletedFiles.size() > ZERO) {
			pObjRes.setResObj(pNotCompletedFiles);	
		}

		indexWriter.close();
		
		return pObjRes;
	}
	
	/**
	 * <pre>
	 * Searching index
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth
	 * @param pObjContent ZappContentPar
	 * @param pObjRes ZstFwResult
	 * @return ZstFwResult
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */	
	public ZstFwResult executeSearching(ZappAuth pObjAuth, ZappContentPar pObj, ZstFwResult pObjRes) throws ZappException, IOException {
		
		Map<String, List<ZappContentRes>> SEARCHLIST = new HashMap<String, List<ZappContentRes>>();
		
		/* Validation */
		if(ZstFwValidatorUtils.valid(FTR_STORE_PATH) == false) {
			return ZappFinalizing.finalising("ERR_MIS_IDX_PATH", "[executeSearching] " + messageService.getMessage("ERR_MIS_IDX_PATH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		if(ZstFwValidatorUtils.valid(pObj.getSword()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_WORD", "[executeSearching] " + messageService.getMessage("ERR_MIS_WORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 		
		
		/* */
		Path path = FileSystems.getDefault().getPath(ZstFwFileUtils.revisePath(FTR_STORE_PATH));
		
		try {
			logger.debug("=== FTR path: " + path.toString());
			
			Directory directory = new NIOFSDirectory(path);
			IndexReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
	
			KoreanAnalyzer analyzer = new KoreanAnalyzer();
			analyzer.setBigrammable(false);
			
//			QueryParser qp = new QueryParser("content", new StandardAnalyzer());
			QueryParser qp = new QueryParser("content", analyzer);
			qp.setDefaultOperator(Operator.OR);
			Query query = qp.parse(pObj.getSword());
	
			TopDocs tops = null;

			SortField sf = new SortField("order", SortField.Type.INT, false);
			Sort sort = new Sort(sf);
			tops = searcher.search(query, 1000);
			int total = tops.scoreDocs.length;
		
			logger.debug("=== executeSearching total: " + total);
			
			for (int i = 0; tops != null && i < total && i < tops.totalHits; i++) {
	
				Document doc = searcher.doc(tops.scoreDocs[i].doc);
	
				String content = doc.get("content");
				String hi = hilight(query, reader, "content", analyzer, pObj.getSword());
				String newContent = content.replaceAll(pObj.getSword(), "<b><font color='blue'>" + pObj.getSword() + "</font></b>");

				ZappContentRes rZappContentRes = new ZappContentRes();
				rZappContentRes.setContentid(doc.get("contentid"));
				rZappContentRes.setContenttype(doc.get("contenttype"));
				rZappContentRes.setVersion(doc.get("versionid"));
				//rZappContentRes.setSummary(doc.get("content"));
				rZappContentRes.setSummary(newContent);
				rZappContentRes.setFtrResult(newContent);
				rZappContentRes.setFiles(doc.get("filename"));
				List<ZappContentRes> objFile = null;
				if(SEARCHLIST.containsKey(doc.get("contentid") + ZstFwConst.SCHARS.UNDERSCORE.character + doc.get("contenttype"))) {
					objFile = (List<ZappContentRes>) SEARCHLIST.get(doc.get("contentid") + ZstFwConst.SCHARS.UNDERSCORE.character + doc.get("contenttype"));
				} else {
					objFile = new ArrayList<ZappContentRes>();
				}
				objFile.add(rZappContentRes);
				SEARCHLIST.put(doc.get("contentid") + ZstFwConst.SCHARS.UNDERSCORE.character + doc.get("contenttype"), objFile);
			}
	
			reader.close();
			directory.close();
			
		} catch (Exception e) {
			return ZappFinalizing.finalising("ERR_FTR", "[executeSearching] " + messageService.getMessage("ERR_FTR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		pObjRes.setResObj(SEARCHLIST);
		
		return pObjRes;
	}
	
	/**
	 * <pre>
	 * Deleting index
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth
	 * @param pObjContent ZappContentPar
	 * @param pObjRes ZstFwResult
	 * @return ZstFwResult
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */	
	public ZstFwResult executeDeleting(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, IOException {
		
		/* Validation */
		if(ZstFwValidatorUtils.valid(pObjContent.getContentid()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_CONTENTID", "[executeDeleting] " + messageService.getMessage("ERR_MIS_CONTENTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		if(ZstFwValidatorUtils.valid(pObjContent.getObjType()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_CONTENTTYPE", "[executeDeleting] " + messageService.getMessage("ERR_MIS_CONTENTTYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 		
		
		/* */
		pObjRes = deindexFile(pObjContent.getContentid(), pObjContent.getObjType(), BLANK, pObjRes);	

        return pObjRes;
	}
	
	/**
	 * <pre>
	 * Checking index
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth
	 * @param pObjContent ZappContentPar
	 * @param pObjRes ZstFwResult
	 * @return ZstFwResult
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	public ZstFwResult existIndex(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, IOException {
		
		pObjRes.setResObj(false);
		
		/* Validation */
		pObjRes = valid(pObjAuth, pObjContent, ZappConts.ACTION.VIEW, "existIndex", pObjRes);	
		
		/* */
		Path path = FileSystems.getDefault().getPath(ZstFwFileUtils.revisePath(FTR_STORE_PATH));
		
		try {
			
			Directory directory = new NIOFSDirectory(path);
			IndexReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
	
			KoreanAnalyzer analyzer = new KoreanAnalyzer();
			analyzer.setBigrammable(false);
			
			QueryParser qp = new QueryParser("uid", analyzer);
			qp.setDefaultOperator(Operator.OR);
			Query query = qp.parse(ZstFwEncodeUtils.encodeString_SHA256(pObjContent.getContentid()));	// Content ID + File ID + Content Type
	
			TopDocs tops = null;

			SortField sf = new SortField("order", SortField.Type.INT, false);
			Sort sort = new Sort(sf);
			tops = searcher.search(query, 1000);
			int total = tops.scoreDocs.length;
		
			for (int i = 0; tops != null && i < total && i < tops.totalHits; i++) {
	
				Document doc = searcher.doc(tops.scoreDocs[i].doc);
				if(doc != null) {
					pObjRes.setResObj(true);
					break;
				}
			}
	
			reader.close();
			directory.close();
			
		} catch (Exception e) {
			return ZappFinalizing.finalising("ERR_MIS_KEYWORD", "[executeSearching] " + messageService.getMessage("ERR_MIS_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
		
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	private void configIndexWriter() throws IOException {

		KoreanAnalyzer analyzer = new KoreanAnalyzer();
		analyzer.setBigrammable(false);
		
		if (indexWriter == null || !indexWriter.isOpen()) {
			Path path = FileSystems.getDefault().getPath(FTR_STORE_PATH);
			Directory directory = new NIOFSDirectory(path);
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
		}
	}
	
	/**
	 * 
	 * @param objId
	 * @param objFile
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private ZstFwResult indexFile(String objContentid, String objContentType, String objFileId, String objFile, ZstFwResult pObjRes) throws IOException {
		
		String question;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(objFile), "UTF-8"));

			int id = 1;
			while ((question = br.readLine()) != null) {
				Document doc = new Document();
				doc.add(new TextField("uid", ZstFwEncodeUtils.encodeString_SHA256(objContentid + objFileId + objContentType), Field.Store.YES));			// UID
				doc.add(new TextField("contentid", objContentid, Field.Store.YES));			// Content ID
				doc.add(new TextField("contenttype", objContentType, Field.Store.YES));		// Content Type
				doc.add(new TextField("verionid", objFileId, Field.Store.YES));				// File Version ID
				doc.add(new TextField("content", question, Field.Store.YES));				// File Content
				doc.add(new TextField("filename", ZstFwFileUtils.getOnlyName(objFile), Field.Store.YES));				// File Path
	
				indexWriter.addDocument(doc);
	
				if (id % 100 == 0) {
					logger.info("> " + id + " sentences !!!");
				}
				id++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
		}		
		return pObjRes;
	}	
	
	/**
	 * Deleting an Index
	 * @param objContentid			- Content ID
	 * @param objContentType		- Content Type
	 * @param objFileId				- Unique File ID
	 * @param pObjRes
	 * @return
	 * @throws IOException
	 */
	private ZstFwResult deindexFile(String objContentid, String objContentType, String objFileId, ZstFwResult pObjRes) throws IOException {
		
		Term term = null;
		if(ZstFwValidatorUtils.valid(objFileId) == true) {
			term = new Term("uid", ZstFwEncodeUtils.encodeString_SHA256(objContentid + objFileId + objContentType)); 
		} else {
			term = new Term("contentid", objContentid); 
		}
		
		configIndexWriter();
        indexWriter.deleteDocuments(term);
        //indexWriter.close();	
        
		return pObjRes;
	}
	
	/**
	 * 
	 * @return
	 */
	private List<String> getIndexFileType() {
		
		List<String> rList = new ArrayList<String>();
		
		if(ZstFwValidatorUtils.valid(FTR_IDX_FILETYPE)) {
			String[] tmp = FTR_IDX_FILETYPE.split("：");
			if(tmp != null) {
				rList = Arrays.asList(tmp);
			}
		}
		
		return rList;
	}	
	
	/**
	 * 
	 * @param query
	 * @param reader
	 * @param fieldName
	 * @param analyzer
	 * @param fieldStr
	 * @return
	 */
	public static String hilight(Query query, IndexReader reader, String fieldName, Analyzer analyzer, String fieldStr) {

		QueryScorer scorer = new QueryScorer(query, reader, fieldName);
		Highlighter highlighter = new Highlighter(scorer);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		highlighter.setTextFragmenter(fragmenter);

		String fragment = null;
		try {
			fragment = highlighter.getBestFragment(analyzer, fieldName, fieldStr);
		} catch (Exception e) {
			e.printStackTrace();
			fragment = null;
		}

		return fragment;
	}		

	/**
	 * 
	 * @param pObjAuth
	 * @param pObjContent
	 * @param pObjAct
	 * @param pObjRes
	 * @return
	 */
	private ZstFwResult valid(ZappAuth pObjAuth, ZappContentPar pObjContent, ZappConts.ACTION pObjAct, String pCaller, ZstFwResult pObjRes) {
		
		if(ZstFwValidatorUtils.valid(FTR_STORE_PATH) == false) {
			return ZappFinalizing.finalising("ERR_MIS_IDX_PATH", "[" + pCaller + "] " + messageService.getMessage("ERR_MIS_IDX_PATH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		if(ZstFwValidatorUtils.valid(FTR_PARSE_PATH) == false) {
			return ZappFinalizing.finalising("ERR_MIS_TMP_IDX_PATH", "[" + pCaller + "] " + messageService.getMessage("ERR_MIS_TMP_IDX_PATH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 	
		if(ZstFwValidatorUtils.valid(pObjContent.getContentid()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_CONTENTID", "[" + pCaller + "] " + messageService.getMessage("ERR_MIS_CONTENTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		if(ZstFwValidatorUtils.valid(pObjContent.getObjType()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_CONTENTTYPE", "[" + pCaller + "] " + messageService.getMessage("ERR_MIS_CONTENTTYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		
		switch(pObjAct) {
			case ADD: 
				if(ZstFwValidatorUtils.valid(pObjContent.getObjTaskid()) == false) {
					return ZappFinalizing.finalising("ERR_MIS_TASKID", "[" + pCaller + "] " + messageService.getMessage("ERR_MIS_TASKID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				} 				
			break;
			case CHANGE: 
			break;
			case DISCARD: 

			break;
			case VIEW: 

			break;
			default:
		}

		return pObjRes;
	}		
	
}
