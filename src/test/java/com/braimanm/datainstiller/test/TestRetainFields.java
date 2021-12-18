package com.braimanm.datainstiller.test;

import com.braimanm.datainstiller.data.RetainData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestRetainFields {

    private void assertAll(RetainData data) {
        Assert.assertEquals(data.b, data.bb);
        Assert.assertEquals(data.sh, data.shsh);
        Assert.assertEquals(data.i, data.ii);
        Assert.assertEquals(data.l, data.ll);
        Assert.assertEquals(data.f, data.ff);
        Assert.assertEquals(data.d, data.dd);
        Assert.assertEquals(data.c, data.cc);
        Assert.assertEquals(data.b, data.bb);
        Assert.assertEquals(data.bo, data.bobo);
        Assert.assertEquals(data.s, data.ss);
        Assert.assertEquals(data.bskip,(byte) 45);
        Assert.assertEquals(RetainData.bstat,(byte) 55);
        Assert.assertEquals(RetainData.bstatfinal,(byte) 65);
    }

    @Test
    public void testRetainData() {
        RetainData data = new RetainData().fromResource("retain-data.xml");
        assertAll(data);
    }

    @Test
    public void testRetainData2() {
        RetainData data = new RetainData();
        data.b = 10;
        data.bb = 10;
        data.s ="New String";
        data.ss = "New String";
        data = data.fromResource("retain-data.xml");
        assertAll(data);
    }

}
