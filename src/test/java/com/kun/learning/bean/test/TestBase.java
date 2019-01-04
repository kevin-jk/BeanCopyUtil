package com.kun.learning.bean.test;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by jrjiakun on 2018/12/27
 */
@RunWith(JUnit4.class)
public class TestBase {
    @Before
    public void before(){
        System.out.println("================Before================");
    }
    @After
    public void after(){
        System.out.println("================After================");
    }
}
