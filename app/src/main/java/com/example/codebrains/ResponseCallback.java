package com.example.codebrains;

public interface ResponseCallback {

    void onResponse(String response);

    void onError(Throwable throwable);
}
