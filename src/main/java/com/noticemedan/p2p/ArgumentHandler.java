package com.noticemedan.p2p;

import java.util.regex.Pattern;

public class ArgumentHandler {

    public static CommandType handle(String[] args){
        String argumentString = String.join(" ", args);


        //PUT GET NEEDS IP
        Pattern createNew = Pattern.compile("(?<createNew>[0-9]+){1}", Pattern.MULTILINE);
        Pattern createNode = Pattern.compile("^(?<createNode>[0-9]+ [0-9\\.]+ [0-9]+)$", Pattern.MULTILINE);
        Pattern put = Pattern.compile("(?<put>put [0-9]+ [a-zA-ZæøåÆØÅ]+ [0-9\\.]+ [0-9]+){1}", Pattern.MULTILINE);
        Pattern get = Pattern.compile("(?<get>get [0-9]+ [0-9\\.]+ [0-9]+){1}", Pattern.MULTILINE);


        if(createNew.matcher(argumentString).matches())
            return CommandType.CREATE_FIRST;
        if(createNode.matcher(argumentString).matches())
            return CommandType.CREATE_NODE;
        if(put.matcher(argumentString).matches())
            return CommandType.PUT;
        if(get.matcher(argumentString).matches())
            return CommandType.GET;
        else return null;
    }
}
