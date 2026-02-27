package com.oceanview.resort.utilTest;

import com.oceanview.resort.util.HtmlUtil;
import org.junit.Assert;
import org.junit.Test;

public class HtmlUtilTest {

    @Test
    public void escape_null_returnsEmptyString() {
        Assert.assertEquals("", HtmlUtil.escape(null));
    }

    @Test
    public void escape_plainText_unchanged() {
        Assert.assertEquals("Hello World", HtmlUtil.escape("Hello World"));
    }

    @Test
    public void escape_specialCharacters_areEscaped() {
        String input = "<script>alert(\"xss\") & 'test'</script>";
        String escaped = HtmlUtil.escape(input);

        Assert.assertEquals("&lt;script&gt;alert(&quot;xss&quot;) &amp; &#39;test&#39;&lt;/script&gt;", escaped);
    }
}

