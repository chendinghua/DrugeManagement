package com.example.druge.entry;

/**
 * Created by Administrator on 2019/2/14/014.
 */
//报废图片信息内容
public class ScrapFileList {

    private int ID;                 //编号
    private String FileName;        //上传文件名
    private String SaveName;        //保存文件名
    private String FilePath;        //文件路径名
    private String FileExt;         //文件类型
    private int Status;             //状态
    private String ViewUrl;         //云服务文件访问网址

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getSaveName() {
        return SaveName;
    }

    public void setSaveName(String saveName) {
        SaveName = saveName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileExt() {
        return FileExt;
    }

    public void setFileExt(String fileExt) {
        FileExt = fileExt;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getViewUrl() {
        return ViewUrl;
    }

    public void setViewUrl(String viewUrl) {
        ViewUrl = viewUrl;
    }
}
