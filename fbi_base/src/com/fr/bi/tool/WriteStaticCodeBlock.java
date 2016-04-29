package com.fr.bi.tool;

import java.io.IOException;

/**
 * Created by Connery on 2016/1/22.
 */
public abstract class WriteStaticCodeBlock extends JavaFileAnalyser {
    public WriteStaticCodeBlock(String sourceAbsoluteFilePath, FileOperation fileOperation, String targetPath) throws IOException {
        super(sourceAbsoluteFilePath, fileOperation, targetPath);
    }

    public String generateCode() {
        int firstBlanket = classBodyStartIndex();
        String start = javaContent.substring(0, firstBlanket + 1);
        String end = javaContent.substring(firstBlanket + 1);
        start += "\r\n\tstatic{";
        end = "}" + end;
        javaContent = start + generateStaticCodeBlock() + end;
        return javaContent;
    }



    public abstract String generateStaticCodeBlock();

}