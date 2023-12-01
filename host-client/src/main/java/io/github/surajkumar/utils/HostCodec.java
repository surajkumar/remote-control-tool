package io.github.surajkumar.utils;

import java.util.HashMap;
import java.util.Map;

public class HostCodec {
    private static final String HOST_PATTERN = "^[0-9.]+$";
    private static final Map<Integer, String> ALPHABET = new HashMap<>();
    static {
        int index = 0;
        ALPHABET.put(index++, "a");
        ALPHABET.put(index++, "b");
        ALPHABET.put(index++, "c");
        ALPHABET.put(index++, "d");
        ALPHABET.put(index++, "e");
        ALPHABET.put(index++, "f");
        ALPHABET.put(index++, "g");
        ALPHABET.put(index++, "h");
        ALPHABET.put(index++, "i");
        ALPHABET.put(index++, "j");
        // DOT
        ALPHABET.put(index++, "q");
        ALPHABET.put(index++, "r");
        ALPHABET.put(index++, "s");
        ALPHABET.put(index++, "t");
        ALPHABET.put(index++, "u");
        ALPHABET.put(index++, "v");
        ALPHABET.put(index++, "w");
        ALPHABET.put(index++, "x");
        ALPHABET.put(index++, "y");
        ALPHABET.put(index, "z");
    }

    public static String encode(String address, int port) {
        if(!address.matches(HOST_PATTERN)) {
            if(address.equals("localhost")) {
                return address;
            }
            throw new IllegalArgumentException("Invalid address format: Must only contains numbers or dots");
        }
        String addressAndPort = address + "#" +port;
        StringBuilder sb = new StringBuilder();
        int previous = 0;
        int index = 0;
        for(String digit : addressAndPort.split("")) {
            if(digit.equals("#")) {
                sb.append("-");
                continue;
            }
            if(index != 0 && index % 4 == 0) {
                sb.append("-");
            }
            if(digit.equals(".")) {
                int idx = (ALPHABET.size() - 1) - previous;
                String dotValue = ALPHABET.get(idx);
                sb.append(dotValue);
                previous = 0;
            } else {
                int number = Integer.parseInt(digit);
                sb.append(ALPHABET.get(number));
                previous = number;
            }
            index++;
        }
        return sb.toString();
    }

    public static String decode(String encodedAddress) {
        if(encodedAddress.equals("localhost")) {
            return encodedAddress;
        }
        StringBuilder sb = new StringBuilder();
        for(String s : encodedAddress.split("")) {
            if(s.equals("-")) {
                continue;
            }
            for (Map.Entry<Integer, String> entry : ALPHABET.entrySet()) {
                int k = entry.getKey();
                String v = entry.getValue();
                if(s.equals(v)) {
                    if(k >= 10) {
                        sb.append(".");
                    } else {
                        sb.append(k);
                    }
                    break;
                }
            }
        }
        return sb.toString();
    }
}
