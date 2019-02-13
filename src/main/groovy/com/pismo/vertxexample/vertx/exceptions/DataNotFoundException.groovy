package com.pismo.vertxexample.vertx.exceptions

class DataNotFoundException extends Exception {

    DataNotFoundException() {
        super('Data not found')
    }
}
