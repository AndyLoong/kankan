// ITestDemoCardCallback.aidl
package com.gionee.demo.launcher.yourpage.aidl;

// Declare any non-default types here with import statements

interface ITestDemoCardCallback {

    void notifyRefesh();

    void onGetDataSuccess(in String data);

    void onGetDataFialed();
}
