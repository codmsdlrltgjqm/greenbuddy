package com.example.projectapplication

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import java.io.Serializable

@Xml(name="response")
data class XmlDetailResponse(
    @Element
    val body: myXmlDetailBody
)

@Xml(name="body")
data class myXmlDetailBody(
    @Element
    val item: myXmlDetailItem? // Nullable로 변경
)

@Xml(name="item")
data class myXmlDetailItem(
    @PropertyElement
    val cntntsNo: Int?, // 컨텐츠번호
    @PropertyElement
    val plntbneNm: String?, // 식물학 명
    @PropertyElement
    val plntzrNm: String?, // 식물학 명
    @PropertyElement
    val adviseInfo: String?, // 조언 정보
    @PropertyElement
    val prpgtEraInfo: String?, // 번식 시기 정보
    @PropertyElement
    val soilInfo: String?, // 토양 정보
    @PropertyElement
    val hdCodeNm: String?, // 습도 코드명
    @PropertyElement
    val frtlzrInfo: String?, // 비료 정보
    @PropertyElement
    val watercycleSprngCodeNm: String?, // 물주기 봄 코드
    @PropertyElement
    val watercycleSummerCodeNm: String?, // 물주기 여름 코드
    @PropertyElement
    val watercycleAutumnCodeNm: String?, // 물주기 가을 코드
    @PropertyElement
    val watercycleWinterCodeNm: String?, // 물주기 겨울 코드
) : Serializable

