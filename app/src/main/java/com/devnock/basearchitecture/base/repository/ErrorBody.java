package com.devnock.basearchitecture.base.repository;

import com.devnock.basearchitecture.utils.entity.ServerErrorEntity;

public class ErrorBody {

    private Throwable throwable;
    private ServerErrorEntity serverErrorEntity;

    public ErrorBody(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public ServerErrorEntity getServerErrorEntity() {
        return serverErrorEntity;
    }

    public void setServerErrorEntity(ServerErrorEntity serverErrorEntity) {
        this.serverErrorEntity = serverErrorEntity;
    }
}
