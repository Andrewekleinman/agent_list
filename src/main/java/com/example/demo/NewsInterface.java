package com.example.demo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@Configuration
@SpringBootApplication
@RestController
public class NewsInterface {

	//private static long initTime = System.currentTimeMillis();
	public static ArrayList<Agent> users = new ArrayList<>();
	//public static ArrayList<Agent> additionalUsers = new ArrayList<>();
	//public static MultipartFile file;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(NewsInterface.class, args);
	}

	public static void getData() throws IOException {
		//get token
		String command =
				"curl voip.ml:2432/login -X POST -d {\"username\":\"admin\",\"password\":\"f7pwMk01sYxRN2dR\"} -s | jq";
		ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
		JSONObject jsonObject = process(processBuilder.start());
		String token = (jsonObject.getJSONObject("data").getString("token"));

		//use token to populate array of agents
		processBuilder.command(
				new String[]{"curl", "voip.ml:2432/users", "-H", "Authorization: Bearer "+token});
		jsonObject = process(processBuilder.start());
		JSONArray arr = jsonObject.getJSONObject("data").getJSONArray("result");
		ArrayList<Agent>  users= new ArrayList<>();
		for(int i = 0; i< arr.length();i++){
			JSONObject obj = (JSONObject) arr.get(i);
			//byte[] image = null;
			Agent thisAgent = new Agent(null,obj.getString("FirstName"),null,obj.getString("PromoCode"),obj.getString("Username"),obj.getString("CountryId"),obj.getString("Email"));
			users.add(thisAgent);
		}
//		users.addAll(additionalUsers);
		NewsInterface.users = users;
	}

	public static JSONObject process(Process process) throws IOException {
		InputStream inputStream = process.getInputStream();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);
		return new JSONObject(responseStrBuilder.toString());
	}

	public static JSONObject sendData(Agent agent) throws IOException {
		String command =
				"curl voip.ml:2432/login -X POST -d {\"username\":\"admin\",\"password\":\"f7pwMk01sYxRN2dR\"} -s | jq";
		ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
		JSONObject jsonObject = process(processBuilder.start());
		String token = (jsonObject.getJSONObject("data").getString("token"));

		processBuilder.command(
				new String[]{"curl", "voip.ml:2432/users", "-X", "POST", "-d", "{\"CountryId\":\""+agent.getRegion()+"\",\"Status\":\"active\",\"Email\":\""+agent.getEmail()+"\",\"PromoCode\":\""+agent.getPromoCode()+"\",\"FirstName\":\""+agent.getName()+"\",\"Role\":\"agent\",\"Username\":\""+ agent.getTelephoneNumber()+"\",\"ID\":25,\"LastName\":\""+agent.getName()+"\",\"Password\":\"213ThisUserwasMadeWithoutAPassword4\",\"Image\":\""+"yes"+"\"}", "-H", "Authorization: Bearer "+token, "-s", "|", "jq"});
		jsonObject = process(processBuilder.start());
		return (jsonObject);
	}
}
