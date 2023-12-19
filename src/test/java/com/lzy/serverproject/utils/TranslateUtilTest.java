package com.lzy.serverproject.utils;

import com.lzy.serverproject.utils.translate.TranslateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TranslateUtilTest {

    @Test
    public void test(){
        String title1 = "大韓航空、札幌－ソウル1/13増便　1日2往復に(Aviation Wire)";
        String content1 = "大韓航空（KAL/KE）は12月19日、ソウル（仁川）－札幌（新千歳）線を2024年1月13日に増便すると発表した。増便後は1日2往復（週14往復）運航する。 　同路線は現在、1日1往復（週7往復）を含む週11往復で、2便目のKE769/770便を増便。火曜、水曜、金曜、日曜の週4往復を毎日運航に増便する。1月29日は運航しない。 　増便分の運航スケジュールは、札幌行きKE769便がソウルを午後......";
        String title2 = "年金の繰下げ受給が「老後対策の定番」にならない当たり前の理由(LIMO)";
        String content2 = "「年金を多くもらうには、年金の繰下げ受給がおすすめ」と聞いたことがある方は、結構多いのではないでしょうか。 【繰下げ受給の一覧表】利用者は1.2％だけ。繰下げが「老後対策の定番」にならない理由は？ 年金の繰下げ受給とは、原則65歳から受給開始となる年金を受け取らずに、開始を遅らせる（繰下げる）ことで、もらえる年金を増やす制度です。 1か月繰下げすれば年金が0.7％増えます。 仮に70歳まで繰下げる......";
        String content = title1+"\n"+content1+"\n"+title2+"\n"+content2;
        TranslateUtil.getTranslateResult(content);
    }
}
