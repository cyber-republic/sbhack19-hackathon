package com.elastonias.container.util;

import java.io.File;

public class CustomUtil{
    public static File getDirectoryByName(String dirName){
        String filesDirName="files";
        File filesDir=new File(filesDirName);
        if(!filesDir.exists()){
            filesDir.mkdir();
        }
        File dir=new File(filesDir, dirName);
        if(!dir.exists()){
            dir.mkdir();
        }
        return dir;
    }
}
