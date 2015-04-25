package it.sbsoft.utility;

import static java.nio.file.FileVisitResult.*;
import it.sbsoft.beans.FileListObj.FileObj;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileVisitorObj extends SimpleFileVisitor<Path> {

	private PathMatcher matcher;
	public Map<String, FileObj> st = new LinkedHashMap<String, FileObj>(); 
	
	FileVisitorObj() {}
	
	public void setMatcher(String pattern) {
		System.out.println("setting pattern to -> " + pattern);
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	}
	
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
    	
    	Path name = file.getFileName();
    	System.out.println("name -> " + name);
    	System.out.println("file.toUri -> " + file.toUri());
    	
    	if (attr.isSymbolicLink()) {
            //System.out.format("Symbolic link: %s ", file);
        } else if (attr.isRegularFile()) {
        	if (name != null && matcher.matches(name)) {

        		System.out.format("Just the name: %s \n", file.getFileName());
        		FileObj fo = new FileObj();
        		fo.setFileUri(file.toUri().toString());
        		fo.setFileName(file.getFileName().toString());
        		st.put(file.getFileName().toString(), fo);
        		
        	}
        } else {
            //System.out.format("Other: %s ", file);
        }
        //System.out.println("(" + attr.size() + "bytes)");
        return CONTINUE;
    }

    // Print each directory visited.
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        //System.out.format("Directory: %s%n", dir);
        return CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException 
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
}