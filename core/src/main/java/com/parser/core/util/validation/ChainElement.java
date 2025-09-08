package com.parser.core.util.validation;

public class ChainElement implements ChainValidator, Validatable {

    private final Validatable then;
    private ChainHead head;
    private ChainElement child;

    private static String validate(ChainElement validator) {
        var error = validator.then.validateMessage();
        if (error == null && validator.child != null) return validate(validator.child);
        return error;
    }

    ChainElement(ChainHead head, Validatable then) {
        this.head = head;
        this.then = then;
    }

    @Override
    public ChainElement then(Validatable then) {
        var validator = new ChainElement(head, then);
        this.head = null;
        this.child = validator;
        return validator;
    }

    @Override
    public String validateMessage() {
        return validate(head.first);
    }
}
