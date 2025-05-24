package com.kh.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kh.configs.CustomEnvironment;
import com.kh.configs.PartnerInfo;
import com.kh.shared.exception.MoMoException;
import com.kh.shared.utils.Execute;

/**
 * @author hainguyen
 * Documention: https://developers.momo.vn
 */

public abstract class AbstractProcess<T, V> {

    protected PartnerInfo partnerInfo;
    protected CustomEnvironment environment;
    protected Execute execute = new Execute();

    public AbstractProcess(CustomEnvironment environment) {
        this.environment = environment;
        this.partnerInfo = environment.getPartnerInfo();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }

    public abstract V execute(T request) throws MoMoException;
}
