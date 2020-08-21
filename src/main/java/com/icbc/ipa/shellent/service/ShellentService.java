package com.icbc.ipa.shellent.service;


public interface ShellentService {
    void intoDatabase(String sshelent);

    void intoDatabaseByStream(String sshelent);

    void mergeShellentData();
}
