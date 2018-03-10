package br.com.maven.desafio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Java Client for the Marvel Rest API
 */
public class Personagem {

	private int id;
	private String name;
	private String description;
	private String thumbnailURL;
	private String[] urls;
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setThumbnailURL(String thumbnailURL)
	{
		this.thumbnailURL = thumbnailURL;
	}
	
	public String getThumbnailURL()
	{
		return thumbnailURL;
	}
	
	public void setUrls(String[] urls)
	{
		this.urls = urls;
	}
	
	
 /**
  * Call a Rest from Marvel just for test
  */
 public static void main(String[] args) {
  HttpURLConnection conn = null;
  try {
   URL url = new URL(Personagem.mountRestUrl());

   conn = (HttpURLConnection) url.openConnection();
   conn.setRequestMethod("GET");
   conn.setRequestProperty("Accept", "application/json");

   if (conn.getResponseCode() != 200) {
    throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
   }

   BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

   String output = null; // inicia vazio
   String retornoRest = "";
   char ultimo;
   int tamanho;
   while ((output = br.readLine()) != null) {
	   retornoRest += output;
	   tamanho = retornoRest.length();
	   ultimo = output.charAt(tamanho-1);
	   if(ultimo == ',')
	   {
		   output += "\r\n";
	   }
   }
   System.out.println("\nRest Return: " + retornoRest);

   Personagem.mountImageUrl(retornoRest);
  } catch (MalformedURLException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  } finally {
   conn.disconnect();
  }

 }


 private static String mountRestUrl() {
  Long timeStamp = new Date().getTime();
  String privateKey = "57dad7d1088a518c397deedfdc39c9be4f208905";
  String apikey = "5f4723aaa3d2ff38e93764818a3f027e";
  String hash = Personagem.generateHash(timeStamp + privateKey + apikey);

  //String urlCompleta = "http://gateway.marvel.com/v1/public/comics/12?ts=" + timeStamp + "&apikey=" + apikey + "&hash=" + hash;
  String urlCompleta = "https://gateway.marvel.com:443/v1/public/characters/1009189?ts=" + timeStamp + "&apikey=" + apikey + "&hash=" + hash;
  System.out.println("Rest URL: " + urlCompleta);

  return urlCompleta;
 }


 private static String generateHash(String string) {
  MessageDigest md = null;
  try {
   md = MessageDigest.getInstance("MD5");
  } catch (NoSuchAlgorithmException e) {
   e.printStackTrace();
  }
  md.update(string.getBytes());
  byte[] bytes = md.digest();

  StringBuilder s = new StringBuilder();
  for (int i = 0; i < bytes.length; i++) {
   int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
   int parteBaixa = bytes[i] & 0xf;
   if (parteAlta == 0) {
    s.append('0');
   }
   s.append(Integer.toHexString(parteAlta | parteBaixa));
  }
  return s.toString();
 }


 private static void mountImageUrl(String total) {
  int inicio = total.lastIndexOf("\"thumbnail\":{\"path\":\"");
  int fim = total.indexOf("\",\"extension\":\"");
  String ss = total.substring(inicio, fim).substring(21) + "/portrait_xlarge.jpg";

  System.out.println("\nUse this URL to see the image returned by Rest: " + ss);
 }
}
