package com.yuenov.open.model.responseModel;

import com.yuenov.open.model.standard.BookMenuItemInfo;
import com.renrui.libraries.model.baseObject.BaseResponseModel;

import java.util.List;

/**
 * 目录
 */
public class MenuListResponse extends BaseResponseModel {

    public int id;
    public String title;
    public String author;
    public String desc;
    public String word;
    public String coverImg;

    public List<BookMenuItemInfo> chapters;
}