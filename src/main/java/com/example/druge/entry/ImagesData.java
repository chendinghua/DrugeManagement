package com.example.druge.entry;

/** 图片数据对象
 * Created by 16486 on 2020/3/31.
 */
public class ImagesData {

    private Integer id;             //图片资源id
    private String Path;            //图片资源路径

    public ImagesData(Integer id, String path) {
        this.id = id;
        Path = path;
    }
    public  ImagesData(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }
}
