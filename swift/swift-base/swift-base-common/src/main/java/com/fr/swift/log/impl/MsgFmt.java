package com.fr.swift.log.impl;

import com.fr.swift.structure.Pair;
import com.fr.swift.util.Optional;
import com.fr.swift.util.Strings;
import com.fr.swift.util.Util;


/**
 * @author anchore
 * @date 2018/12/30
 */
class MsgFmt {

    static Pair<String, Optional<Throwable>> fmt(String msg, Object... args) {
        if (Util.isEmpty(args)) {
            return Pair.of(msg, Optional.<Throwable>empty());
        }

        boolean hasThrowable = false;
        if (args[args.length - 1] instanceof Throwable) {
            hasThrowable = true;
        }

        if (msg == null) {
            msg = Strings.EMPTY;
        }

        StringBuilder sb = new StringBuilder(msg.length());
        int head = 0;
        for (int indexOfBrace, argI = 0; (indexOfBrace = msg.indexOf("{}", head)) != -1; ) {
            for (int i = head; i < indexOfBrace; i++) {
                sb.append(msg.charAt(i));
            }
            if (argI < args.length) {
                sb.append(args[argI++]);
            } else {
                sb.append("{}");
            }
            head = indexOfBrace + 2;
        }

        if (head < msg.length()) {
            for (int i = head; i < msg.length(); i++) {
                sb.append(msg.charAt(i));
            }
        }

        return Pair.of(sb.toString(), hasThrowable ? Optional.of((Throwable) args[args.length - 1]) : Optional.<Throwable>empty());
    }
}