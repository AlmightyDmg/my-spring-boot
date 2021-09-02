package cn.com.dmg.myspringboot.dtos;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class AddText2PdfContentDto {
    //x坐标
    private Integer x;
    //y坐标
    private Integer y;
    //宽度
    private Integer width;
    //高度
    private Integer height;
    //内容
    private String content;
}
