package com.nghianguyenit.talk2everyone;

import io.github.firemaples.language.Language;
import io.github.firemaples.translate.Translate;

/**
 * Created by NghiaNH on 4/22/2017.
 */

public class Test {

    public static void main(String... args) throws Exception {
        // Set your Windows Azure Marketplace client info - See http://msdn.microsoft.com/en-us/library/hh454950.aspx
        Translate.setSubscriptionKey("508b1fec83fd47018f5dfafcf1c9d3e9");

        String translatedText = Translate.execute("Bonjour le monde", Language.FRENCH, Language.ENGLISH);

        System.out.println(translatedText);


    }
}
