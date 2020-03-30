package ru.progwards.java2.lessons.tests.test.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.progwards.java2.lessons.tests.app.model.Account;
import ru.progwards.java2.lessons.tests.app.service.impl.StoreServiceImpl;

public class StoreServiceImplTest {
    StoreServiceImpl storeService;

    @Before
    public void init() {
        storeService = new StoreServiceImpl();
    }

    @Test(expected = RuntimeException.class)
    public void getByIdTest(){
        Account actual = storeService.get("asd");
    }
}
