package com.luv2code.springbootlibrary.utils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ExtractJWT {

    // static function (can be called without object) to extract userEmail from "sub" in JWT
    public static String payloadJWTExtraction(String token) {

        // this is a Bearer token --> it has "Bearer " as prefix --> need to exclude the "Bearer " part to get only the JWT
        token.replace("Bearer ", "");

        // split the JWT into chunks, (split by ".") (index: 0 (header), 1 (payload), 2 (signature))
        String[] chunks = token.split("\\.");

        // create a "decoder" object to decode the JWT elements (because JWT is Base64 ENCODED)
        Base64.Decoder decoder = Base64.getUrlDecoder();

        // because we just want the "payload" --> just need to decode the "payload" (chunks[1])
        String payload = new String(decoder.decode(chunks[1]));

        // split the decoded payload into multiple parts (split by ",")
        String[] entries = payload.split(",");

        // create a Map (key: string, value: string)
        Map<String, String> map = new HashMap<String, String>();

        // loop through each entry in the entries
        // for each entry, split it by ":" --> in each split/entry, index 0 (key), index 1 (value) of
        for (String entry : entries) {
            String[] keyValue = entry.split(":");

            // check if there's an entry whose "key" == "sub" (this will hold the userEmail)
            if (keyValue[0].equals("\"sub\"")) {

                // refer to 1 thing that needs to be removed from the "value" that contains userEmail, which is the ' " ' prefixed the userEmail
                int remove = 1;
                // if there's a "}" at the end of the "value" --> remove it also --> there are 2 things that need to be removed to get EXACTLY the userEmail
                if (keyValue[1].endsWith("}")) {
                    remove = 2;
                }
                // extract the first " , to the end of the EXACT userEmail
                keyValue[1] = keyValue[1].substring(0, keyValue[1].length() - remove);
                keyValue[1] = keyValue[1].substring(1); // now, also remove the first " from the above result --> we will get the exact userEmail

                // assign to a "map" to get in the key-value pair form
                map.put(keyValue[0], keyValue[1]);


            }
        }
        // if map contains key == "sub" --> take its value, which is the EXACT userEmail
        if(map.containsKey("\"sub\"")) {
            return map.get("\"sub\"");
        }
        return null;
    }
}
