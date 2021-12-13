package com.example.demo;

import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Configuration
@SpringBootApplication
@Controller
public class Mappings{

    @GetMapping(value = "/")
    public String redirect(){
        return "redirect:/Agents";
    }

    @GetMapping("/Agents")
    public String getAll(Model model) throws IOException {
        NewsInterface.getData();
        HashSet<String> uniqueRegions= new HashSet<>();
        for(Agent agent:NewsInterface.users){
            uniqueRegions.add(agent.region);
        }
        model.addAttribute("database",uniqueRegions);

        return "content-list.html";
    }

    @GetMapping("/Agents/new")
    public String add(Model model){
        //allows entry of data
        model.addAttribute(new Agent());
        return "add-agent.html";
    }

    @PostMapping("/Agents")
    public String submit(@ModelAttribute Agent agent) throws IOException {
        return "redirect:/Agents";
    }

    @PostMapping(value = "/Agents/region")
    String regionList(@RequestParam(value="location") String location, Model model){
        if(!location.equals("Select Region")) {
            ArrayList<Agent> regionalAgents = new ArrayList<>();
            for (Agent agent : NewsInterface.users) {
                if (agent.region.equals(location)) {
                    regionalAgents.add(agent);
                }
            }
            model.addAttribute("location", location);
            model.addAttribute("database", regionalAgents);
            return "agent-list";
        }
        else return "redirect:/Agents";
    }

    @RequestMapping(value = "/Agents/dashboard")
    String dashboard(Model model){
        model.addAttribute("location", "All Locations");
        model.addAttribute("database", NewsInterface.users);
        return "agent-list";
    }

    @GetMapping(value = "images")
    public ResponseEntity<InputStreamResource> uploadFile(InputStream imgFile){
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(imgFile));
    }

    @PostMapping(value = "/Agents/new")
    public String newAgent(@ModelAttribute Agent agent, ModelMap modelMap) throws IOException {
        JSONObject result = NewsInterface.sendData(agent);
        System.out.println(result);
        if(result.has("error") && result.get("error") != null){
            String errorMessage = result.getString("error");
            if(errorMessage.contains("username")){
                errorMessage = errorMessage.replaceAll("username","telephone number");
            }
            if(errorMessage.contains("country_id")){
                errorMessage = errorMessage.replaceAll("country_id","region");
            }
            modelMap.addAttribute("message", errorMessage);
            return "add-agent";
        }
        if(!agent.getImage().getOriginalFilename().equals("") && agent.getImage().getOriginalFilename() != null) {
            agent.pic = agent.getImage().getBytes();
            System.out.println(agent.getImage().getOriginalFilename());
            Path path = Paths.get("./build/classes/java/main/com/example/demo/" + agent.getPromoCode() + ".png");
            System.out.println("image added for "+agent.getPromoCode());
            Files.write(path, agent.pic);
        }
//        NewsInterface.addData(agent);
        return "redirect:/Agents";
    }

    @GetMapping(value = "/images/{ID}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String ID) throws IOException {
        var imgFile = new ClassPathResource("/com/example/demo/"+ID+".png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(imgFile.getInputStream()));
    }

    @PostMapping(value = "/Agents/search")
    String search(@RequestParam(value="SearchTerm") String SearchTerm, Model model){
        ArrayList<Agent> searchNames = new ArrayList<>();
        ArrayList<Agent> searchPhones = new ArrayList<>();
        ArrayList<Agent> searchEmails = new ArrayList<>();
        ArrayList<Agent> searchPromos = new ArrayList<>();
        SearchTerm = SearchTerm.toLowerCase(Locale.ROOT);
        if(SearchTerm.equals("")){
            model.addAttribute("location", "All Locations");
            model.addAttribute("database", NewsInterface.users);
            return "agent-list";
        }
        for(Agent agent: NewsInterface.users){
            if(agent.name.toLowerCase(Locale.ROOT).contains(SearchTerm)){
                searchNames.add(agent);
            }
            if(agent.telephoneNumber.toLowerCase(Locale.ROOT).contains(SearchTerm)){
                searchPhones.add(agent);
            }
            if(agent.email.toLowerCase(Locale.ROOT).contains(SearchTerm)){
                searchEmails.add(agent);
            }
            if(agent.promoCode.toLowerCase(Locale.ROOT).contains(SearchTerm)){
                searchPromos.add(agent);
            }
        }
        model.addAttribute("SearchNames",searchNames);
        model.addAttribute("SearchPhones",searchPhones);
        model.addAttribute("SearchEmails",searchEmails);
        model.addAttribute("SearchPromos",searchPromos);
        return "agent-search";
    }

    @RequestMapping(value = "/Agents/{PromoCode}")
    String get(@ModelAttribute Agent agent, Model model) throws IOException {
        Agent temp = null;
        for(Agent a : NewsInterface.users){
            if (a.getPromoCode().equals(agent.getPromoCode())){
                temp = a;
            }
        }
        if(temp != null) {
            //var f = new ClassPathResource("/com/example/demo/"+temp.promoCode+".png");
//            File f = imgFile.getFile();
            String s = getResourceFiles("/com/example/demo").toString();
            System.out.println(s);

            if(s.toLowerCase(Locale.ROOT).contains((temp.promoCode+".png").toLowerCase(Locale.ROOT))){
                System.out.println("contains");
            try{
//                Image i =ImageIO.read((ImageInputStream) f);
//                temp.setImage((MultipartFile) i);
                temp.hasPic=true;
            }
            catch (Exception e){
                System.out.println(e);
            }
            //System.out.println(getResourceFiles("").toString());
           // if(f.exists()) {
               // System.out.println(temp.promoCode +".png exists");

            }
          //  else System.out.println("image does not exist");
            model.addAttribute("agent", temp);
            return "display-agent.html";
        }
        else{
            return "redirect:/Agents";
        }
    }


    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
