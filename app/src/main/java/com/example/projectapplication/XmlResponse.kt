package com.example.projectapplication

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name="response")
data class XmlResponse(
    @Element
    val body : myXmlBody
)

@Xml(name="body")
data class myXmlBody(
    @Element
    val items : myXmlItems,

)

@Xml(name="items")
data class myXmlItems(
    @Element
    val item : MutableList<myXmlItem>,
    @PropertyElement
    val numOfRows: Int?,
    @PropertyElement
    val pageNo: Int?,
    @PropertyElement
    val totalCount: Int?
)

@Xml(name="item")
data class myXmlItem(
    @PropertyElement
    val cntntsNo:String?,
    @PropertyElement
    val cntntsSj:String?,
    @PropertyElement
    val rtnFileUrl:String?,
) {
    constructor() : this(null,null, null)
}
