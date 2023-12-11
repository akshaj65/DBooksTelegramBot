package com.akshaj.repository;

import java.util.concurrent.ConcurrentHashMap;

public class TFilesRepository {
    private  static TFilesRepository instance=null;
    private final ConcurrentHashMap<String, String > fileMap;

    public static TFilesRepository getInstance(){
        if(instance==null){
            instance= new TFilesRepository(); //this calls the constructor once
        }
        return instance;
    }
    private TFilesRepository(){
        fileMap=new ConcurrentHashMap<>();
    }

    public void addFile(String bookId,String fileId){
        fileMap.putIfAbsent(bookId, fileId);
    }
    public String getFileId(String bookId){
        return fileMap.get(bookId);
    }
    public int getNoOfFileIds(){
        return fileMap.size();
    }

}
