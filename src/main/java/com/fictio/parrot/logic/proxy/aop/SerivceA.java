package com.fictio.parrot.logic.proxy.aop;

import com.fictio.parrot.logic.annotation.SimpleInject;

public class SerivceA {
    
    @SimpleInject
    private SerivceB serviceB;
    
    public void callB() {
        
    }

}
