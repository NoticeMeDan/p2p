package com.noticemedan.p2p;

import java.util.regex.Pattern;

public class ArgumentHandler {

    public static CommandType handle(String[] args){
        if(args.length == 0)
            return CommandType.UNKNOWN;

        String argumentString = String.join(" ", args);

        Pattern createNew = Pattern.compile("(?<createNew>[0-9]+){1}", Pattern.MULTILINE);
        Pattern createNode = Pattern.compile("^(?<createNode>[0-9]+ [0-9\\.]+ [0-9]+)$", Pattern.MULTILINE);
        Pattern put = Pattern.compile("(?<put>[0-9]+ put [0-9]+ [a-zA-ZæøåÆØÅ]+ [0-9\\.]+ [0-9]+){1}", Pattern.MULTILINE);
        Pattern get = Pattern.compile("(?<get>[0-9]+ get [0-9]+ [0-9\\.]+ [0-9]+){1}", Pattern.MULTILINE);

        if(createNew.matcher(argumentString).matches())
            return CommandType.CREATE_NETWORK;
        if(createNode.matcher(argumentString).matches())
            return CommandType.CREATE_NODE;
        if(put.matcher(argumentString).matches())
            return CommandType.PUT;
        if(get.matcher(argumentString).matches())
            return CommandType.GET;
        else return CommandType.UNKNOWN;
    }
}
