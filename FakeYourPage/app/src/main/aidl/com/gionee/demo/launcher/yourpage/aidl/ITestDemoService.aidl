// ITestDemoService.aidl
package com.gionee.demo.launcher.yourpage.aidl;

// Declare any non-default types here with import statements
import com.gionee.demo.launcher.yourpage.aidl.ITestDemoCardCallback;

interface ITestDemoService {

    void refreshTestDemoCard();

    void clickItem(in String url);

    void clickMore();

    void registerTestDemoCardCallback(ITestDemoCardCallback callback);

    void unRegisterTestDemoCardCallback(ITestDemoCardCallback callback);
}

