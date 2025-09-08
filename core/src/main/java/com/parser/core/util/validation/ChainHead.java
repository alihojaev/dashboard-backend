package com.parser.core.util.validation;

public class ChainHead implements ChainValidator {

    ChainElement first;

    @Override
    public ChainElement then(Validatable then) {
        return this.first = new ChainElement(this, then);
    }
}
