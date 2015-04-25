package it.sbsoft.beans;

import java.util.LinkedHashMap;
import java.util.Map;

public class FileListObj {
	
	static public class FileObj {
		
		String id = "";
		String dtFirst = "";
		String dtLast = "";
		String fileUri = "";
		String month = "";
		String year = "";
		String fileName = "";
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		
		public String getDtFirst() {
			return dtFirst;
		}
		public void setDtFirst(String dtFirst) {
			this.dtFirst = dtFirst;
		}
		public String getDtLast() {
			return dtLast;
		}
		public void setDtLast(String dtLast) {
			this.dtLast = dtLast;
		}
		public String getFileUri() {
			return fileUri;
		}
		public void setFileUri(String fileUri) {
			this.fileUri = fileUri;
		}
	}
	
	private Map<String, FileObj> fileMap = new LinkedHashMap<String, FileObj>();

	public Map<String, FileObj> getMp() {
		return fileMap;
	}

	public void setMp(Map<String, FileObj> mp) {
		this.fileMap = mp;
	}
	
    
}