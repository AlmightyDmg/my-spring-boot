package cn.com.dmg.myspringboot.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AddText2PdfDto {
    //页面的整体宽度
    private Integer pageWidth;
    //页面的整体高度
    private Integer pageHeight;
    //偏移量
    private Integer offset;
    //文字内容
    private List<AddText2PdfContentDto> contents;
}
